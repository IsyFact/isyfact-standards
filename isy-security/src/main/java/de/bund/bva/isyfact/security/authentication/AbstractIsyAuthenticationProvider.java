package de.bund.bva.isyfact.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * Superclass with common methods for all Isy...AuthenticationProviders.
 */
public abstract class AbstractIsyAuthenticationProvider implements AuthenticationProvider {


    /**
     * Converter to create a JwtAuthenticationToken from a JWT.
     */
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    /**
     * Factory for decoding and validating the returned JWT.
     */
    private final JwtDecoderFactory<ClientRegistration> jwtDecoderFactory = new OidcIdTokenDecoderFactory();

    public AbstractIsyAuthenticationProvider(JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    public JwtAuthenticationConverter getJwtAuthenticationConverter() {
        return jwtAuthenticationConverter;
    }

    protected AbstractAuthenticationToken getAndConvertAccessToken(OAuth2AuthorizedClient authorizedClient) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        Jwt jwt = jwtDecoderFactory.createDecoder(authorizedClient.getClientRegistration())
                .decode(accessToken.getTokenValue());

        return getJwtAuthenticationConverter().convert(jwt);
    }
}
