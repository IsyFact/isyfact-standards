package de.bund.bva.isyfact.security.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.StringUtils;

import de.bund.bva.isyfact.security.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.config.IsyOAuth2ClientProperties;

/**
 * Default implementation of {@link Authentifizierungsmanager} for IsyFact applications.
 * Provides methods for Authentication of clients using preconfigured {@link ClientRegistration}s
 * with preconfigured or dynamic Credentials.
 */
public class IsyOAuth2Authentifizierungsmanager implements Authentifizierungsmanager {

    /**
     * Autowired AuthenticationManager.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Autowired ClientRegistrationRepository.
     */
    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Autowired IsyOAuth2ClientProperties.
     */
    private final IsyOAuth2ClientProperties isyOAuth2ClientProperties;

    public IsyOAuth2Authentifizierungsmanager(
            AuthenticationManager authenticationManager,
            ClientRegistrationRepository clientRegistrationRepository,
            IsyOAuth2ClientProperties isyOAuth2ClientProperties
    ) {
        this.authenticationManager = authenticationManager;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.isyOAuth2ClientProperties = isyOAuth2ClientProperties;
    }

    @Override
    public void authentifiziere(String registrationId) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        AuthorizationGrantType grantType = clientRegistration.getAuthorizationGrantType();
        if (grantType.equals(AuthorizationGrantType.CLIENT_CREDENTIALS)) {
            IsyOAuth2ClientCredentialsAuthenticationToken token = new IsyOAuth2ClientCredentialsAuthenticationToken(clientRegistration);

            authenticate(token);
        } else if (grantType.equals(AuthorizationGrantType.PASSWORD)) {
            IsyOAuth2ClientProperties.IsyClientRegistration isyClientRegistration = isyOAuth2ClientProperties.getRegistration().getOrDefault(registrationId, null);
            // TODO: throw AuthentifizierungFehlgeschlagenException/AuthentifizierungTechnicalException
            if (isyClientRegistration == null) {
                throw new AssertionError("An IsyClientRegistration with registrationID: " + registrationId + " has to be defined.");
            }
            if (!StringUtils.hasText(isyClientRegistration.getUsername())) {
                throw new AssertionError("Username for IsyClientRegistration with registrationID: " + registrationId + " has to be defined.");
            }
            if (!StringUtils.hasText(isyClientRegistration.getPassword())) {
                throw new AssertionError("Password for IsyClientRegistration with registrationID: " + registrationId + " has to be defined.");
            }
            IsyOAuth2PasswordAuthenticationToken token = new IsyOAuth2PasswordAuthenticationToken(
                    isyClientRegistration.getUsername(),
                    isyClientRegistration.getPassword(),
                    registrationId,
                    isyClientRegistration.getBhknz()
            );
            authenticate(token);
        }
    }

    @Override
    public void authentifiziereClient(String clientId, String clientSecret, String issuerLocation) {
        ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation(issuerLocation)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();

        IsyOAuth2ClientCredentialsAuthenticationToken token = new IsyOAuth2ClientCredentialsAuthenticationToken(clientRegistration);

        authenticate(token);
    }

    @Override
    public void authentifiziereSystem(String username, String password, String registrationId, @Nullable String bhknz) {
        IsyOAuth2PasswordAuthenticationToken token = new IsyOAuth2PasswordAuthenticationToken(username, password, registrationId, bhknz);

        authenticate(token);
    }

    private void authenticate(Authentication unauthenticatedToken) {
        Authentication authentication = authenticationManager.authenticate(unauthenticatedToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
