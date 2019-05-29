package de.bund.bva.isyfact.serviceapi.autoconfigure;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextFactoryImpl;
import de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereitInterceptor;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.StelltAufrufKontextBereitInterceptor;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.helper.DebugAufrufKontextVerwalter;

import static org.assertj.core.api.Assertions.assertThat;


public class IsyServiceApiCoreAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(IsyServiceApiCoreAutoConfiguration.class));

    @Test
    public void isyServiceApiCoreAutoConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(StelltLoggingKontextBereitInterceptor.class);
            assertThat(context).hasBean("stelltLoggingKontextBereitAdvisor");
            assertThat(context).doesNotHaveBean(StelltAufrufKontextBereitInterceptor.class);
            assertThat(context).doesNotHaveBean("stelltAufrufKontextBereitAdvisor");
        });
    }

    @Test
    public void isyServiceApiCoreAutoConfigurationWithAufrufKontext() {
        contextRunner
            .withConfiguration(UserConfigurations.of(AufrufKontextTestConfig.class)).run(context -> {
            assertThat(context).hasSingleBean(StelltLoggingKontextBereitInterceptor.class);
            assertThat(context).hasBean("stelltLoggingKontextBereitAdvisor");
            assertThat(context).hasSingleBean(StelltAufrufKontextBereitInterceptor.class);
            assertThat(context).hasBean("stelltAufrufKontextBereitAdvisor");
        });
    }

    @Configuration
    public static class AufrufKontextTestConfig {

        @Bean
        public AufrufKontextFactory aufrufKontextFactory() {
            return new AufrufKontextFactoryImpl();
        }

        @Bean
        public AufrufKontextVerwalter aufrufKontextVerwalter() {
            return new DebugAufrufKontextVerwalter();
        }
    }
}