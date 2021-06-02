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

import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

import com.github.dozermapper.core.Mapper;

import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.ValidRemoteBean;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl.ValidRemoteBeanImpl;

public class TestServiceImpl {

    private MethodMappingSource source;
    private Mapper mapper;
    private ValidRemoteBean remote;
    private ServiceImpl service;
    private ValidRemoteBean proxy;

    @Before
    public void setUp() throws Exception {
        remote = new ValidRemoteBeanImpl();
        service = new ServiceImpl();
        source = mock(MethodMappingSource.class);
        mapper = mock(Mapper.class);
        service.setMethodMappingSource(source);
        service.setMapper(mapper);
        ProxyFactory fac = new ProxyFactory(remote);
        fac.addAdvice(service);
        proxy = (ValidRemoteBean) fac.getProxy();
    }

    @Test
    public void testInvoke() throws PlisTechnicalToException, NoSuchMethodException, SecurityException {
        when(source.getTargetMethod(any(), any())).thenReturn(remote.getClass().getMethod("eineMethode"));
        proxy.eineMethode();
    }

    @Test(expected = InvocationTargetException.class)
    public void testInvokeMitException() throws PlisTechnicalToException, NoSuchMethodException, SecurityException, InvocationTargetException {
        RemoteBean bean = new RemoteBeanImpl();
        ProxyFactory fac = new ProxyFactory(bean);
        fac.addAdvice(service);
        RemoteBean proxy = (RemoteBean) fac.getProxy();
        when(source.getTargetMethod(any(), any())).thenReturn(bean.getClass().getMethod("eineMethodeMitException"));
        proxy.eineMethodeMitException();
    }

    @Test
    public void testInvokeMitParameter() throws PlisTechnicalToException, NoSuchMethodException, SecurityException {
        when(source.getTargetMethod(any(), any())).thenReturn(remote.getClass().getMethod("methodeMitParametern", Integer.class, String.class));
        when(source.skipParameter(any())).thenReturn(false, false);
        proxy.methodeMitParametern(10, "zehn");
    }

    @Test
    public void testValidateConfiguration() {
        service.validateConfiguration(ValidRemoteBean.class, remote);
    }

}
