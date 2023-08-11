package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.util.StringUtils;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;
import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties.AdditionalRegistrationProperties;

/**
 * Authentication Provider to obtain an {@link Authentication} with the OAuth2 Resource Owner Password Credentials flow.
 */
public class PasswordAuthenticationProvider extends AbstractPasswordAuthenticationProvider {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(PasswordAuthenticationProvider.class);

    /**
     * Repository containing the registered clients.
     * Unlike in {@link ClientCredentialsAuthenticationProvider} we can't use the AuthorizedClientManager because
     * we have to modify the AccessTokenResponseClient before each request in order to set the BHKNZ header.
     */
    private final ClientRegistrationRepository clientRegistrationRepository;

    public PasswordAuthenticationProvider(ClientRegistrationRepository clientRegistrationRepository,
                                          JwtAuthenticationConverter jwtAuthenticationConverter,
                                          IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps) {
        super(jwtAuthenticationConverter, isyOAuth2ClientProps);
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    @Nullable
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof PasswordAuthenticationToken)) {
            return null;
        }

        PasswordAuthenticationToken token = (PasswordAuthenticationToken) authentication;

        String registrationId = token.getRegistrationId();

        AdditionalRegistrationProperties props = isyOAuth2ClientProps.getRegistration().get(registrationId);
        if (props == null) {
            throw new BadCredentialsException(
                    String.format("No configured credentials found for client with registrationId: %s.", registrationId));
        }

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        OAuth2AuthorizedClient authorizedClient = obtainAuthorizedClient(clientRegistration, token, props.getUsername(), props.getPassword(), props.getBhknz());
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
