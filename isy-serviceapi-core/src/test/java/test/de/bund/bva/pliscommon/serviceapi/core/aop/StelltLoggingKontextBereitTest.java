package test.de.bund.bva.pliscommon.serviceapi.core.aop;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

import test.de.bund.bva.pliscommon.serviceapi.core.aop.service.httpinvoker.v1_0_0.DummyKontextServiceImpl;
import test.de.bund.bva.pliscommon.serviceapi.core.aop.service.httpinvoker.v1_0_0.DummyKontextServiceRemoteBean;

/**
 * Tested, ob der {@link de.bund.bva.pliscommon.serviceapi.core.aop.StelltLoggingKontextBereitInterceptor} korrekt
 * durch die Annotation an der Methode {@code ping()} der Klasse {@link DummyKontextServiceImpl} aufgerufen wird.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/remoting-servlet.xml" })
public class StelltLoggingKontextBereitTest {

    @Autowired
    DummyKontextServiceRemoteBean dummyService;

    private AufrufKontextTo aufrufKontext;

    @Before
    public void setUp() {
        this.aufrufKontext = new AufrufKontextTo();
        this.aufrufKontext.setKorrelationsId(UUID.randomUUID().toString());
    }

    @After
    public void tearDown() {
        this.aufrufKontext = null;
    }

    @Test
    public void testStelltLoggingKontextNichtBereitOhneAufrufKontext() {
        String korrelationsId = this.dummyService.stelltLoggingKontextNichtBereitOhneAufrufKontext();
        assertEquals(korrelationsId, null);
    }

    @Test
    public void testStelltLoggingKontextNichtBereitMitAufrufKontext() {
        String korrelationsId =
            this.dummyService.stelltLoggingKontextNichtBereitMitAufrufKontext(this.aufrufKontext);
        assertEquals(korrelationsId, null);
    }

    @Test
    public void testStelltLoggingKontextBereitOhneAufrufKontextErwartet() {
        String korrelationsId = this.dummyService.stelltLoggingKontextBereitOhneAufrufKontextErwartet();
        assertNotEquals(korrelationsId, this.aufrufKontext.getKorrelationsId());
        assertNotNull(korrelationsId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStelltLoggingKontextBereitOhneAufrufKontextNichtErwartet() {
        this.dummyService.stelltLoggingKontextBereitOhneAufrufKontextNichtErwartet();
    }

    @Test
    public void testStelltLoggingKontextBereitMitAufrufKontext() {
        String korrelationsId =
            this.dummyService.stelltLoggingKontextBereitMitAufrufKontext(this.aufrufKontext);
        assertEquals(korrelationsId, this.aufrufKontext.getKorrelationsId());
    }

}
