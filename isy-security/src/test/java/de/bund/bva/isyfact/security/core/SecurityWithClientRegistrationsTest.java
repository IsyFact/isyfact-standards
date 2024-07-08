package de.bund.bva.isyfact.security.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;

/**
 * Test for {@link Security} with all autoconfigurations.
 */
@SpringBootTest
public class SecurityWithClientRegistrationsTest {

    @Autowired
    private Authentifizierungsmanager contextAuthentifizierungsmanager;

    @Autowired
    private Berechtigungsmanager contextBerechtigungsmanager;

    @Autowired
    private Security security;

    @Test
    public void testHasAuthentifizierungsmanager() {
        Optional<Authentifizierungsmanager> authentifizierungsmanager = security.getAuthentifizierungsmanager();
        assertTrue(authentifizierungsmanager.isPresent());
        assertEquals(contextAuthentifizierungsmanager, authentifizierungsmanager.get());
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
