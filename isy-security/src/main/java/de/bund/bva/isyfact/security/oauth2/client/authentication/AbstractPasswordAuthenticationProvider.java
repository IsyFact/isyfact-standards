package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultPasswordTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.util.Assert;

import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;

public abstract class AbstractPasswordAuthenticationProvider extends IsyOAuth2AuthenticationProvider {

    /** Global isy-security Configuration properties. */
    protected final IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps;

    protected AbstractPasswordAuthenticationProvider(JwtAuthenticationConverter jwtAuthenticationConverter,
                                                     IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps) {
        super(jwtAuthenticationConverter);
        this.isyOAuth2ClientProps = isyOAuth2ClientProps;
    }

    protected OAuth2AuthorizedClient obtainAuthorizedClient(ClientRegistration clientRegistration, Authentication principal,
                                                            String username, String password, @Nullable String bhknz) {
        Assert.hasText(username, "username cannot be empty");
        Assert.hasText(password, "password cannot be empty");

        OAuth2AuthorizationContext authorizationContext = OAuth2AuthorizationContext.withClientRegistration(clientRegistration)
                .principal(principal)
                .attribute(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username)
                .attribute(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password)
                .build();

        OAuth2AccessTokenResponseClient<OAuth2PasswordGrantRequest> responseClient = createPasswordTokenResponseClient(bhknz);
        OAuth2AuthorizedClientProvider clientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .password(passwordGrantBuilder -> passwordGrantBuilder.accessTokenResponseClient(responseClient))
                .build();
        return clientProvider.authorize(authorizationContext);
    }

    private OAuth2AccessTokenResponseClient<OAuth2PasswordGrantRequest> createPasswordTokenResponseClient(@Nullable String bhknz) {
        // we need to create a new client every time because each request has a unique interceptor
        DefaultPasswordTokenResponseClient passwordTokenResponseClient = new DefaultPasswordTokenResponseClient();

        if (bhknz != null) {
            String headerValue = String.format("%s:%s", bhknz, isyOAuth2ClientProps.getDefaultCertificateOu());

            // add a headers converter to the default request entity converter which sets the bhknz header
            passwordTokenResponseClient.setRequestEntityConverter(passwordGrantRequest -> {
                OAuth2PasswordGrantRequestEntityConverter converter = new OAuth2PasswordGrantRequestEntityConverter();
                converter.addHeadersConverter(addBhknzHeader(headerValue));
                return converter.convert(passwordGrantRequest);
            });
        }

        return passwordTokenResponseClient;
    }

    private Converter<OAuth2PasswordGrantRequest, HttpHeaders> addBhknzHeader(String headerValue) {
        return (request) -> {
            HttpHeaders headers = new HttpHeaders();
            headers.add(isyOAuth2ClientProps.getBhknzHeaderName(), headerValue);
            return headers;
        };
    }
}
