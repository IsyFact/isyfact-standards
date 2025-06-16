package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.aop.framework.ProxyFactory;

import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.ValidRemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl.ValidRemoteBeanImpl;

import ma.glasnost.orika.MapperFacade;

public class TestServiceImpl {

    private MethodMappingSource source;
    private MapperFacade mapper;
    private ValidRemoteBean remote;
    private ServiceImpl service;
    private ValidRemoteBean proxy;

    @Before
    public void setUp() throws Exception {
        remote = new ValidRemoteBeanImpl();
        service = new ServiceImpl();
        source = Mockito.mock(MethodMappingSource.class);
        mapper = Mockito.mock(MapperFacade.class);
        service.setMethodMappingSource(source);
        service.setMapper(mapper);
        ProxyFactory fac = new ProxyFactory(remote);
        fac.addAdvice(service);
        proxy = (ValidRemoteBean) fac.getProxy();
    }

    @Test
    public void testInvoke() throws Exception {
        Mockito.when(source.getTargetMethod(Mockito.any(), Mockito.any())).thenReturn(remote.getClass().getMethod("eineMethode"));
        proxy.eineMethode();
    }

    @Test(expected = InvocationTargetException.class)
    public void testInvokeMitException() throws Exception {
        RemoteBean bean = new RemoteBeanImpl();
        ProxyFactory fac = new ProxyFactory(bean);
        fac.addAdvice(service);
        RemoteBean proxy = (RemoteBean) fac.getProxy();
        Mockito.when(source.getTargetMethod(Mockito.any(), Mockito.any())).thenReturn(bean.getClass().getMethod("eineMethodeMitException"));
        proxy.eineMethodeMitException();
    }

    @Test
    public void testInvokeMitParameter() throws Exception {
        Mockito.when(source.getTargetMethod(Mockito.any(), Mockito.any())).thenReturn(remote.getClass().getMethod("methodeMitParametern", Integer.class, String.class));
        Mockito.when(source.skipParameter(Mockito.any())).thenReturn(false, false);
        proxy.methodeMitParametern(10, "zehn");
    }

    @Test
    public void testValidateConfiguration() {
        service.validateConfiguration(ValidRemoteBean.class, remote);
    }
}
