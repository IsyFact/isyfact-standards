package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * Base class for an AuthenticationProvider that creates a {@link org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken}
 * from an {@link OAuth2AuthorizedClient}.
 */
public abstract class IsyOAuth2AuthenticationProvider implements AuthenticationProvider {

    /** Converter to create a JwtAuthenticationToken from a JWT. */
    protected final JwtAuthenticationConverter jwtAuthenticationConverter;

    /** Factory for decoding and validating the returned JWT. */
    @Autowired
    private JwtDecoderFactory<ClientRegistration> jwtDecoderFactory;

    public IsyOAuth2AuthenticationProvider(JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    protected Authentication createJwtAuthentication(OAuth2AuthorizedClient authorizedClient) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        ClientRegistration clientRegistration = authorizedClient.getClientRegistration();
        Jwt jwt = jwtDecoderFactory.createDecoder(clientRegistration).decode(accessToken.getTokenValue());

        return jwtAuthenticationConverter.convert(jwt);
    }

}
