package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultPasswordTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
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
public class PasswordAuthenticationProvider extends IsyOAuth2AuthenticationProvider {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(PasswordAuthenticationProvider.class);

    /**
     * Repository containing the registered clients.
     * Unlike in {@link ClientCredentialsAuthenticationProvider} we can't use the AuthorizedClientManager because
     * we have to modify the AccessTokenResponseClient before each request in order to set the BHKNZ header.
     */
    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Global isy-security Configuration properties.
     */
    private final IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps;

    public PasswordAuthenticationProvider(ClientRegistrationRepository clientRegistrationRepository,
                                          JwtAuthenticationConverter jwtAuthenticationConverter,
                                          IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps) {
        super(jwtAuthenticationConverter);
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.isyOAuth2ClientProps = isyOAuth2ClientProps;
    }

    @Override
    @Nullable
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof PasswordAuthenticationToken)) {
            return null;
        }

        PasswordAuthenticationToken passwordAuth = fillCredentialsFromConfigIfNotProvided((PasswordAuthenticationToken) authentication);

        String registrationId = passwordAuth.getRegistrationId();
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        OAuth2AuthorizedClient authorizedClient = obtainAuthorizedClient(clientRegistration, passwordAuth);

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

    private PasswordAuthenticationToken fillCredentialsFromConfigIfNotProvided(PasswordAuthenticationToken passwordAuth) {
        if (StringUtils.hasText(passwordAuth.getUsername())) {
            LOG.debug("Found username in authentication request. Using it as is.");
            return passwordAuth;
        } else {
            LOG.debug("Authentication request does not contain credentials. "
                    + "Trying to create new request with credentials from isy.security.oauth.client properties.");
            String registrationId = passwordAuth.getRegistrationId();
            AdditionalRegistrationProperties props = isyOAuth2ClientProps.getRegistration()
                    .get(registrationId);
            if (props == null) {
                throw new BadCredentialsException(
                        String.format("No configured credentials found for client with registrationId: %s.", registrationId));
            }
            return new PasswordAuthenticationToken(registrationId, props.getUsername(), props.getPassword(), props.getBhknz());
        }
    }

    private OAuth2AuthorizedClient obtainAuthorizedClient(ClientRegistration clientRegistration,
                                                          PasswordAuthenticationToken passwordAuth) {
        OAuth2AuthorizationContext authorizationContext = OAuth2AuthorizationContext.withClientRegistration(clientRegistration)
                .principal(passwordAuth)
                .attribute(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, passwordAuth.getUsername())
                .attribute(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, passwordAuth.getPassword())
                .build();

        OAuth2AccessTokenResponseClient<OAuth2PasswordGrantRequest> responseClient = createPasswordTokenResponseClient(passwordAuth);
        OAuth2AuthorizedClientProvider clientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .password(passwordGrantBuilder -> passwordGrantBuilder.accessTokenResponseClient(responseClient))
                .build();
        return clientProvider.authorize(authorizationContext);
    }

    private OAuth2AccessTokenResponseClient<OAuth2PasswordGrantRequest> createPasswordTokenResponseClient(
            PasswordAuthenticationToken passwordAuth) {
        // we need to create a new client every time because each request has a unique interceptor
        DefaultPasswordTokenResponseClient passwordTokenResponseClient = new DefaultPasswordTokenResponseClient();

        if (passwordAuth.getBhknz() != null) {
            String bhknz = passwordAuth.getBhknz();
            String headerValue = String.format("%s:%s", bhknz, isyOAuth2ClientProps.getDefaultCertificateOu());

            // add a headers converter to the default request entity converter which sets the bhknz header
            passwordTokenResponseClient.setRequestEntityConverter(passwordGrantRequest -> {
                OAuth2PasswordGrantRequestEntityConverter converter = new OAuth2PasswordGrantRequestEntityConverter();
                converter.addHeadersConverter(addBhknzHeader(headerValue));
                return converter.convert(passwordGrantRequest);
            });
        }

        return passwordTokenResponseClient;
    }

    private Converter<OAuth2PasswordGrantRequest, HttpHeaders> addBhknzHeader(String headerValue) {
        return (request) -> {
            HttpHeaders headers = new HttpHeaders();
            headers.add(isyOAuth2ClientProps.getBhknzHeaderName(), headerValue);
            return headers;
        };
    }

}
