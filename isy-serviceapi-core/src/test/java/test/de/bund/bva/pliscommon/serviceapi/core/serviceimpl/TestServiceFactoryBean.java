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
package test.de.bund.bva.pliscommon.serviceapi.core.serviceimpl;

import java.util.HashMap;
import java.util.Map;

import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.MethodMapSicherheitAttributeSource;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.ServiceExceptionFassade;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.ServiceFactoryBean;
import de.bund.bva.pliscommon.sicherheit.annotation.AnnotationSicherheitAttributeSource;
import de.bund.bva.pliscommon.sicherheit.annotation.GesichertInterceptor;
import de.bund.bva.pliscommon.sicherheit.annotation.SicherheitAttributeSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import test.de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.ValidRemoteBean;
import test.de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl.ValidRemoteBeanImpl;

public class TestServiceFactoryBean {

	private ValidRemoteBean remote;

	private GesichertInterceptor interceptor1;
	private ServiceExceptionFassade interceptor2;
	
	private ServiceFactoryBean bean;
	
	private Map<String, String[]> rechte;
	
	@Before
	public void setUp() throws Exception {
		remote = new ValidRemoteBeanImpl();
		interceptor1 = Mockito.mock(GesichertInterceptor.class);
		interceptor2 = Mockito.mock(ServiceExceptionFassade.class);
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
		SicherheitAttributeSource source = Mockito.mock(AnnotationSicherheitAttributeSource.class);
		Mockito.when(interceptor1.getSicherheitAttributeSource()).thenReturn(source);
		bean.afterPropertiesSet();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testAfterPropertiesSetOhneValidierung(){
		SicherheitAttributeSource source = Mockito.mock(MethodMapSicherheitAttributeSource.class);
		Mockito.when(interceptor1.getSicherheitAttributeSource()).thenReturn(source);
		bean.afterPropertiesSet();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAfterPropertiesSetFalscherRechteKey(){
		rechte.put("falscher.key", new String[]{"rechtE", "rechtF"});
		SicherheitAttributeSource source = Mockito.mock(MethodMapSicherheitAttributeSource.class);
		Mockito.when(interceptor1.getSicherheitAttributeSource()).thenReturn(source);
		bean.afterPropertiesSet();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testAfterPropertiesSetMitValidierung(){
		bean.setValidateConfiguration(true);
		SicherheitAttributeSource source = Mockito.mock(MethodMapSicherheitAttributeSource.class);
		Mockito.when(interceptor1.getSicherheitAttributeSource()).thenReturn(source);
		bean.afterPropertiesSet();
	}

}
