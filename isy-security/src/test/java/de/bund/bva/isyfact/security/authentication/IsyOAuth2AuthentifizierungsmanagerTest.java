package de.bund.bva.isyfact.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.example.IsySecurityTestApplication;
import de.bund.bva.isyfact.security.example.config.OAuth2WebClientConfiguration;
import de.bund.bva.isyfact.security.example.config.SecurityConfig;
import de.bund.bva.isyfact.security.example.rest.ExampleRestController;

@ActiveProfiles("test-clients")
@SpringBootTest(
        classes = {
                IsySecurityTestApplication.class,
                SecurityConfig.class,
                ExampleRestController.class,
                OAuth2WebClientConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
// clear context so WebClient will fetch a fresh access token
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IsyOAuth2AuthentifizierungsmanagerTest extends AbstractOidcProviderTest {

    private final String TEST_CLIENT_REGISTRATION_ID = "testclient";
    private final String TEST_CLIENT_ID = "client-credentials-test-client";
    private final String TEST_CLIENT_SECRET = "supersecretpassword";
    private final Set<String> TEST_CLIENT_ROLE = Collections.singleton("Rolle_A");

    @LocalServerPort
    private int port;

    @Autowired
    Authentifizierungsmanager authentifizierungsmanager;

    @BeforeEach
    public void setup() {
        embeddedOidcProvider.removeAllClients();
        embeddedOidcProvider.addClient(
                TEST_CLIENT_ID,
                TEST_CLIENT_SECRET,
                TEST_CLIENT_ROLE
        );
    }

    @Test
    void authentifiziereClientWithRegistrationId(@Autowired ApplicationContext context) {
        assertThat(context).isNotNull();
        authentifizierungsmanager.authentifiziere(TEST_CLIENT_REGISTRATION_ID);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertThat(securityContext).isNotNull();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication).isInstanceOf(JwtAuthenticationToken.class);
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        assertThat(jwt.getToken().getClaims().get("azp")).isEqualTo(TEST_CLIENT_ID);
        List<String> grantedAuthorityNames = jwt.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertThat(grantedAuthorityNames).containsOnly("PRIV_Recht_A");
    }

    @Test
    void authentifiziereSystemWithRegistrationId(@Autowired ApplicationContext context) {
        assertThat(context).isNotNull();
    }

    @Test
    void authentifiziereClientWithClientIdAndSecret(@Autowired ApplicationContext context) {
        authentifizierungsmanager.authentifiziereClient(
                TEST_CLIENT_ID,
                TEST_CLIENT_SECRET,
                getIssuerLocation()
        );
        assertThat(context).isNotNull();
    }
}