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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.qos.logback.classic.Level;

import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.RemoteBean;
import org.junit.Before;
import org.junit.Test;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.aop.framework.ProxyFactory;

import de.bund.bva.pliscommon.aufrufkontext.common.exception.AufrufKontextFehlerhaftException;
import de.bund.bva.pliscommon.exception.PlisBusinessException;
import de.bund.bva.pliscommon.exception.PlisException;
import de.bund.bva.pliscommon.exception.PlisTechnicalException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.PlisTechnicalRuntimeTestException;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.PlisTechnicalTestToException;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.ValidRemoteBean;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl.ValidRemoteBeanImpl;

public class TestServiceExceptionFassade {

	private ServiceExceptionFassade fassade;
	private AusnahmeIdErmittler ermittler;
	private MethodMappingSource methodMappingSource;
	private ExceptionMappingSource exceptionMappingSource;
	private ValidRemoteBean bean;

	@Before
	public void setUp(){
		fassade = new ServiceExceptionFassade();
		ermittler = mock(AusnahmeIdErmittler.class);
		methodMappingSource = mock(MethodMappingSource.class);
		exceptionMappingSource = mock(ExceptionMappingSource.class);				
		fassade.setAusnahmeIdErmittler(ermittler);
		fassade.setMethodMappingSource(methodMappingSource);
		fassade.setExceptionMappingSource(exceptionMappingSource);
		fassade.setAppTechnicalRuntimeException(PlisTechnicalRuntimeTestException.class);
		fassade.setLogLevelPlisExceptions(Level.DEBUG.levelStr);
		bean = new ValidRemoteBeanImpl();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetAppTechnicalRuntimeExceptionErwarteterKonstruktorNichtVorhanden() {
		fassade.setAppTechnicalRuntimeException(AufrufKontextFehlerhaftException.class);
	}
	
	@Test
	public void testValidateConfiguration() throws NoSuchMethodException, SecurityException{		
		when(exceptionMappingSource.getGenericTechnicalToException((Method) any())).thenAnswer(new Answer<Class<PlisTechnicalToException>>() {
			@Override
			public Class<PlisTechnicalToException> answer(InvocationOnMock invocation) throws Throwable {
				return PlisTechnicalToException.class;
			}
		});		
		when(exceptionMappingSource.getToExceptionClass((Method)any(), (Class)any())).thenAnswer(new Answer<Class<PlisTechnicalException>>() {
			@Override
			public Class<PlisTechnicalException> answer(InvocationOnMock invocation) throws Throwable {				
				return PlisTechnicalException.class;
			}
		});
		
		fassade.setMethodMappingSource(new ReflectiveMethodMappingSource());
		fassade.validateConfiguration(ValidRemoteBean.class, bean);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testValidateConfigurationPlisExceptionClassIsNull() throws NoSuchMethodException, SecurityException{		
		when(exceptionMappingSource.getGenericTechnicalToException((Method) any())).thenAnswer(new Answer<Class<PlisTechnicalToException>>() {
			@Override
			public Class<PlisTechnicalToException> answer(InvocationOnMock invocation) throws Throwable {				
				return PlisTechnicalToException.class;
			}
		});		
		when(exceptionMappingSource.getToExceptionClass((Method)any(), (Class)any())).thenAnswer(new Answer<Class<PlisTechnicalException>>() {
			@Override
			public Class<PlisTechnicalException> answer(InvocationOnMock invocation) throws Throwable {				
				return null;
			}
		});
		
		fassade.setMethodMappingSource(new ReflectiveMethodMappingSource());		
		fassade.validateConfiguration(ValidRemoteBean.class, bean);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testValidateConfigurationPlisExceptionClassIsNotOnMethod() throws NoSuchMethodException, SecurityException{		
		when(exceptionMappingSource.getGenericTechnicalToException((Method) any())).thenAnswer(new Answer<Class<PlisTechnicalToException>>() {
			@Override
			public Class<PlisTechnicalToException> answer(InvocationOnMock invocation) throws Throwable {				
				return PlisTechnicalToException.class;
			}
		});		
		when(exceptionMappingSource.getToExceptionClass((Method)any(), (Class<? extends PlisException>)any())).thenAnswer(new Answer<Class<PlisBusinessException>>() {
			@Override
			public Class<PlisBusinessException> answer(InvocationOnMock invocation) throws Throwable {				
				return PlisBusinessException.class;
			}
		});
		
		fassade.setMethodMappingSource(new ReflectiveMethodMappingSource());		
		fassade.validateConfiguration(ValidRemoteBean.class, bean);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testValidateConfigurationNoStaticConfigOfGenericToException(){
		when(exceptionMappingSource.getGenericTechnicalToException((Method) any())).thenReturn(null);		
		fassade.validateConfiguration(ValidRemoteBean.class, new ValidRemoteBeanImpl());
	}

	@Test(expected = IllegalStateException.class)
	public void testValidateConfigurationNoPlisTechnicalToExceptionOnMethod(){
		when(exceptionMappingSource.getGenericTechnicalToException((Method) any())).thenAnswer(new Answer<Class<PlisTechnicalToException>>() {
			@Override
			public Class<PlisTechnicalToException> answer(InvocationOnMock invocation) throws Throwable {				
				return PlisTechnicalToException.class;
			}
		});
		fassade.setMethodMappingSource(new ReflectiveMethodMappingSource());
		fassade.validateConfiguration(RemoteBean.class, new RemoteBeanImpl());
	}
	
	@Test
	public void testInvoke() throws Throwable{
		ProxyFactory fac = new ProxyFactory(bean);
		fac.addAdvice(fassade);
		ValidRemoteBean proxy = (ValidRemoteBean) fac.getProxy();
		proxy.eineMethode();
	}
	
	private RemoteBean getProxyForRemoteBean(){
		RemoteBean bean = new RemoteBeanImpl();
		ProxyFactory fac = new ProxyFactory(bean);
		fac.addAdvice(fassade);
		return (RemoteBean) fac.getProxy();
	}
	
	@Test(expected = PlisTechnicalTestToException.class)
	public void testInvokePlisException() throws Throwable{
		when(exceptionMappingSource.getToExceptionClass((Method) any(), (Class<? extends PlisException>) any())).thenAnswer(new Answer<Class<PlisTechnicalTestToException>>() {
			@Override
			public Class<PlisTechnicalTestToException> answer(InvocationOnMock invocation) throws Throwable {				
				return PlisTechnicalTestToException.class;
			}
		});
		RemoteBean proxy = getProxyForRemoteBean();
		proxy.eineMethodeMitPlisException();
	}
	
	@Test(expected = PlisTechnicalTestToException.class)
	public void testInvokePlisException2() throws Throwable{
		when(exceptionMappingSource.getGenericTechnicalToException((Method) any())).thenAnswer(new Answer<Class<PlisTechnicalTestToException>>() {
			@Override
			public Class<PlisTechnicalTestToException> answer(InvocationOnMock invocation) throws Throwable {				
				return PlisTechnicalTestToException.class;
			}
		});
		RemoteBean proxy = getProxyForRemoteBean();
		proxy.eineMethodeMitPlisException();
	}
	
	@Test(expected = PlisTechnicalTestToException.class)
	public void testInvokePlisTechnicalRuntimeException() throws Throwable{
		when(exceptionMappingSource.getGenericTechnicalToException((Method) any())).thenAnswer(new Answer<Class<PlisTechnicalTestToException>>() {
			@Override
			public Class<PlisTechnicalTestToException> answer(InvocationOnMock invocation) throws Throwable {				
				return PlisTechnicalTestToException.class;
			}
		});
		RemoteBean proxy = getProxyForRemoteBean();
		proxy.eineMethodeMitPlisTechnicalRuntimeException();
	}
	
	@Test(expected = PlisTechnicalTestToException.class)
	public void testInvokeIllegalArgumentException() throws InvocationTargetException, PlisTechnicalToException{
		when(exceptionMappingSource.getGenericTechnicalToException((Method) any())).thenAnswer(new Answer<Class<PlisTechnicalTestToException>>() {
			@Override
			public Class<PlisTechnicalTestToException> answer(InvocationOnMock invocation) throws Throwable {				
				return PlisTechnicalTestToException.class;
			}
		});
		RemoteBean proxy = getProxyForRemoteBean();
		proxy.eineMethodeMitException();
	}
}