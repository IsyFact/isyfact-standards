package de.bund.bva.isyfact.serviceapi.core.aufrufkontext;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.serviceapi.core.aop.test.AufrufKontextSstTestBean;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.helper.DebugAufrufKontextVerwalter;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

import junit.framework.AssertionFailedError;

/**
 * Tests the functionality of {@link StelltAufrufKontextBereitInterceptor}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"isy.logging.autoconfiguration.enabled=false"})
public class AufrufKontextInterceptorAnnotationTest {

    /**
     * Access to the {@link de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter} to check if the {@link AufrufKontext} is set / not set correctly.
     */
    @Autowired
    public DebugAufrufKontextVerwalter aufrufKontextVerwalter;

    /**
     * Simulation of a service interface to which calls are made.
     */
    @Autowired
    public AufrufKontextSstTestBean sst;

    /**
     * creates a {@link AufrufKontextTo}.
     */
    private AufrufKontextTo createAufrufKontextTo() {
        AufrufKontextTo aufrufKontextTo = new AufrufKontextTo();
        aufrufKontextTo.setDurchfuehrendeBehoerde("TEST");
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung("TEST");
        aufrufKontextTo.setDurchfuehrenderBenutzerPasswort("TEST");
        aufrufKontextTo.setDurchfuehrenderSachbearbeiterName("TEST");
        aufrufKontextTo.setKorrelationsId("TEST");
        aufrufKontextTo.setRolle(new String[]{"TEST"});
        aufrufKontextTo.setRollenErmittelt(true);
        return aufrufKontextTo;
    }

    /**
     * Compares a {@link AufrufKontextTo} with a {@link AufrufKontext}.
     * <p>
     * Throws a {@link AssertionFailedError} if the passed objects does not match.
     *
     * @param to      Transport object
     * @param kontext context
     */
    private void assertEqualData(AufrufKontextTo to, AufrufKontext kontext) {
        assertEquals(to.getDurchfuehrendeBehoerde(), kontext.getDurchfuehrendeBehoerde());
        assertEquals(to.getDurchfuehrenderBenutzerKennung(), kontext.getDurchfuehrenderBenutzerKennung());
        assertEquals(to.getDurchfuehrenderSachbearbeiterName(),
                kontext.getDurchfuehrenderSachbearbeiterName());
        assertEquals(to.getDurchfuehrenderBenutzerPasswort(), kontext.getDurchfuehrenderBenutzerPasswort());
        assertEquals(to.getKorrelationsId(), kontext.getKorrelationsId());
        assertArrayEquals(to.getRolle(), kontext.getRolle());
        assertEquals(to.isRollenErmittelt(), kontext.isRollenErmittelt());
    }

    /**
     * resets the context before each test.
     */
    @Before
    public void leereAufrufKontext() {
        aufrufKontextVerwalter.resetAufrufKontext();
    }

    @After
    public void isAufrufKontextLeer() {
        // the context must not be set after the call is finished.
        assertNull(aufrufKontextVerwalter.getAufrufKontext());
    }

    @Test
    public void stelltAufrufKontextNichtBereitOhneParameter() {
        sst.stelltAufrufKontextNichtBereitOhneParameter();
        assertNull(aufrufKontextVerwalter.getLetzterAufrufKontext());
    }

    @Test
    public void stelltAufrufKontextNichtBereitMitParameter() {
        sst.stelltAufrufKontextNichtBereitMitParameter(createAufrufKontextTo());
        assertNull(aufrufKontextVerwalter.getLetzterAufrufKontext());
    }

    @Test
    public void stelltAufrufKontextBereitMitParameter() {
        AufrufKontextTo aufrufKontextTo = createAufrufKontextTo();
        sst.stelltAufrufKontextBereitMitParameter(aufrufKontextTo);
        assertEqualData(aufrufKontextTo, aufrufKontextVerwalter.getLetzterAufrufKontext());
        aufrufKontextVerwalter.getLetzterAufrufKontext().setRolle(null);
        assertNotNull(aufrufKontextVerwalter.getLetzterAufrufKontext().getRolle());
    }

    @Test
    public void stelltAufrufKontextBereitMitMehrerenParametern() {
        AufrufKontextTo aufrufKontextTo = createAufrufKontextTo();
        sst.stelltAufrufKontextBereitMitMehrerenParametern(aufrufKontextTo, "42");
        assertEqualData(aufrufKontextTo, aufrufKontextVerwalter.getLetzterAufrufKontext());
    }

    @Test
    public void stelltAufrufKontextBereitMitMehrerenParameterMehrereKontexte() {
        AufrufKontextTo aufrufKontextTo = createAufrufKontextTo();
        sst.stelltAufrufKontextBereitMitMehrerenParameterMehrereKontexte(aufrufKontextTo,
                createAufrufKontextTo());
        assertEqualData(aufrufKontextTo, aufrufKontextVerwalter.getLetzterAufrufKontext());
    }

    @Test
    public void stelltNullAufrufKontextBereitOhneParameter() {
        sst.stelltAufrufKontextBereitOhneParameter();
        assertNull(aufrufKontextVerwalter.getAufrufKontext());
    }
}
