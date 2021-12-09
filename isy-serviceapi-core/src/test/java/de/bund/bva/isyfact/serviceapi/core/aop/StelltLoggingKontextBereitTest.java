package de.bund.bva.isyfact.serviceapi.core.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.serviceapi.core.aop.service.httpinvoker.v1_0_0.DummyKontextServiceImpl;
import de.bund.bva.isyfact.serviceapi.core.aop.service.httpinvoker.v1_0_0.DummyKontextServiceRemoteBean;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Tested, ob der {@link StelltLoggingKontextBereitInterceptor} korrekt durch die Annnotation an der Methode
 * {@code ping()} der Klasse {@link DummyKontextServiceImpl} aufgerufen wird.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StelltLoggingKontextBereitTest.TestConfig.class,
properties = {"isy.logging.autoconfiguration.enabled=false"})
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

    @Configuration
    @EnableAutoConfiguration
    public static class TestConfig {

        @Bean
        public DummyKontextServiceImpl dummyKontextService() {
            return new DummyKontextServiceImpl();
        }
    }
}
