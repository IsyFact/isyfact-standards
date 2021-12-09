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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

import de.bund.bva.isyfact.aufrufkontext.common.exception.AufrufKontextFehlerhaftException;
import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.TechnicalRuntimeTestException;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.TechnicalTestToException;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.ValidRemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl.ValidRemoteBeanImpl;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;

import ch.qos.logback.classic.Level;

public class TestServiceExceptionFassade {

    private ServiceExceptionFassade fassade;

    private ValidRemoteBean bean;

    private AusnahmeIdErmittler ermittler;

    private ExceptionMappingSource exceptionMappingSource;

    @Before
    public void setUp() {
        ermittler = mock(AusnahmeIdErmittler.class);
        exceptionMappingSource = mock(ExceptionMappingSource.class);
        fassade = new ServiceExceptionFassade(new ReflectiveMethodMappingSource(), exceptionMappingSource,
            ermittler, TechnicalRuntimeTestException.class);

        fassade.setLogLevelExceptions(Level.DEBUG.levelStr);
        bean = new ValidRemoteBeanImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetAppTechnicalRuntimeExceptionErwarteterKonstruktorNichtVorhanden() {
        fassade = new ServiceExceptionFassade(new ReflectiveMethodMappingSource(), exceptionMappingSource,
            ermittler, AufrufKontextFehlerhaftException.class);
    }

    @Test
    public void testValidateConfiguration() {
        when(exceptionMappingSource.getGenericTechnicalToException(any()))
            .thenAnswer(invocation -> PlisTechnicalToException.class);
        when(exceptionMappingSource.getToExceptionClass(any(), any()))
            .thenAnswer(invocation -> TechnicalException.class);

        fassade.validateConfiguration(ValidRemoteBean.class, bean);
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateConfigurationExceptionClassIsNull() {
        when(exceptionMappingSource.getGenericTechnicalToException(any()))
            .thenAnswer(invocation -> PlisTechnicalToException.class);
        when(exceptionMappingSource.getToExceptionClass(any(), any())).thenAnswer(invocation -> null);

        fassade.validateConfiguration(ValidRemoteBean.class, bean);
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateConfigurationExceptionClassIsNotOnMethod() {
        when(exceptionMappingSource.getGenericTechnicalToException(any()))
            .thenAnswer(invocation -> PlisTechnicalToException.class);
        when(exceptionMappingSource.getToExceptionClass(any(), any()))
            .thenAnswer(invocation -> BusinessException.class);

        fassade.validateConfiguration(ValidRemoteBean.class, bean);
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateConfigurationNoStaticConfigOfGenericToException() {
        when(exceptionMappingSource.getGenericTechnicalToException(any())).thenReturn(null);

        fassade.validateConfiguration(ValidRemoteBean.class, new ValidRemoteBeanImpl());
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateConfigurationNoTechnicalToExceptionOnMethod() {
        when(exceptionMappingSource.getGenericTechnicalToException(any()))
            .thenAnswer(invocation -> PlisTechnicalToException.class);

        fassade.validateConfiguration(RemoteBean.class, new RemoteBeanImpl());
    }

    @Test
    public void testInvoke() throws Exception {
        ProxyFactory fac = new ProxyFactory(bean);
        fac.addAdvice(fassade);
        ValidRemoteBean proxy = (ValidRemoteBean) fac.getProxy();
        proxy.eineMethode();
    }

    private RemoteBean getProxyForRemoteBean() {
        ProxyFactory fac = new ProxyFactory(new RemoteBeanImpl());
        fac.addAdvice(fassade);
        return (RemoteBean) fac.getProxy();
    }

    @Test(expected = TechnicalTestToException.class)
    public void testInvokeException() throws Throwable {
        when(exceptionMappingSource.getToExceptionClass(any(), any()))
            .thenAnswer(invocation -> TechnicalTestToException.class);
        RemoteBean proxy = getProxyForRemoteBean();
        proxy.eineMethodeMitBusinessException();
    }

    @Test(expected = TechnicalTestToException.class)
    public void testInvokeException2() throws Throwable {
        when(exceptionMappingSource.getGenericTechnicalToException(any()))
            .thenAnswer(invocation -> TechnicalTestToException.class);
        RemoteBean proxy = getProxyForRemoteBean();
        proxy.eineMethodeMitBusinessException();
    }

    @Test(expected = TechnicalTestToException.class)
    public void testInvokeTechnicalRuntimeException() throws Throwable {
        when(exceptionMappingSource.getGenericTechnicalToException(any()))
            .thenAnswer(invocation -> TechnicalTestToException.class);
        RemoteBean proxy = getProxyForRemoteBean();
        proxy.eineMethodeMitTechnicalRuntimeException();
    }

    @Test(expected = TechnicalTestToException.class)
    public void testInvokeIllegalArgumentException() throws Exception {
        when(exceptionMappingSource.getGenericTechnicalToException(any()))
            .thenAnswer(invocation -> TechnicalTestToException.class);
        RemoteBean proxy = getProxyForRemoteBean();
        proxy.eineMethodeMitException();
    }
}
