package de.bund.bva.isyfact.security.core;

import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.Authentifizierungsmanager;

@ActiveProfiles("test-clients")
@SpringBootTest(classes = {IsyOAuth2AuthentifizierungsmanagerTest.TestConfig.class})
class IsyOAuth2AuthentifizierungsmanagerTest extends AbstractOidcProviderTest {

    // Test data for client credentials flow
    private static final String CC_TEST_CLIENT_REGISTRATION_ID = "testclient";
    private static final String CC_TEST_CLIENT_ID = "client-credentials-test-client";
    private static final String CC_TEST_CLIENT_SECRET = "supersecretpassword";

    // Test data for ressource owner password credentials flow
    private static final String ROPC_TEST_CLIENT_REGISTRATION_ID = "ropc-client";
    private static final String ROPC_TEST_CLIENT_ID = "ressource-owner-password-credentials-test-client";
    private static final String ROPC_TEST_CLIENT_SECRET = "hypersecretpassword";
    private static final String ROPC_TEST_USERNAME = "admin";
    private static final String ROPC_TEST_PASSWORD = "admin123";
    private static final String ROPC_TEST_BHKNZ = "testbhknz";

    // Test data for ressource owner password credentials flow (without bhknz)
    private static final String ROPC_NO_BHKNZ_TEST_CLIENT_REGISTRATION_ID = "ropc-client-no-bhknz";
    private static final String ROPC_NO_BHKNZ_TEST_CLIENT_ID = "ropc-no-bhknz-test-client";
    private static final String ROPC_NO_BHKNZ_TEST_CLIENT_SECRET = "superhypersecretpassword";
    private static final String ROPC_NO_BHKNZ_TEST_USERNAME = "adminNoBhknz";
    private static final String ROPC_NO_BHKNZ_TEST_PASSWORD = "passwordNoBhknz";

    private static final Set<String> TEST_CLIENT_ROLE = Collections.singleton("Rolle_A");

    @Autowired
    Authentifizierungsmanager authentifizierungsmanager;

    @BeforeAll
    public static void setup() {
        // client with authorization-grant-type=password
        embeddedOidcProvider.addUser(
                ROPC_TEST_CLIENT_ID,
                ROPC_TEST_CLIENT_SECRET,
                ROPC_TEST_USERNAME,
                ROPC_TEST_PASSWORD,
                Optional.of(ROPC_TEST_BHKNZ),
                TEST_CLIENT_ROLE
        );
        // client with authorization-grant-type=password (without bhknz)
        embeddedOidcProvider.addUser(
                ROPC_NO_BHKNZ_TEST_CLIENT_ID,
                ROPC_NO_BHKNZ_TEST_CLIENT_SECRET,
                ROPC_NO_BHKNZ_TEST_USERNAME,
                ROPC_NO_BHKNZ_TEST_PASSWORD,
                Optional.empty(),
                TEST_CLIENT_ROLE
        );
        // client with authorization-grant-type=client_credentials
        embeddedOidcProvider.addClient(
                CC_TEST_CLIENT_ID,
                CC_TEST_CLIENT_SECRET,
                TEST_CLIENT_ROLE
        );
    }

    @Test
    void authentifiziereClientWithRegistrationId() {
        authentifizierungsmanager.authentifiziere(CC_TEST_CLIENT_REGISTRATION_ID);

        assertSecurityContextForClientId(CC_TEST_CLIENT_ID);

        // Check that our implementation makes use of the spring caching
        // authenticate a second time:
        authentifizierungsmanager.authentifiziere(CC_TEST_CLIENT_REGISTRATION_ID);
        // expect only one request to the IAM-Sever:
        verify(exactly(1), postRequestedFor(urlEqualTo("/auth/realms/testrealm/protocol/openid-connect/token")));
    }

    @Test
    void authentifiziereSystemWithRegistrationId() {
        authentifizierungsmanager.authentifiziere(ROPC_TEST_CLIENT_REGISTRATION_ID);

        assertSecurityContextForClientId(ROPC_TEST_CLIENT_ID);
    }

    @Test
    void authentifiziereSystemWithRegistrationIdOhneBhknz() {
        authentifizierungsmanager.authentifiziere(ROPC_NO_BHKNZ_TEST_CLIENT_REGISTRATION_ID);

        assertSecurityContextForClientId(ROPC_NO_BHKNZ_TEST_CLIENT_ID);
    }

    @Test
    void authentifiziereClientWithClientIdAndSecret() {
        authentifizierungsmanager.authentifiziereClient(
                CC_TEST_CLIENT_ID,
                CC_TEST_CLIENT_SECRET,
                getIssuerLocation()
        );

        assertSecurityContextForClientId(CC_TEST_CLIENT_ID);
    }

    @Test
    void authentifiziereSystemWithUsernameAndPassword() {
        authentifizierungsmanager.authentifiziereSystem(
                ROPC_TEST_USERNAME,
                ROPC_TEST_PASSWORD,
                ROPC_TEST_CLIENT_REGISTRATION_ID,
                ROPC_TEST_BHKNZ
        );

        assertSecurityContextForClientId(ROPC_TEST_CLIENT_ID);
    }

    @Test
    void authentifiziereSystemWithUsernameAndPasswordOhneBhknz() {
        authentifizierungsmanager.authentifiziereSystem(
                ROPC_NO_BHKNZ_TEST_USERNAME,
                ROPC_NO_BHKNZ_TEST_PASSWORD,
                ROPC_NO_BHKNZ_TEST_CLIENT_REGISTRATION_ID,
                null
        );

        assertSecurityContextForClientId(ROPC_NO_BHKNZ_TEST_CLIENT_ID);
    }

    private void assertSecurityContextForClientId(String clientId) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertThat(securityContext).isNotNull();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication).isInstanceOf(JwtAuthenticationToken.class);
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        assertThat(jwt.getToken().getClaims().get("azp")).isEqualTo(clientId);
        List<String> grantedAuthorityNames = jwt.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertThat(grantedAuthorityNames).containsOnly("PRIV_Recht_A");
    }

    @Configuration
    @EnableAutoConfiguration
    public static class TestConfig {
    }
}