package de.bund.bva.isyfact.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class IsyOAuth2ClientCredentialsAuthenticationProvider implements AuthenticationProvider {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    /**
     * Factory for decoding and validating the returned JWT.
     */
    private final JwtDecoderFactory<ClientRegistration> jwtDecoderFactory = new OidcIdTokenDecoderFactory();

    public IsyOAuth2ClientCredentialsAuthenticationProvider(
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof IsyOAuth2ClientCredentialsAuthenticationToken)) {
            return null;
        }

        IsyOAuth2ClientCredentialsAuthenticationToken token = (IsyOAuth2ClientCredentialsAuthenticationToken) authentication;

        OAuth2AuthorizationContext authorizationContext = OAuth2AuthorizationContext
                .withClientRegistration(token.getClientRegistration())
                .principal(token)
                .build();

        OAuth2AuthorizedClientProvider clientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        OAuth2AuthorizedClient authorizedClient = clientProvider.authorize(authorizationContext);

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        Jwt jwt = jwtDecoderFactory.createDecoder(token.getClientRegistration())
                .decode(accessToken.getTokenValue());

        return jwtAuthenticationConverter.convert(jwt);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return IsyOAuth2ClientCredentialsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
