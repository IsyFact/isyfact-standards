package de.bund.bva.isyfact.serviceapi.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereitInterceptor;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.StelltAufrufKontextBereitInterceptor;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.helper.DebugAufrufKontextVerwalter;
import de.bund.bva.isyfact.sicherheit.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.Rolle;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.autoconfigure.IsySicherheitAutoConfiguration;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;

public class IsyServiceApiCoreAutoConfigurationSicherheitTest {

    @Test
    public void isyServiceApiCoreAutoConfigurationWithoutIsySicherheit() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        IsyServiceApiCoreAutoConfiguration.class,
                        IsyServiceApiSicherheitAutoConfiguration.class
                ))
                .withClassLoader(new FilteredClassLoader(IsySicherheitAutoConfiguration.class))
                .run(context -> {
                    assertThat(context)
                            .doesNotHaveBean(StelltAufrufKontextBereitInterceptor.class)
                            .doesNotHaveBean("stelltAufrufKontextBereitAdvisor");
                });
    }

    @Test
    public void isyServiceApiCoreAutoConfigurationWithIsySicherheit() {
        // test that IsyServiceApiCore is autoconfigured after IsySicherheit when a Sicherheit bean is provided
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        IsyServiceApiCoreAutoConfiguration.class,
                        IsyServiceApiSicherheitAutoConfiguration.class,
                        IsySicherheitAutoConfiguration.class
                ))
                .withConfiguration(UserConfigurations.of(AufrufKontextTestConfig.class))
                .withPropertyValues(
                        "isy.logging.anwendung.name=test",
                        "isy.logging.anwendung.typ=test",
                        "isy.logging.anwendung.version=test"
                ).run(context -> {
                    assertThat(context)
                            .hasSingleBean(StelltLoggingKontextBereitInterceptor.class)
                            .hasBean("stelltLoggingKontextBereitAdvisor")
                            .hasSingleBean(StelltAufrufKontextBereitInterceptor.class)
                            .hasBean("stelltAufrufKontextBereitAdvisor");
                });
    }

    @Configuration
    public static class AufrufKontextTestConfig {

        // The AufrufKontextVerwalter has to be provided by the application and is necessary for the interceptor creation
        @Bean
        public AufrufKontextVerwalter<?> aufrufKontextVerwalter() {
            return new DebugAufrufKontextVerwalter();
        }

        // A Sicherheit bean is necessary for IsySicherheitAutoConfiguration to activate
        @Bean
        public Sicherheit<?> sicherheit() {
            return new Sicherheit<AufrufKontext>() {
                @Override
                public Berechtigungsmanager getBerechtigungsManager() throws AuthentifizierungTechnicalException {
                    return null;
                }

                @Override
                public void leereCache() {
                }

                @Override
                public Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziere(AufrufKontext unauthentifizierterAufrufKontext) throws AuthentifizierungTechnicalException {
                    return null;
                }

                @Override
                public Set<Rolle> getAlleRollen() {
                    return null;
                }
            };
        }

    }

}
