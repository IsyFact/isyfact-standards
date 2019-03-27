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
package de.bund.bva.pliscommon.serviceapi.core.serviceimpl;

import java.lang.reflect.InvocationTargetException;

import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.RemoteBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.ValidRemoteBean;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl.ValidRemoteBeanImpl;

public class TestServiceImplDelegator {

	private ServiceImplDelegator delegator;
	private ValidRemoteBeanImpl bean;
	private ValidRemoteBean proxy;
	
	@Before
	public void setUp() throws Exception {
		delegator = new ServiceImplDelegator();
		bean = new ValidRemoteBeanImpl();
		ProxyFactory fac = new ProxyFactory(bean);
		fac.addAdvice(delegator);
		proxy = (ValidRemoteBean) fac.getProxy();
	}

	@Test
	public void testInvoke() throws PlisTechnicalToException {
		proxy.eineMethode();
	}
	
	@Test
	public void testValidateConfiguration(){
		delegator.validateConfiguration(ValidRemoteBean.class, bean);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidateConfigurationWrongTargetObject(){
		delegator.validateConfiguration(ValidRemoteBean.class, new Object());
	}
	
	@Test(expected = InvocationTargetException.class)
	public void testInvokeMitException() throws PlisTechnicalToException, NoSuchMethodException, SecurityException, InvocationTargetException {
		RemoteBean bean = new RemoteBeanImpl();
		ProxyFactory fac = new ProxyFactory(bean);
		fac.addAdvice(delegator);
		RemoteBean proxy = (RemoteBean) fac.getProxy();		
		proxy.eineMethodeMitException();
	}

}
