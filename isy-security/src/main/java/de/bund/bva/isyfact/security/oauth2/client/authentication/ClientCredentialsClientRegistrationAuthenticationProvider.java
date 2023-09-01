package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import de.bund.bva.isyfact.security.oauth2.client.authentication.token.ClientCredentialsClientRegistrationAuthenticationToken;

/**
 * Authentication Provider to obtain an {@link Authentication} with the OAuth2 Client Credentials flow
 * using an externally created Client Registration object.
 */
public class ClientCredentialsClientRegistrationAuthenticationProvider extends IsyOAuth2AuthenticationProvider {

    /**
     * AuthorizedClientProvider for the client credentials.
     * Unlike in {@link ClientCredentialsAuthorizedClientAuthenticationProvider} we can't use the AuthorizedClientManager because
     * the client registrations are created programmatically from given runtime-parameters.
     */
    private final OAuth2AuthorizedClientProvider clientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build();

    public ClientCredentialsClientRegistrationAuthenticationProvider(JwtAuthenticationConverter jwtAuthenticationConverter) {
        super(jwtAuthenticationConverter);
    }

    @Override
    @Nullable
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof ClientCredentialsClientRegistrationAuthenticationToken)) {
            return null;
        }

        ClientCredentialsClientRegistrationAuthenticationToken token = (ClientCredentialsClientRegistrationAuthenticationToken) authentication;

        ClientRegistration clientRegistration = token.getClientRegistration();

        OAuth2AuthorizationContext authorizationContext = OAuth2AuthorizationContext.withClientRegistration(clientRegistration)
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient authorizedClient = clientProvider.authorize(authorizationContext);
        /* The authorized client could theoretically be null if a valid token already exists,
           but since the OAuth2AuthorizationContext does not use authorized clients and the
           OAuth2AuthorizationContext is only configured for a single grant type this can currently not be the case. */
        if (authorizedClient == null) {
            throw new ClientAuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT),
                    "clientRegistration.authorizationGrantType must be AuthorizationGrantType.CLIENT_CREDENTIALS");
        }

        return createJwtAuthentication(authorizedClient);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ClientCredentialsClientRegistrationAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
