package de.bund.bva.isyfact.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import de.bund.bva.isyfact.security.autoconfigure.IsySecurityAutoConfiguration;
import de.bund.bva.isyfact.security.core.Berechtigungsmanager;

public class BerechtigungsmanagerTest {

    private Berechtigungsmanager berechtigungsmanager;
    private static final String[] TEST_AUTHORITIES = { "PRIV_test", "PRIV_test3" };
    private static final String WRONG_AUTHORITY = "PRIV_test2";
    private static final String[] TEST_ROLES = { "ROLE_1", "ROLE_2" };
    private JwtAuthenticationToken token;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        token = mock(JwtAuthenticationToken.class);

        Collection<GrantedAuthority> authorities = Arrays.stream(TEST_AUTHORITIES)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        Map<String, Object> tokenAttributes = new HashMap<>();
        tokenAttributes.put("roles", Arrays.asList(TEST_ROLES));

        IsySecurityAutoConfiguration config = new IsySecurityAutoConfiguration();
        berechtigungsmanager = config.berechtigungsmanager(config.isySecurityProperties());
        when(token.getAuthorities()).thenReturn(authorities);
        when(token.getTokenAttributes()).thenReturn(tokenAttributes);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    public void testGetRollen() {
        assertEquals(new HashSet<>(Arrays.asList(TEST_ROLES)), berechtigungsmanager.getRollen());
    }

    @Test
    public void testGetRechte() {
        assertEquals(new HashSet<>(Arrays.asList(TEST_AUTHORITIES)), berechtigungsmanager.getRechte());
    }

    @Test
    public void testHatRechtPositive() {
        assertTrue(berechtigungsmanager.hatRecht(TEST_AUTHORITIES[0]));
    }

    @Test
    public void testHatRechtNegative() {
        assertFalse(berechtigungsmanager.hatRecht(WRONG_AUTHORITY));
    }

    @Test
    public void testPruefeRechtPositive() {
        berechtigungsmanager.pruefeRecht(TEST_AUTHORITIES[0]);
    }

    @Test
    public void testPruefeRechtNegative() {
        assertThrows(AccessDeniedException.class, () -> berechtigungsmanager.pruefeRecht(WRONG_AUTHORITY));
    }

}
