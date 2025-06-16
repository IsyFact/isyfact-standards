package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.ValidRemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl.ValidRemoteBeanImpl;

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
    public void testInvoke() throws Exception {
        proxy.eineMethode();
    }

    @Test
    public void testValidateConfiguration() {
        delegator.validateConfiguration(ValidRemoteBean.class, bean);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateConfigurationWrongTargetObject() {
        delegator.validateConfiguration(ValidRemoteBean.class, new Object());
    }

    @Test(expected = InvocationTargetException.class)
    public void testInvokeMitException() throws Exception {
        ProxyFactory fac = new ProxyFactory(new RemoteBeanImpl());
        fac.addAdvice(delegator);
        ((RemoteBean) fac.getProxy()).eineMethodeMitException();
    }

}
