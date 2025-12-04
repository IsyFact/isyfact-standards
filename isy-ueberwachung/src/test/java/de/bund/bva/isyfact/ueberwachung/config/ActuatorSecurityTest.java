package de.bund.bva.isyfact.ueberwachung.config;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.reactive.server.WebTestClient;

import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyActuatorSecurityAutoConfiguration;

import java.util.Arrays;

/**
 * Checks whether a response is received with authentication and whether it is rejected as 401 without authentication.
 */
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = {IsyActuatorSecurityAutoConfiguration.class},
    properties = {
        "isy.logging.anwendung.name=ActuatorSecurityTest",
        "isy.logging.anwendung.version=0.0.0-SNAPSHOT",
        "isy.logging.anwendung.typ=JUnit5Test"
    }
)
@EnableAutoConfiguration
class ActuatorSecurityTest {

    @Autowired
    WebTestClient webClient;



    @Autowired
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    private void setSecurityContext(String login, String name, String bhknz, String... roles) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(StandardClaimNames.PREFERRED_USERNAME, login)
                .claim(StandardClaimNames.NAME, name)
                .claim("roles", Arrays.asList(roles))
                .claim("bhknz", bhknz)
                .build();

        AbstractOAuth2TokenAuthenticationToken<?> authentication = new JwtAuthenticationToken(jwt, jwtAuthenticationConverter.convert(jwt).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    //Test actuator with old basic auth
    @Test
    void actuator() {
        webClient.get().uri("/actuator")
                .headers(headers -> headers.setBasicAuth("dummy","dummy"))
                .exchange().expectStatus().isUnauthorized();
    }

    //Test actuator with invalid token
    @Test
    void actuatormitToken() {
        setSecurityContext("test","test","300020");

        webClient.get().uri("/actuator/metrics")
            .headers(headers -> headers.setBearerAuth("test")
            )
            .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void unauthorized() {
        webClient.get().uri("/actuator")
            .exchange().expectStatus().isUnauthorized();
    }
}
