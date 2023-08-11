package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;

/**
 * Authentication Provider to obtain an {@link Authentication} with the OAuth2 Resource Owner Password Credentials flow
 * using ad hoc created ClientRegistrations.
 */
public class ManualPasswordAuthenticationProvider extends AbstractPasswordAuthenticationProvider {

    public ManualPasswordAuthenticationProvider(JwtAuthenticationConverter jwtAuthenticationConverter,
                                                IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps) {
        super(jwtAuthenticationConverter, isyOAuth2ClientProps);
    }

    @Override
    @Nullable
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof ManualPasswordAuthenticationToken)) {
            return null;
        }

        ManualPasswordAuthenticationToken token = (ManualPasswordAuthenticationToken) authentication;

        ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation(token.getIssuerLocation())
                .clientId(token.getClientId())
                .clientSecret(token.getClientSecret())
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .build();

        OAuth2AuthorizedClient authorizedClient = obtainAuthorizedClient(clientRegistration, token, token.getUsername(), token.getPassword(), token.getBhknz());
        if (authorizedClient == null) {
            throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT),
                    "clientRegistration.authorizationGrantType must be AuthorizationGrantType.PASSWORD");
        }

        return createJwtAuthentication(authorizedClient);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ManualPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
