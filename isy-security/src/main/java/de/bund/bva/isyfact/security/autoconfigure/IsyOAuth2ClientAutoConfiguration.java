package de.bund.bva.isyfact.security.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import de.bund.bva.isyfact.security.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.authentication.IsyOAuth2Authentifizierungsmanager;
import de.bund.bva.isyfact.security.authentication.IsyOAuth2ClientCredentialsAuthenticationProvider;
import de.bund.bva.isyfact.security.authentication.IsyOAuth2ManualClientCredentialsAuthenticationProvider;
import de.bund.bva.isyfact.security.authentication.IsyOAuth2PasswordAuthenticationProvider;
import de.bund.bva.isyfact.security.config.IsyOAuth2ClientProperties;

/**
 * This class contains bean definitions for applications that act as OAuth2 clients.
 */
@Configuration
@ConditionalOnClass({ClientRegistration.class, EnableWebSecurity.class})
public class IsyOAuth2ClientAutoConfiguration {

    /**
     * Beans defined in this class are only required if any ClientRegistrations are configured.
     */
    @Configuration
    @Conditional(ClientsConfiguredCondition.class)
    public static class ClientsConfiguredDependantBeans {
        @Bean
        @ConfigurationProperties(prefix = "isy.security.oauth2.client")
        IsyOAuth2ClientProperties isyOAuth2ClientProperties(OAuth2ClientProperties oAuth2ClientProperties) {
            return new IsyOAuth2ClientProperties(oAuth2ClientProperties);
        }

        @Bean
        IsyOAuth2ClientCredentialsAuthenticationProvider isyOAuth2ClientCredentialsAuthenticationProvider(
                JwtAuthenticationConverter jwtAuthenticationConverter,
                OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager
        ) {
            return new IsyOAuth2ClientCredentialsAuthenticationProvider(
                    jwtAuthenticationConverter,
                    oAuth2AuthorizedClientManager
            );
        }

        @Bean
        @Conditional(ClientsConfiguredCondition.class)
        public IsyOAuth2PasswordAuthenticationProvider isyOAuth2PasswordAuthenticationProvider(
                ClientRegistrationRepository clientRegistrationRepository,
                OAuth2ClientProperties oAuth2ClientProperties,
                JwtAuthenticationConverter jwtAuthenticationConverter
        ) {
            return new IsyOAuth2PasswordAuthenticationProvider(
                    clientRegistrationRepository,
                    jwtAuthenticationConverter,
                    isyOAuth2ClientProperties(oAuth2ClientProperties)
            );
        }

        @Bean
        OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(
                ClientRegistrationRepository clientRegistrationRepository,
                OAuth2AuthorizedClientService authorizedClientService
        ) {
            OAuth2AuthorizedClientProvider clientProvider = OAuth2AuthorizedClientProviderBuilder
                    .builder()
                    .clientCredentials()
                    .build();
            AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                    clientRegistrationRepository,
                    authorizedClientService
            );
            clientManager.setAuthorizedClientProvider(clientProvider);
            return clientManager;
        }
    }

    @Bean
    @ConditionalOnMissingBean
    Authentifizierungsmanager authentifizierungsmanager(
            AuthenticationManager authenticationManager,
            @Nullable ClientRegistrationRepository clientRegistrationRepository
    ) {
        return new IsyOAuth2Authentifizierungsmanager(
                authenticationManager,
                clientRegistrationRepository
        );
    }

    @Bean
    @ConditionalOnMissingBean
    AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    IsyOAuth2ManualClientCredentialsAuthenticationProvider isyOAuth2ManualClientCredentialsAuthenticationProvider(
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) {
        return new IsyOAuth2ManualClientCredentialsAuthenticationProvider(
                jwtAuthenticationConverter
        );
    }
}
