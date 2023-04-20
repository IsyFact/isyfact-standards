package de.bund.bva.isyfact.security.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultPasswordTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.util.StringUtils;

import de.bund.bva.isyfact.security.config.IsyOAuth2ClientProperties;

/**
 * Authentication Provider to obtain an {@link Authentication} with the OAuth2 Resource Owner Password Credentials flow.
 */
public class IsyOAuth2PasswordAuthenticationProvider extends AbstractIsyAuthenticationProvider {

    /**
     * Repository containing the registered clients.
     */
    private final ClientRegistrationRepository clientRegistrationRepository;


    /**
     * Global isy-security Configuration properties.
     */
    private final IsyOAuth2ClientProperties properties;

    public IsyOAuth2PasswordAuthenticationProvider(ClientRegistrationRepository clientRegistrationRepository, JwtAuthenticationConverter jwtAuthenticationConverter, IsyOAuth2ClientProperties properties) {
        super(jwtAuthenticationConverter);
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.properties = properties;
    }

    public Authentication authenticate(String username, String password, String registrationId) throws AuthenticationException {
        return authenticate(new IsyOAuth2PasswordAuthenticationToken(username, password, registrationId, null));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof IsyOAuth2PasswordAuthenticationToken)) {
            return null;
        }

        IsyOAuth2PasswordAuthenticationToken passwordAuth = (IsyOAuth2PasswordAuthenticationToken) authentication;

        String registrationId = passwordAuth.getRegistrationId();
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        return obtainAccessToken(clientRegistration, passwordAuth);
    }

    @Override
    public boolean supports(Class authentication) {
        return IsyOAuth2PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private AbstractAuthenticationToken obtainAccessToken(ClientRegistration clientRegistration, IsyOAuth2PasswordAuthenticationToken authentication) {
        IsyOAuth2PasswordAuthenticationToken token;

        if (!StringUtils.hasText(authentication.getUsername())) {
            IsyOAuth2ClientProperties.IsyClientRegistration isyClientRegistration = properties.getRegistration().get(authentication.getRegistrationId());
            token = new IsyOAuth2PasswordAuthenticationToken(isyClientRegistration.getUsername(), isyClientRegistration.getPassword(), authentication.getRegistrationId(), isyClientRegistration.getBhknz());
        } else {
            token = authentication;
        }

        String username = token.getPrincipal().toString();
        String password = token.getCredentials().toString();

        OAuth2AuthorizationContext authorizationContext = OAuth2AuthorizationContext.withClientRegistration(clientRegistration).principal(token).attribute(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username).attribute(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password).build();

        DefaultPasswordTokenResponseClient accessTokenResponseClient = new DefaultPasswordTokenResponseClient();
        if (token.getBhknz() != null) {
            OAuth2PasswordGrantRequestEntityConverter entityConverter = new OAuth2PasswordGrantRequestEntityConverter();
            entityConverter.addHeadersConverter(source -> {
                HttpHeaders headers = new HttpHeaders();
                headers.set(properties.getBhknzHeaderName(), String.format("%s:%s", token.getBhknz(), properties.getDefaultCertificateOu()));
                return headers;
            });
            accessTokenResponseClient.setRequestEntityConverter(entityConverter);
        }

        OAuth2AuthorizedClientProvider clientProvider = OAuth2AuthorizedClientProviderBuilder.builder().password(passwordGrantBuilder -> passwordGrantBuilder.accessTokenResponseClient(accessTokenResponseClient)).build();
        OAuth2AuthorizedClient authorizedClient = clientProvider.authorize(authorizationContext);

        if (authorizedClient == null) {
            throw new ClientAuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT), clientRegistration.getRegistrationId(), "clientRegistration.authorizationGrantType must be AuthorizationGrantType.PASSWORD");
        }
        return getAndConvertAccessToken(authorizedClient);
    }
}
