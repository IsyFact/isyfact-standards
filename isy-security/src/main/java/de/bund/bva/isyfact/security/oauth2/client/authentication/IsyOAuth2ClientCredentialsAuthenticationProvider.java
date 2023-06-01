package de.bund.bva.isyfact.security.oauth2.client.authentication;

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
public class IsyOAuth2ClientCredentialsAuthenticationProvider extends AbstractIsyAuthenticationProvider {

    /**
     * Spring bean used to cache authentications.
     */
    private final OAuth2AuthorizedClientManager clientManager;

    public IsyOAuth2ClientCredentialsAuthenticationProvider(
            JwtAuthenticationConverter jwtAuthenticationConverter,
            OAuth2AuthorizedClientManager clientManager
    ) {
        super(jwtAuthenticationConverter);
        this.clientManager = clientManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        IsyOAuth2ClientCredentialsAuthenticationToken token = (IsyOAuth2ClientCredentialsAuthenticationToken) authentication;

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(token.getRegistrationId())
                .principal(token)
                .build();

        OAuth2AuthorizedClient authorizedClient = clientManager.authorize(authorizeRequest);

        if (authorizedClient == null) {
            throw new ClientAuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT), token.getRegistrationId(),
                    "clientRegistration.authorizationGrantType must be AuthorizationGrantType.CLIENT_CREDENTIALS");
        }
        return getAndConvertAccessToken(authorizedClient);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return IsyOAuth2ClientCredentialsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
