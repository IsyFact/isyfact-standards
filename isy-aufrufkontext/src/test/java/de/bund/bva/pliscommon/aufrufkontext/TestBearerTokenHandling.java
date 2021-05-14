package de.bund.bva.pliscommon.aufrufkontext;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextVerwalterImpl;

/**
 * Test for the handling of an OAuth 2 Bearer Token in the {@link AufrufKontextVerwalter}.
 */
public class TestBearerTokenHandling {

    private AufrufKontextVerwalter<AufrufKontextImpl> aufrufKontextVerwalter;

    @Before
    public void setUp() {
        aufrufKontextVerwalter =new AufrufKontextVerwalterImpl<>();
    }

    /**
     * Tests the correct removal of the "Bearer " prefix.
     */
    @Test
    public void testCuttingOutBearer() {
        aufrufKontextVerwalter.setBearerToken("bearer 12345");
        assertEquals("12345", aufrufKontextVerwalter.getBearerToken());

        aufrufKontextVerwalter.setBearerToken("BEARER 2345");
        assertEquals("2345", aufrufKontextVerwalter.getBearerToken());

        aufrufKontextVerwalter.setBearerToken("Bearer 12345");
        assertEquals("12345", aufrufKontextVerwalter.getBearerToken());

        aufrufKontextVerwalter.setBearerToken("2345");
        assertEquals("2345", aufrufKontextVerwalter.getBearerToken());
    }

    /**
     * Tests that null assignment works.
     */
    @Test
    public void testResettingToNull() {
        aufrufKontextVerwalter.setBearerToken("bearer 12345");
        assertEquals("12345", aufrufKontextVerwalter.getBearerToken());

        aufrufKontextVerwalter.setBearerToken(null);
        assertNull(aufrufKontextVerwalter.getBearerToken());
    }

}
