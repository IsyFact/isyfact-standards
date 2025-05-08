package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * FactoryBean zum Erstellen einer ProxyFactory für HTTP-Invoker-Aufrufe gemäß den Vorgaben der
 * IsyFact-Standards.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class IsyHttpInvokerProxyFactoryBean extends IsyHttpInvokerClientInterceptor implements
    FactoryBean<Object> {

    /** Die ProxyFactory. */
    private Object serviceProxy;

    /**
     * {@inheritDoc}
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() {
        return this.serviceProxy;
    }

    /**
     * {@inheritDoc}
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<?> getObjectType() {
        return getServiceInterface();
    }

    /**
     * {@inheritDoc}
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * {@inheritDoc}
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (getServiceInterface() == null) {
            throw new IllegalArgumentException("Property 'serviceInterface' is required");
        }
        this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
    }

}
