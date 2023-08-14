package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;
import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties.AdditionalRegistrationProperties;

/**
 * Authentication Provider to obtain an {@link Authentication} with the OAuth2 Resource Owner Password Credentials flow.
 */
public class PasswordAuthenticationProvider extends AbstractPasswordAuthenticationProvider {

    public PasswordAuthenticationProvider(JwtAuthenticationConverter jwtAuthenticationConverter,
                                          IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps) {
        super(jwtAuthenticationConverter, isyOAuth2ClientProps);
    }

    @Override
    @Nullable
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof PasswordAuthenticationToken)) {
            return null;
        }

        PasswordAuthenticationToken token = (PasswordAuthenticationToken) authentication;
        /* Unlike in ClientCredentialsAuthenticationProvider we have to get and pass the Client Registration
           instead of letting the AuthorizedClientManager handle it because we have to modify
           the AccessTokenResponseClient before each request in order to set the BHKNZ header. */
        ClientRegistration clientRegistration = token.getClientRegistration();

        AdditionalRegistrationProperties props = isyOAuth2ClientProps.getRegistration().get(clientRegistration.getRegistrationId());
        if (props == null) {
            throw new BadCredentialsException(
                    String.format("No configured credentials found for client with registrationId: %s.", clientRegistration.getRegistrationId()));
        }

        OAuth2AuthorizedClient authorizedClient = obtainAuthorizedClient(token.getClientRegistration(), token, props.getUsername(), props.getPassword(), props.getBhknz());
        if (authorizedClient == null) {
            throw new ClientAuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT), clientRegistration.getRegistrationId(),
                    "clientRegistration.authorizationGrantType must be AuthorizationGrantType.PASSWORD");
        }

        return createJwtAuthentication(authorizedClient);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
