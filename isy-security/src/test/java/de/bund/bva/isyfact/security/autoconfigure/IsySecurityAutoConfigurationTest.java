package de.bund.bva.isyfact.security.autoconfigure;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;
import de.bund.bva.isyfact.security.config.IsySecurityConfigurationProperties;
import de.bund.bva.isyfact.security.core.Berechtigungsmanager;
import de.bund.bva.isyfact.security.core.Security;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ManualClientCredentialsAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordAuthenticationProvider;
import de.bund.bva.isyfact.security.xmlparser.RolePrivilegesMapper;

public class IsySecurityAutoConfigurationTest extends AbstractOidcProviderTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    // isy-security auto configurations
                    IsySecurityAutoConfiguration.class, IsyOAuth2ClientAutoConfiguration.class,
                    // spring-security is required
                    SecurityAutoConfiguration.class
            ));

    @BeforeAll
    public static void setup() {
        registerTestClients();
    }

    // case 1: acting as ressource-server -> no IsyOAuth2ClientAutoConfiguration
    @Test
    public void testContextStartsWithoutSpringSecurityOAuth2ClientLibrary() {
        contextRunner
                .withClassLoader(new FilteredClassLoader(ClientRegistration.class)) // "remove" spring-security-oauth2-client
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        // core beans
                        .hasSingleBean(IsySecurityConfigurationProperties.class)
                        .hasSingleBean(RolePrivilegesMapper.class)
                        .hasSingleBean(JwtAuthenticationConverter.class)
                        .hasSingleBean(GrantedAuthorityDefaults.class)
                        .hasSingleBean(Berechtigungsmanager.class)
                        .hasSingleBean(Security.class)
                        // isy oauth2 auto config beans
                        .doesNotHaveBean(ManualClientCredentialsAuthenticationProvider.class)
                        .doesNotHaveBean(ProviderManager.class)
                        .doesNotHaveBean(Authentifizierungsmanager.class)
                        // client registration beans
                        .doesNotHaveBean(IsyOAuth2ClientConfigurationProperties.class)
                        .doesNotHaveBean(ClientCredentialsAuthenticationProvider.class)
                        .doesNotHaveBean(PasswordAuthenticationProvider.class)
                );
    }

    // case 2: client enabled but no ClientRegistrations
    @Test
    public void testContextStartsWithoutConfiguredClients() {
        contextRunner
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        // core beans
                        .hasSingleBean(IsySecurityConfigurationProperties.class)
                        .hasSingleBean(RolePrivilegesMapper.class)
                        .hasSingleBean(JwtAuthenticationConverter.class)
                        .hasSingleBean(GrantedAuthorityDefaults.class)
                        .hasSingleBean(Berechtigungsmanager.class)
                        .hasSingleBean(Security.class)
                        // isy oauth2 auto config beans
                        .hasSingleBean(ManualClientCredentialsAuthenticationProvider.class)
                        .hasSingleBean(ProviderManager.class)
                        .hasSingleBean(Authentifizierungsmanager.class)
                        // client registration beans
                        .doesNotHaveBean(IsyOAuth2ClientConfigurationProperties.class)
                        .doesNotHaveBean(ClientCredentialsAuthenticationProvider.class)
                        .doesNotHaveBean(PasswordAuthenticationProvider.class)
                        // provider manager has only the manual provider configured
                        .getBean(ProviderManager.class).extracting(ProviderManager::getProviders, as(InstanceOfAssertFactories.LIST))
                        .map(Object::getClass).map(Class::getName)
                        .containsExactlyInAnyOrder(ManualClientCredentialsAuthenticationProvider.class.getName())
                );
    }

    // case 3: client enabled and ClientRegistrations
    @Test
    public void testContextStartsWithConfiguredClients() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=test"
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        // core beans
                        .hasSingleBean(IsySecurityConfigurationProperties.class)
                        .hasSingleBean(RolePrivilegesMapper.class)
                        .hasSingleBean(JwtAuthenticationConverter.class)
                        .hasSingleBean(GrantedAuthorityDefaults.class)
                        .hasSingleBean(Berechtigungsmanager.class)
                        .hasSingleBean(Security.class)
                        // isy oauth2 auto config beans
                        .hasSingleBean(ManualClientCredentialsAuthenticationProvider.class)
                        .hasSingleBean(ProviderManager.class)
                        .hasSingleBean(Authentifizierungsmanager.class)
                        // client registration beans
                        .hasSingleBean(IsyOAuth2ClientConfigurationProperties.class)
                        .hasSingleBean(ClientCredentialsAuthenticationProvider.class)
                        .hasSingleBean(PasswordAuthenticationProvider.class)
                        // provider manager has all three providers configured
                        .getBean(ProviderManager.class).extracting(ProviderManager::getProviders, as(InstanceOfAssertFactories.LIST))
                        .map(Object::getClass).map(Class::getName).containsExactlyInAnyOrder(
                                ClientCredentialsAuthenticationProvider.class.getName(), PasswordAuthenticationProvider.class.getName(),
                                ManualClientCredentialsAuthenticationProvider.class.getName())
                );
    }

    @Test
    public void testContestFailsIfAdditionalOAuth2RegistrationIsInvalid() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=test",
                        "isy.security.oauth2.client.registration.invalid.username=test",
                        "isy.security.oauth2.client.registration.invalid.password=test"
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    public void testContestFailsIfAdditionalOAuth2PropertiesAreInvalid() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=test",
                        "isy.security.oauth2.client.registration.test.username=test",
                        "isy.security.oauth2.client.registration.test.password="
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    public void testContestStartsIfAdditionalOAuth2PropertiesAreValid() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=test",
                        "isy.security.oauth2.client.registration.test.username=test",
                        "isy.security.oauth2.client.registration.test.password=test"
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context).hasNotFailed());
    }

}
