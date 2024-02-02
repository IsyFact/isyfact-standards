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
import de.bund.bva.isyfact.security.autoconfigure.IsySecurityAutoConfiguration;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.helper.DebugAufrufKontextVerwalter;
import de.bund.bva.isyfact.sicherheit.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.Rolle;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.autoconfigure.IsySicherheitAutoConfiguration;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;

public class IsyServiceApiCoreConditionalAutoConfigurationClassesTest {

    @Test
    public void isyServiceApiCoreAutoConfigurationWithIsySicherheitAndIsySecurity() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        IsyServiceApiCoreAutoConfiguration.class,
                        IsyServiceApiSicherheitAutoConfiguration.class,
                        IsyServiceApiSecurityAutoConfiguration.class,
                        IsySicherheitAutoConfiguration.class,
                        IsySecurityAutoConfiguration.class
                ))
                // necessary for IsySicherheitAutoConfiguration
                .withConfiguration(UserConfigurations.of(AufrufKontextTestConfig.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(IsyServiceApiCoreAutoConfiguration.class)
                            .hasSingleBean(IsySicherheitAutoConfiguration.class)
                            .hasSingleBean(IsyServiceApiSicherheitAutoConfiguration.class)
                            .hasSingleBean(IsySecurityAutoConfiguration.class)
                            .hasSingleBean(IsyServiceApiSecurityAutoConfiguration.class);
                });
    }

    @Test
    public void isyServiceApiCoreAutoConfigurationWithoutIsySicherheitAndIsySecurity() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        IsyServiceApiCoreAutoConfiguration.class,
                        IsyServiceApiSicherheitAutoConfiguration.class,
                        IsyServiceApiSecurityAutoConfiguration.class
                ))
                // "remove" IsySicherheitAutoConfiguration and IsySecurityAutoConfiguration
                .withClassLoader(new FilteredClassLoader(
                        IsySicherheitAutoConfiguration.class,
                        IsySecurityAutoConfiguration.class
                ))
                .run(context -> {
                    assertThat(context).hasSingleBean(IsyServiceApiCoreAutoConfiguration.class)
                            .doesNotHaveBean(IsySicherheitAutoConfiguration.class)
                            .doesNotHaveBean(IsyServiceApiSicherheitAutoConfiguration.class)
                            .doesNotHaveBean(IsySecurityAutoConfiguration.class)
                            .doesNotHaveBean(IsyServiceApiSecurityAutoConfiguration.class);
                });
    }

    @Test
    public void isyServiceApiCoreAutoConfigurationWithoutIsySicherheit() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        IsyServiceApiCoreAutoConfiguration.class,
                        IsyServiceApiSicherheitAutoConfiguration.class,
                        IsyServiceApiSecurityAutoConfiguration.class,
                        IsySecurityAutoConfiguration.class
                ))
                // "remove" IsySicherheitAutoConfiguration
                .withClassLoader(new FilteredClassLoader(IsySicherheitAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(IsyServiceApiCoreAutoConfiguration.class)
                            .doesNotHaveBean(IsySicherheitAutoConfiguration.class)
                            .doesNotHaveBean(IsyServiceApiSicherheitAutoConfiguration.class)
                            .hasSingleBean(IsySecurityAutoConfiguration.class)
                            .hasSingleBean(IsyServiceApiSecurityAutoConfiguration.class);
                });
    }

    @Test
    public void isyServiceApiCoreAutoConfigurationWithoutIsySecurity() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        IsyServiceApiCoreAutoConfiguration.class,
                        IsyServiceApiSicherheitAutoConfiguration.class,
                        IsyServiceApiSecurityAutoConfiguration.class,
                        IsySicherheitAutoConfiguration.class
                ))
                // necessary for IsySicherheitAutoConfiguration
                .withConfiguration(UserConfigurations.of(AufrufKontextTestConfig.class))
                // "remove" IsySecurityAutoConfiguration
                .withClassLoader(new FilteredClassLoader(IsySecurityAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(IsyServiceApiCoreAutoConfiguration.class)
                            .hasSingleBean(IsySicherheitAutoConfiguration.class)
                            .hasSingleBean(IsyServiceApiSicherheitAutoConfiguration.class)
                            .doesNotHaveBean(IsySecurityAutoConfiguration.class)
                            .doesNotHaveBean(IsyServiceApiSecurityAutoConfiguration.class);
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
