package de.bund.bva.isyfact.serviceapi.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereitInterceptor;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;

public class IsyServiceApiCoreAutoConfigurationTest {

    @Test
    public void isyServiceApiCoreAutoConfiguration() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(IsyServiceApiCoreAutoConfiguration.class))
                .run(context -> {
                    assertThat(context)
                            .hasSingleBean(StelltLoggingKontextBereitInterceptor.class)
                            .hasBean("stelltLoggingKontextBereitAdvisor")
                            .hasSingleBean(AufrufKontextToResolver.class);
                });
    }

}
