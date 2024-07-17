package de.bund.bva.isyfact.security.autoconfigure;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import com.nimbusds.jwt.proc.JWTProcessor;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;
import de.bund.bva.isyfact.security.config.IsySecurityConfigurationProperties;
import de.bund.bva.isyfact.security.config.JWTConfigurationProperties;
import de.bund.bva.isyfact.security.core.Berechtigungsmanager;
import de.bund.bva.isyfact.security.core.Security;
import de.bund.bva.isyfact.security.core.TenantJwsKeySelector;
import de.bund.bva.isyfact.security.core.TenantJwtIssuerValidator;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.oauth2.client.annotation.AuthenticateInterceptor;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsAuthorizedClientAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsClientRegistrationAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordClientRegistrationAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.util.BhknzHeaderConverterBuilder;
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
                        .doesNotHaveBean(IsyOAuth2ClientConfigurationProperties.class)
                        .doesNotHaveBean(BhknzHeaderConverterBuilder.class)
                        .doesNotHaveBean(ClientCredentialsClientRegistrationAuthenticationProvider.class)
                        .doesNotHaveBean(PasswordClientRegistrationAuthenticationProvider.class)
                        .doesNotHaveBean(ProviderManager.class)
                        .doesNotHaveBean(Authentifizierungsmanager.class)
                        // client registration beans
                        .doesNotHaveBean(OAuth2ClientProperties.class)
                        .doesNotHaveBean(ClientCredentialsAuthorizedClientAuthenticationProvider.class)
                        .doesNotHaveBean(AuthenticateInterceptor.class)
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
                        .hasSingleBean(IsyOAuth2ClientConfigurationProperties.class)
                        .hasSingleBean(BhknzHeaderConverterBuilder.class)
                        .hasSingleBean(ClientCredentialsClientRegistrationAuthenticationProvider.class)
                        .hasSingleBean(PasswordClientRegistrationAuthenticationProvider.class)
                        .hasSingleBean(ProviderManager.class)
                        .hasSingleBean(Authentifizierungsmanager.class)
                        // client registration beans
                        .doesNotHaveBean(OAuth2ClientProperties.class)
                        .doesNotHaveBean(ClientCredentialsAuthorizedClientAuthenticationProvider.class)
                        .doesNotHaveBean(AuthenticateInterceptor.class)
                        // provider manager has only the manual provider configured
                        .getBean(ProviderManager.class).extracting(ProviderManager::getProviders, as(InstanceOfAssertFactories.LIST))
                        .map(Object::getClass).map(Class::getName).containsExactlyInAnyOrder(
                                ClientCredentialsClientRegistrationAuthenticationProvider.class.getName(),
                                PasswordClientRegistrationAuthenticationProvider.class.getName())
                );
    }

    // case 3: client enabled and ClientRegistrations
    @Test
    public void testContextStartsWithConfiguredClients() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=testclient"
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
                        .hasSingleBean(IsyOAuth2ClientConfigurationProperties.class)
                        .hasSingleBean(BhknzHeaderConverterBuilder.class)
                        .hasSingleBean(ClientCredentialsClientRegistrationAuthenticationProvider.class)
                        .hasSingleBean(PasswordClientRegistrationAuthenticationProvider.class)
                        .hasSingleBean(ProviderManager.class)
                        .hasSingleBean(Authentifizierungsmanager.class)
                        // client registration beans
                        .hasSingleBean(OAuth2ClientProperties.class)
                        .hasSingleBean(ClientCredentialsAuthorizedClientAuthenticationProvider.class)
                        .hasSingleBean(AuthenticateInterceptor.class)
                        // provider manager has all three providers configured
                        .getBean(ProviderManager.class).extracting(ProviderManager::getProviders, as(InstanceOfAssertFactories.LIST))
                        .map(Object::getClass).map(Class::getName).containsExactlyInAnyOrder(
                                ClientCredentialsAuthorizedClientAuthenticationProvider.class.getName(),
                                PasswordClientRegistrationAuthenticationProvider.class.getName(),
                                ClientCredentialsClientRegistrationAuthenticationProvider.class.getName())
                );
    }

    @Test
    public void testContextFailsIfAdditionalOAuth2RegistrationIsInvalid() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=testclient",
                        "isy.security.oauth2.client.registration.invalid.username=testuser",
                        "isy.security.oauth2.client.registration.invalid.password=testpw"
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    public void testContextStartsIfAdditionalOAuth2PropertiesAreValid() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=testclient",
                        "isy.security.oauth2.client.registration.test.username=testuser",
                        "isy.security.oauth2.client.registration.test.password=testpw"
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(IsyOAuth2ClientConfigurationProperties.class)
                        .hasSingleBean(OAuth2ClientProperties.class)
                );
    }

    /**
     * Autoconfiguration only tests if the properties can be mapped to a client registration ID.
     * Whether they are queried at all is decided by the Authentifizierungsmanager and having a
     * "username" without a "password" is not automatically wrong.
     */
    @Test
    public void testContextStartsIfOnlyUsernameSet() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=testclient",
                        "isy.security.oauth2.client.registration.test.username=testuser"
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(IsyOAuth2ClientConfigurationProperties.class)
                        .hasSingleBean(OAuth2ClientProperties.class)
                );
    }

    /**
     * Autoconfiguration only tests if the properties can be mapped to a client registration ID.
     * Whether they are queried at all is decided by the Authentifizierungsmanager and having a
     * "password" without a "username" is not automatically wrong.
     */
    @Test
    public void testContextStartsIfOnlyPasswordSet() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=testclient",
                        "isy.security.oauth2.client.registration.test.password=testpw"
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(IsyOAuth2ClientConfigurationProperties.class)
                        .hasSingleBean(OAuth2ClientProperties.class)
                );
    }

    @Test
    public void testContextFailsIfBhknzSetButNotOu() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=testclient",
                        "isy.security.oauth2.client.registration.test.bhknz=123456"
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    public void testContextStartsIfBhknzAndOuSet() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=testclient",
                        "isy.security.oauth2.client.registration.test.bhknz=123456",
                        "isy.security.oauth2.client.default-certificate-ou=TESTOU"
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(IsyOAuth2ClientConfigurationProperties.class)
                        .hasSingleBean(OAuth2ClientProperties.class)
                );
    }

    @Test
    public void testContextStartsIfAllAdditionalPropertiesAreSet() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.client.provider.test.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "spring.security.oauth2.client.registration.test.client-id=testclient",
                        "isy.security.oauth2.client.registration.test.username=testuser",
                        "isy.security.oauth2.client.registration.test.password=testpw",
                        "isy.security.oauth2.client.registration.test.bhknz=123456",
                        "isy.security.oauth2.client.default-certificate-ou=TESTOU"
                ).withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(IsyOAuth2ClientConfigurationProperties.class)
                        .hasSingleBean(OAuth2ClientProperties.class)
                );
    }

    @Test
    public void testContextStartsWithoutMultiTenancy() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9095/auth/realms/testrealm")
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(JWTConfigurationProperties.class)
                        .doesNotHaveBean(TenantJwsKeySelector.class)
                        .doesNotHaveBean(TenantJwtIssuerValidator.class)
                        .doesNotHaveBean(JwtDecoder.class)
                        .doesNotHaveBean(JWTProcessor.class)
                );
    }

    @Test
    public void testContextStartsWithoutMultiTenancyWithMultiTenancyProperties() {
        contextRunner
                .withPropertyValues(
                        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9095/auth/realms/testrealm",
                        "isy.security.oauth2.resourceserver.jwt.tenants.tenant1.issuer-uri=http://localhost:9095/auth/realms/testrealm1",
                        "isy.security.oauth2.resourceserver.jwt.tenants.tenant1.jwks-uri=http://localhost:9096/auth/realms/testrealm1")
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(JWTConfigurationProperties.class)
                        .doesNotHaveBean(TenantJwsKeySelector.class)
                        .doesNotHaveBean(TenantJwtIssuerValidator.class)
                        .doesNotHaveBean(JwtDecoder.class)
                        .doesNotHaveBean(JWTProcessor.class)
                );
    }

    @Test
    public void testContextStartsWithMultiTenancySpringPropertyMissing() {
        contextRunner
                .withPropertyValues(
                        "isy.security.oauth2.resourceserver.jwt.tenants.tenant1.issuer-uri=http://localhost:9095/auth/realms/testrealm1",
                        "isy.security.oauth2.resourceserver.jwt.tenants.tenant1.jwks-uri=http://localhost:9096/auth/realms/testrealm1")
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(JWTConfigurationProperties.class)
                        .hasSingleBean(TenantJwsKeySelector.class)
                        .hasSingleBean(TenantJwtIssuerValidator.class)
                        .hasSingleBean(JwtDecoder.class)
                        .hasSingleBean(JWTProcessor.class)
                );
    }

    @Test
    void isySecurityAutoConfigurationNoMappingFile() {
        contextRunner
                .withPropertyValues(
                        "isy.security.role-privileges-mapping-file=/resources/sicherheit/noRollenrechte.xml"
                )
                .run(context -> {
                            assertThat(context)
                                    .hasNotFailed()
                                    .hasSingleBean(RolePrivilegesMapper.class);

                            RolePrivilegesMapper mapper = context.getBean(RolePrivilegesMapper.class);
                            assertThat(mapper.getAllPrivileges()).isEmpty();
                        }
                );
    }
}
