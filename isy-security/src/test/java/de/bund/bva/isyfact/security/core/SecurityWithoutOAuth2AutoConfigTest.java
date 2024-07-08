package de.bund.bva.isyfact.security.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import de.bund.bva.isyfact.security.autoconfigure.IsyOAuth2ClientAutoConfiguration;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;

/**
 * Test for {@link Security} if the {@link IsyOAuth2ClientAutoConfiguration} has been excluded.
 */
@SpringBootTest
@EnableAutoConfiguration(exclude = IsyOAuth2ClientAutoConfiguration.class)
public class SecurityWithoutOAuth2AutoConfigTest {

    @Autowired
    protected Berechtigungsmanager contextBerechtigungsmanager;

    @Autowired
    protected Security security;

    @Test
    public void testAuthentifizierungsmanagerNotPresent() {
        Optional<Authentifizierungsmanager> authentifizierungsmanager = security.getAuthentifizierungsmanager();
        assertFalse(authentifizierungsmanager.isPresent());
    }

    @Test
    public void testHasBerechtigungsmanager() {
        Berechtigungsmanager berechtigungsmanager = security.getBerechtigungsmanager();
        assertNotNull(berechtigungsmanager);
        assertEquals(contextBerechtigungsmanager, berechtigungsmanager);
    }

    @Test
    public void testReturnsAllRoles() {
        assertThat(security.getAlleRollen()).containsExactlyInAnyOrder("Rolle_ABC", "Rolle_Keine", "Rolle_A", "Rolle_B", "Rolle_C");
    }

}
