package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * Authentication Provider to obtain an {@link Authentication} with the OAuth2 Client Credentials flow
 * using ad hoc created ClientRegistrations.
 */
public class ManualClientCredentialsAuthenticationProvider extends IsyOAuth2AuthenticationProvider {

    /**
     * AuthorizedClientProvider for the client credentials.
     * Unlike in {@link ClientCredentialsAuthenticationProvider} we can't use the AuthorizedClientManager because
     * the client registrations are created manually.
     */
    private final OAuth2AuthorizedClientProvider clientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build();

    public ManualClientCredentialsAuthenticationProvider(JwtAuthenticationConverter jwtAuthenticationConverter) {
        super(jwtAuthenticationConverter);
    }

    @Override
    @Nullable
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof ManualClientCredentialsAuthenticationToken)) {
            return null;
        }

        ManualClientCredentialsAuthenticationToken token = (ManualClientCredentialsAuthenticationToken) authentication;

        OAuth2AuthorizationContext authorizationContext = OAuth2AuthorizationContext.withClientRegistration(token.getClientRegistration())
                .principal(token)
                .build();

        OAuth2AuthorizedClient authorizedClient = clientProvider.authorize(authorizationContext);

        if (authorizedClient == null) {
            throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT),
                    "clientRegistration.authorizationGrantType must be AuthorizationGrantType.CLIENT_CREDENTIALS");
        }

        return createJwtAuthentication(authorizedClient);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ManualClientCredentialsAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
