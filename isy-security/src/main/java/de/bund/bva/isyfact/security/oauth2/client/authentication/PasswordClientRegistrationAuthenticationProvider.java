package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultPasswordTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.util.Assert;

import de.bund.bva.isyfact.security.oauth2.client.authentication.token.PasswordClientRegistrationAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.util.BhknzHeaderConverterBuilder;

/**
 * Authentication Provider to obtain an {@link Authentication} with the OAuth2 Resource Owner Password Credentials flow
 * using an externally created Client Registration object.
 */
public class PasswordClientRegistrationAuthenticationProvider extends IsyOAuth2AuthenticationProvider {

    /** Builder for the BHKNZ header converter. */
    protected final BhknzHeaderConverterBuilder bhknzHeaderConverterBuilder;

    public PasswordClientRegistrationAuthenticationProvider(JwtAuthenticationConverter jwtAuthenticationConverter,
                                                            BhknzHeaderConverterBuilder bhknzHeaderConverterBuilder) {
        super(jwtAuthenticationConverter);
        this.bhknzHeaderConverterBuilder = bhknzHeaderConverterBuilder;
    }

    @Override
    @Nullable
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof PasswordClientRegistrationAuthenticationToken)) {
            return null;
        }

        PasswordClientRegistrationAuthenticationToken token = (PasswordClientRegistrationAuthenticationToken) authentication;
        /* Unlike in ClientCredentialsAuthenticationProvider we have to get and pass the Client Registration
           instead of letting the AuthorizedClientManager handle it because we have to modify
           the AccessTokenResponseClient before each request in order to set the BHKNZ header. */
        ClientRegistration clientRegistration = token.getClientRegistration();

        OAuth2AuthorizedClient authorizedClient = obtainAuthorizedClient(clientRegistration, token, token.getUsername(), token.getPassword(), token.getBhknz());
        if (authorizedClient == null) {
            throw new ClientAuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT), clientRegistration.getRegistrationId(),
                    "clientRegistration.authorizationGrantType must be AuthorizationGrantType.PASSWORD");
        }

        return createJwtAuthentication(authorizedClient);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordClientRegistrationAuthenticationToken.class.isAssignableFrom(authentication);
    }

    protected OAuth2AuthorizedClient obtainAuthorizedClient(ClientRegistration clientRegistration, Authentication principal,
                                                            String username, String password, @Nullable String bhknz) {
        Assert.hasText(username, "username cannot be empty for client: " + clientRegistration.getRegistrationId());
        Assert.hasText(password, "password cannot be empty for client: " + clientRegistration.getRegistrationId());

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
            // add a headers converter to the default request entity converter which sets the bhknz header
            passwordTokenResponseClient.setRequestEntityConverter(passwordGrantRequest -> {
                OAuth2PasswordGrantRequestEntityConverter converter = new OAuth2PasswordGrantRequestEntityConverter();
                converter.addHeadersConverter(bhknzHeaderConverterBuilder.buildWith(bhknz));
                return converter.convert(passwordGrantRequest);
            });
        }

        return passwordTokenResponseClient;
    }
}
