/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.ValidRemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl.ValidRemoteBeanImpl;
import de.bund.bva.isyfact.sicherheit.annotation.AnnotationSicherheitAttributeSource;
import de.bund.bva.isyfact.sicherheit.annotation.GesichertInterceptor;
import de.bund.bva.isyfact.sicherheit.annotation.SicherheitAttributeSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class TestServiceFactoryBean {

	private ValidRemoteBean remote;

	private GesichertInterceptor interceptor1;
	private ServiceExceptionFassade interceptor2;

	private ServiceFactoryBean bean;
	
	private Map<String, String[]> rechte;

	@Before
	public void setUp() {
		remote = new ValidRemoteBeanImpl();
		interceptor1 = mock(GesichertInterceptor.class);
		interceptor2 = mock(ServiceExceptionFassade.class);
		rechte = new HashMap<String, String[]>();
		rechte.put("rolle1", new String[]{"rechtA","rechtB"});
		rechte.put("rolle2", new String[]{"rechtC","rechtD"});
		bean = new ServiceFactoryBean();
		bean.setRemoteBeanInterface(ValidRemoteBean.class);
		bean.setPreInterceptors(new Object[]{interceptor1});
		bean.setPostInterceptors(new Object[]{interceptor2});
		bean.setTarget(remote);
		bean.setValidateConfiguration(false);
		bean.setBenoetigtesRecht(rechte);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetRemoteBeanInterfaceParamIsNotInterface() {
		bean.setRemoteBeanInterface(ValidRemoteBeanImpl.class);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testAfterPropertiesSetRechteMapNull(){
		bean.setBenoetigtesRecht(null);
		bean.afterPropertiesSet();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testAfterPropertiesSetRechteMapLeer(){
		rechte.clear();
		bean.setBenoetigtesRecht(rechte);
		bean.afterPropertiesSet();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAfterPropertiesSetKeineMethodMapInGesichertInterface(){
		SicherheitAttributeSource source = mock(AnnotationSicherheitAttributeSource.class);
		Mockito.when(interceptor1.getSicherheitAttributeSource()).thenReturn(source);
		bean.afterPropertiesSet();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testAfterPropertiesSetOhneValidierung(){
		SicherheitAttributeSource source = mock(MethodMapSicherheitAttributeSource.class);
		Mockito.when(interceptor1.getSicherheitAttributeSource()).thenReturn(source);
		bean.afterPropertiesSet();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAfterPropertiesSetFalscherRechteKey(){
		rechte.put("falscher.key", new String[]{"rechtE", "rechtF"});
		SicherheitAttributeSource source = mock(MethodMapSicherheitAttributeSource.class);
		Mockito.when(interceptor1.getSicherheitAttributeSource()).thenReturn(source);
		bean.afterPropertiesSet();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testAfterPropertiesSetMitValidierung(){
		bean.setValidateConfiguration(true);
		SicherheitAttributeSource source = mock(MethodMapSicherheitAttributeSource.class);
		Mockito.when(interceptor1.getSicherheitAttributeSource()).thenReturn(source);
		bean.afterPropertiesSet();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAfterPropertiesSetIncorrectSicherheitAttributeSource() {
		SicherheitAttributeSource incorrectSource = mock(AnnotationSicherheitAttributeSource.class);
		Mockito.when(interceptor1.getSicherheitAttributeSource()).thenReturn(incorrectSource);
		bean.setPreInterceptors(new Object[]{interceptor1});

		Map<String, String[]> testRechte = new HashMap<>();
		testRechte.put("someMethod", new String[]{"someRight"});
		bean.setBenoetigtesRecht(testRechte);

		bean.afterPropertiesSet();
	}

}
