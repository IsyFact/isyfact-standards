package de.bund.bva.isyfact.security.oauth2.client.authentication;

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
public class IsyOAuth2ManualClientCredentialsAuthenticationProvider extends AbstractIsyAuthenticationProvider {

    public IsyOAuth2ManualClientCredentialsAuthenticationProvider(
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) {
        super(jwtAuthenticationConverter);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        IsyOAuth2ManualClientCredentialsAuthenticationToken token = (IsyOAuth2ManualClientCredentialsAuthenticationToken) authentication;

        OAuth2AuthorizationContext authorizationContext = OAuth2AuthorizationContext
                .withClientRegistration(token.getClientRegistration())
                .principal(token)
                .build();

        OAuth2AuthorizedClientProvider clientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        OAuth2AuthorizedClient authorizedClient = clientProvider.authorize(authorizationContext);

        if (authorizedClient == null) {
            throw new OAuth2AuthorizationException(
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT),
                    "authorization is not supported for the specified client"
            );
        }
        return getAndConvertAccessToken(authorizedClient);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return IsyOAuth2ManualClientCredentialsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
