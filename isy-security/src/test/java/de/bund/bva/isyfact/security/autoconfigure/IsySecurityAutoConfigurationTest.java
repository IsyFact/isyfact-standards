package de.bund.bva.isyfact.security.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.Security;

class IsySecurityAutoConfigurationTest extends AbstractOidcProviderTest {

    WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    OAuth2ClientAutoConfiguration.class,
                    IsySecurityAutoConfiguration.class,
                    IsyOAuth2ClientAutoConfiguration.class
            ));

    /**
     * case 1: acting as ressource-server -> no IsyOAuth2ClientAutoConfiguration
     */
    @Test
    public void isyOAuth2ClientAutoConfigurationSkipped() {
        contextRunner
                .withClassLoader(new FilteredClassLoader(ClientRegistration.class))
                .run(context ->
                        assertThat(context).hasNotFailed()
                                .hasSingleBean(IsySecurityAutoConfiguration.class)
                                .doesNotHaveBean(IsyOAuth2ClientAutoConfiguration.class)
                                .doesNotHaveBean(IsyOAuth2ClientAutoConfiguration.ClientsConfiguredDependentBeans.class)
                );
    }

    // case 2: client enabled but no ClientRegistrations
    @Test
    public void isyOAuth2ClientAutoConfigurationWithoutClientRegistrations() {
        contextRunner
                .run(context ->
                        assertThat(context).hasNotFailed()
                                .hasSingleBean(IsySecurityAutoConfiguration.class)
                                .hasSingleBean(IsyOAuth2ClientAutoConfiguration.class)
                                .doesNotHaveBean(IsyOAuth2ClientAutoConfiguration.ClientsConfiguredDependentBeans.class)
                );
    }

    // case 3: client enabled and ClientRegistrations
    @Test
    public void isyOAuth2ClientAutoConfigurationWithClientRegistration() {
        contextRunner
                .withUserConfiguration(TestConfig.class)
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.testrealm.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.cc-testclient.client-id=cc-testclient-id",
                        "spring.security.oauth2.client.registration.cc-testclient.client-secret=cc-testclient-secret",
                        "spring.security.oauth2.client.registration.cc-testclient.authorization-grant-type=client-credentials",
                        "spring.security.oauth2.client.registration.cc-testclient.provider=testrealm"
                )
                .run(context ->
                        assertThat(context).hasNotFailed()
                                .hasSingleBean(IsySecurityAutoConfiguration.class)
                                .hasSingleBean(IsyOAuth2ClientAutoConfiguration.class)
                                .hasSingleBean(IsyOAuth2ClientAutoConfiguration.ClientsConfiguredDependentBeans.class)
                );
    }

    @Test
    public void createSecurityBean() {
        contextRunner.withConfiguration(AutoConfigurations.of(IsySecurityAutoConfiguration.class))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(Security.class)
                );
    }

    @Configuration
    @EnableWebSecurity
    public static class TestConfig {
    }
}
