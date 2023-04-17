package de.bund.bva.isyfact.security.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultPasswordTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * Authentication Provider to obtain an {@link Authentication} with the OAuth2 Resource Owner Password Credentials flow.
 */
public class IsyOAuth2PasswordAuthenticationProvider implements AuthenticationProvider {

    /**
     * Repository containing the registered clients.
     */
    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Converter to create a JwtAuthenticationToken from a JWT.
     */
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    /**
     * AccessTokenResponseClient for the Password grant.
     */
    private final DefaultPasswordTokenResponseClient accessTokenResponseClient = new DefaultPasswordTokenResponseClient();

    /**
     * Factory for decoding and validating the returned JWT.
     */
    private final JwtDecoderFactory<ClientRegistration> jwtDecoderFactory = new OidcIdTokenDecoderFactory();

    public IsyOAuth2PasswordAuthenticationProvider(
            ClientRegistrationRepository clientRegistrationRepository,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
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

        OAuth2AccessToken accessToken = obtainAccessToken(clientRegistration, passwordAuth);
        Jwt jwt = jwtDecoderFactory.createDecoder(clientRegistration).decode(accessToken.getTokenValue());

        return jwtAuthenticationConverter.convert(jwt);
    }

    @Override
    public boolean supports(Class authentication) {
        return IsyOAuth2PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private OAuth2AccessToken obtainAccessToken(ClientRegistration clientRegistration, IsyOAuth2PasswordAuthenticationToken authentication) {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        OAuth2AuthorizationContext authorizationContext = OAuth2AuthorizationContext.withClientRegistration(clientRegistration)
                .principal(authentication)
                .attribute(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username)
                .attribute(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password)
                .build();

        // TODO: add bhknz header if present
        if (authentication.getBhknz() != null) {
            OAuth2PasswordGrantRequestEntityConverter entityConverter = new OAuth2PasswordGrantRequestEntityConverter();
            entityConverter.addHeadersConverter(
                    source -> {
                        HttpHeaders headers = new HttpHeaders();
                        headers.set("x-client-cert-bhknz", String.format("%s:%s", authentication.getBhknz(), "TESTOU"));
                        return headers;
                    }
            );
            accessTokenResponseClient.setRequestEntityConverter(entityConverter);
        }

        OAuth2AuthorizedClientProvider clientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .password(passwordGrantBuilder -> passwordGrantBuilder.accessTokenResponseClient(accessTokenResponseClient))
                .build();
        OAuth2AuthorizedClient authorizedClient = clientProvider.authorize(authorizationContext);

        if (authorizedClient == null) {
            throw new ClientAuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT), clientRegistration.getRegistrationId(),
                    "clientRegistration.authorizationGrantType must be AuthorizationGrantType.PASSWORD");
        }

        return authorizedClient.getAccessToken();
    }
}
