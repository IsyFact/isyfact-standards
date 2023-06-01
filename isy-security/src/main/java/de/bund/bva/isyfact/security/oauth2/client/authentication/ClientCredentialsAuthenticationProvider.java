package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * Authentication Provider to obtain an {@link Authentication} with the OAuth2 Client Credentials flow
 * using configured ClientRegistrations.
 * <p>
 * Authentications are cached using the {@link OAuth2AuthorizedClientManager}.
 */
public class ClientCredentialsAuthenticationProvider extends IsyOAuth2AuthenticationProvider {

    /** Manager for authorized clients. */
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public ClientCredentialsAuthenticationProvider(OAuth2AuthorizedClientManager authorizedClientManager,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter) {
        super(jwtAuthenticationConverter);
        this.authorizedClientManager = authorizedClientManager;
    }

    @Override
    @Nullable
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof ClientCredentialsAuthenticationToken)) {
            return null;
        }

        ClientCredentialsAuthenticationToken token = (ClientCredentialsAuthenticationToken) authentication;

        String registrationId = token.getRegistrationId();
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(registrationId)
                .principal(token)
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient == null) {
            throw new ClientAuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT), registrationId,
                    "clientRegistration.authorizationGrantType must be AuthorizationGrantType.CLIENT_CREDENTIALS");
        }

        return createJwtAuthentication(authorizedClient);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ClientCredentialsAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
