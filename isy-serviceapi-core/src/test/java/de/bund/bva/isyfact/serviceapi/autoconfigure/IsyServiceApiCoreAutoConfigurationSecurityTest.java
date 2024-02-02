package de.bund.bva.isyfact.serviceapi.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.support.RemoteInvocationFactory;

import de.bund.bva.isyfact.security.core.Berechtigungsmanager;
import de.bund.bva.isyfact.security.autoconfigure.IsySecurityAutoConfiguration;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.AufrufKontextToRemoteInvocationFactory;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.CreateAufrufKontextToStrategy;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.IsyHttpInvokerProxyFactoryBean;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.DummyServiceRemoteBean;

public class IsyServiceApiCoreAutoConfigurationSecurityTest {

    ApplicationContextRunner applicationContextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(IsyServiceApiCoreAutoConfiguration.class, IsyServiceApiSecurityAutoConfiguration.class));

    @Test
    public void isyServiceApiCoreAutoConfigurationWithoutIsySecurity() {
        applicationContextRunner
                .withClassLoader(new FilteredClassLoader(IsySecurityAutoConfiguration.class))
                .run(context -> {
                    assertThat(context)
                            .doesNotHaveBean(CreateAufrufKontextToStrategy.class)
                            .doesNotHaveBean(RemoteInvocationFactory.class);
                });
    }

    @Test
    public void isyServiceApiCoreAutoConfigurationWithIsySecurity() {
        // test that IsyServiceApiCore is autoconfigured after IsySecurity
        applicationContextRunner
                .withConfiguration(AutoConfigurations.of(IsySecurityAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(IsySecurityAutoConfiguration.class)
                            .hasSingleBean(Berechtigungsmanager.class)
                            .hasSingleBean(CreateAufrufKontextToStrategy.class)
                            .hasSingleBean(RemoteInvocationFactory.class);
                });
    }

    @Test
    public void remoteInvocationFactoryIsSet() {
        applicationContextRunner
                .withConfiguration(AutoConfigurations.of(IsySecurityAutoConfiguration.class))
                .withUserConfiguration(TestConfiguration.class)
                .run(context -> {
                    RemoteInvocationFactory remoteInvocationFactory = context.getBean(RemoteInvocationFactory.class);
                    IsyHttpInvokerProxyFactoryBean isyHttpInvokerProxyFactoryBean = context.getBean(IsyHttpInvokerProxyFactoryBean.class);

                    assertThat(remoteInvocationFactory).isInstanceOf(AufrufKontextToRemoteInvocationFactory.class);
                    assertThat(isyHttpInvokerProxyFactoryBean.getRemoteInvocationFactory()).isInstanceOf(AufrufKontextToRemoteInvocationFactory.class);
                });
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public IsyHttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean() {
            IsyHttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new IsyHttpInvokerProxyFactoryBean();
            httpInvokerProxyFactoryBean.setRemoteSystemName("testRemoteSystem");
            httpInvokerProxyFactoryBean.setServiceUrl("testServiceUrl");
            httpInvokerProxyFactoryBean.setServiceInterface(DummyServiceRemoteBean.class);
            return httpInvokerProxyFactoryBean;
        }
    }
}
