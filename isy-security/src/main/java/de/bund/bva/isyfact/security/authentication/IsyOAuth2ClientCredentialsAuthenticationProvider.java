package de.bund.bva.isyfact.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class IsyOAuth2ClientCredentialsAuthenticationProvider implements AuthenticationProvider {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    /**
     * Factory for decoding and validating the returned JWT.
     */
    private final JwtDecoderFactory<ClientRegistration> jwtDecoderFactory = new OidcIdTokenDecoderFactory();

    public IsyOAuth2ClientCredentialsAuthenticationProvider(
            JwtAuthenticationConverter jwtAuthenticationConverter,
            OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager
    ) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof IsyOAuth2ClientCredentialsAuthenticationToken)) {
            return null;
        }

        IsyOAuth2ClientCredentialsAuthenticationToken clientCredentialsAuth = (IsyOAuth2ClientCredentialsAuthenticationToken) authentication;

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientCredentialsAuth.getClientRegistration().getRegistrationId())
                .principal(clientCredentialsAuth)
                .build();

        OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientManager.authorize(authorizeRequest);
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        Jwt jwt = jwtDecoderFactory.createDecoder(clientCredentialsAuth.getClientRegistration())
                .decode(accessToken.getTokenValue());

        return jwtAuthenticationConverter.convert(jwt);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return IsyOAuth2ClientCredentialsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
