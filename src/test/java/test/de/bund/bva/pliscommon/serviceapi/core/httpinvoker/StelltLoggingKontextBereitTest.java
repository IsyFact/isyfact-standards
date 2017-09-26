package test.de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bund.bva.pliscommon.serviceapi.core.aufrufkontext.StelltLoggingKontextBereitInterceptor;

import test.de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.DummyServiceImpl;
import test.de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.DummyServiceRemoteBean;

/**
 * Tested, ob der {@link StelltLoggingKontextBereitInterceptor} korrekt durch die Annnotation an der Methode
 * {@code ping()} der Klasse {@link DummyServiceImpl} aufgerufen wird.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/remoting-servlet.xml" })
public class StelltLoggingKontextBereitTest {

    @Autowired
    DummyServiceRemoteBean dummyService;

    @Test(expected = IllegalArgumentException.class)
    public void testAnnotationFunktioniert() {
        this.dummyService.pingMitAufrufKontext("test");

    }
}
