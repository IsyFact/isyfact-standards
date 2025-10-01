package de.bund.bva.isyfact.security.autoconfigure;

import java.util.List;

import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.ConditionalOnOAuth2ClientRegistrationProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;
import de.bund.bva.isyfact.security.config.IsySecurityConfigurationProperties;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.oauth2.client.IsyOAuth2Authentifizierungsmanager;
import de.bund.bva.isyfact.security.oauth2.client.annotation.AuthenticateInterceptor;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsAuthorizedClientAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.ClientCredentialsClientRegistrationAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.IsyAccessTokenDecoderFactory;
import de.bund.bva.isyfact.security.oauth2.client.authentication.PasswordClientRegistrationAuthenticationProvider;
import de.bund.bva.isyfact.security.oauth2.client.authentication.util.BhknzHeaderConverterBuilder;

/**
 * Autoconfiguration for beans related to OAuth 2.0 client authentication.
 * Most of the beans will only be created if clients are configuration in the application properties.
 * If there is no need for {@link Authentifizierungsmanager} it is safe to exclude this autoconfiguration.
 * <p>
 * Since it creates beans that use {@link ClientRegistration}s the library {@code spring-boot-oauth2-client} is required.
 */
@ConditionalOnClass({ EnableWebSecurity.class, ClientRegistration.class })
@AutoConfiguration
@EnableConfigurationProperties
public class IsyOAuth2ClientAutoConfiguration {

    // configures value like BHKNZ and OU which should be available for manual password authentication
    @Bean
    @ConfigurationProperties(prefix = "isy.security.oauth2.client")
    public IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProperties(@Nullable OAuth2ClientProperties oAuth2ClientProperties) {
        return new IsyOAuth2ClientConfigurationProperties(oAuth2ClientProperties);
    }

    // configures the IsyAccessTokenDecoderFactory
    @Bean
    public JwtDecoderFactory<ClientRegistration> isyJwtDecoderFactory() {
        return new IsyAccessTokenDecoderFactory();
    }

    // lazy converter because we only need it when an optional BHKNZ is passed during authentication
    @Lazy
    @Bean
    @ConditionalOnMissingBean
    public BhknzHeaderConverterBuilder bhknzHeaderConverterBuilder(IsyOAuth2ClientConfigurationProperties isyOAuth2ClientConfigurationProperties) {
        return new BhknzHeaderConverterBuilder(isyOAuth2ClientConfigurationProperties);
    }

    // does not have a dependency on ClientRegistrations and should always be created
    @Bean
    @ConditionalOnMissingBean
    public ClientCredentialsClientRegistrationAuthenticationProvider clientCredentialsClientRegistrationAuthenticationProvider(
            JwtAuthenticationConverter jwtAuthenticationConverter) {
        return new ClientCredentialsClientRegistrationAuthenticationProvider(jwtAuthenticationConverter);
    }

    // does not have a dependency on ClientRegistrations and should always be created
    @Bean
    public PasswordClientRegistrationAuthenticationProvider passwordClientRegistrationAuthenticationProvider(
            JwtAuthenticationConverter jwtAuthenticationConverter,
            @Lazy BhknzHeaderConverterBuilder bhknzHeaderConverterBuilder) {
        return new PasswordClientRegistrationAuthenticationProvider(jwtAuthenticationConverter, bhknzHeaderConverterBuilder);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProviderManager providerManager(List<AuthenticationProvider> authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public Authentifizierungsmanager authentifizierungsmanager(
            ProviderManager providerManager,
            IsyOAuth2ClientConfigurationProperties isyOAuth2ClientConfigurationProperties,
            IsySecurityConfigurationProperties isySecurityConfigurationProperties,
            @Nullable ClientRegistrationRepository clientRegistrationRepository) {
        return new IsyOAuth2Authentifizierungsmanager(
                providerManager,
                isyOAuth2ClientConfigurationProperties,
                clientRegistrationRepository,
                isySecurityConfigurationProperties);
    }

    /**
     * Beans defined in this class are only required if any ClientRegistrations are configured.
     */
    @Configuration
    @ConditionalOnOAuth2ClientRegistrationProperties
    public static class ClientsConfiguredDependentBeans {

        /** Identifier for the AuthorizedClientManager created by isy-security. */
        public static final String ISY_AUTHORIZED_CLIENT_MANAGER_BEAN = "isyAuthorizedClientManager";

        /** Name of the {@link AuthenticateInterceptor}/Advisor bean. */
        public static final String AUTHENTICATE_INTERCEPTOR_BEAN = "isyOAuth2ClientAuthenticateInterceptor";

        /**
         * Provides an AuthorizedClientManager configured with the OAuth 2.0 Client Credentials flow that can be used to obtain
         * access tokens in the service-tier.
         */
        @Bean(ISY_AUTHORIZED_CLIENT_MANAGER_BEAN)
        public OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(
                ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
            OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                    .clientCredentials()
                    .build();

            AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                    new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
            authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

            return authorizedClientManager;
        }

        @Bean
        public ClientCredentialsAuthorizedClientAuthenticationProvider clientCredentialsAuthorizedClientAuthenticationProvider(
                @Qualifier(ISY_AUTHORIZED_CLIENT_MANAGER_BEAN) OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager,
                JwtAuthenticationConverter jwtAuthenticationConverter) {
            return new ClientCredentialsAuthorizedClientAuthenticationProvider(oAuth2AuthorizedClientManager, jwtAuthenticationConverter);
        }

        @Bean(AUTHENTICATE_INTERCEPTOR_BEAN)
        @ConditionalOnMissingBean(name = AUTHENTICATE_INTERCEPTOR_BEAN)
        public Advisor authenticateInterceptor(Authentifizierungsmanager authentifizierungsmanager) {
            return new AuthenticateInterceptor(authentifizierungsmanager);
        }
    }

}
