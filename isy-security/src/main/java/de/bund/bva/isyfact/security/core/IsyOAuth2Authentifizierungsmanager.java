package de.bund.bva.isyfact.security.core;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import de.bund.bva.isyfact.security.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.authentication.IsyOAuth2ClientCredentialsAuthenticationToken;
import de.bund.bva.isyfact.security.authentication.IsyOAuth2ManualClientCredentialsAuthenticationToken;
import de.bund.bva.isyfact.security.authentication.IsyOAuth2PasswordAuthenticationToken;

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

    public IsyOAuth2Authentifizierungsmanager(
            AuthenticationManager authenticationManager,
            @Nullable ClientRegistrationRepository clientRegistrationRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public void authentifiziere(String registrationId) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        AuthorizationGrantType grantType = clientRegistration.getAuthorizationGrantType();
        if (grantType.equals(AuthorizationGrantType.CLIENT_CREDENTIALS)) {
            IsyOAuth2ClientCredentialsAuthenticationToken token = new IsyOAuth2ClientCredentialsAuthenticationToken(registrationId);

            authenticate(token);
        } else if (grantType.equals(AuthorizationGrantType.PASSWORD)) {
            IsyOAuth2PasswordAuthenticationToken token = new IsyOAuth2PasswordAuthenticationToken(registrationId);
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

        IsyOAuth2ManualClientCredentialsAuthenticationToken token = new IsyOAuth2ManualClientCredentialsAuthenticationToken(clientRegistration);

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
