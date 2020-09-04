package de.bund.bva.pliscommon.aufrufkontext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextVerwalterImpl;

public class TestBearerTokenHandling {

    private AufrufKontextVerwalter<AufrufKontextImpl> aufrufKontextVerwalter;

    @Before
    public void setUp() {
        aufrufKontextVerwalter =new AufrufKontextVerwalterImpl<>();
    }

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
