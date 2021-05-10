package de.bund.bva.isyfact.aufrufkontext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextVerwalterImpl;

/**
 * Tests the handling of an OAuth2 Bearer Token in the {@link AufrufKontextVerwalter}.
 */
public class TestBearerTokenHandling {

    /** {@link AufrufKontextVerwalter} under test. */
    private AufrufKontextVerwalter<AufrufKontextImpl> aufrufKontextVerwalter;

    /**
     * Sets up the {@link AufrufKontextVerwalter} under test.
     */
    @Before
    public void setUp() {
        aufrufKontextVerwalter =new AufrufKontextVerwalterImpl<>();
    }

    /**
     * Tests the truncation of the 'bearer ' prefix.
     */
    @Test
    public void testCuttingOutBearer() {
        aufrufKontextVerwalter.setBearerToken("bearer 12345");
        Assert.assertEquals("12345", aufrufKontextVerwalter.getBearerToken());

        aufrufKontextVerwalter.setBearerToken("BEARER 12345");
        Assert.assertEquals("12345", aufrufKontextVerwalter.getBearerToken());

        aufrufKontextVerwalter.setBearerToken("Bearer 12345");
        Assert.assertEquals("12345", aufrufKontextVerwalter.getBearerToken());
    }

}
