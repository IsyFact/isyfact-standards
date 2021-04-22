package de.bund.bva.isyfact.serviceapi.core.bridge;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public class IF1OrIF2AufrufKontextToResolverTest {

    AufrufKontextToResolver resolver = new IF1OrIF2AufrufKontextToResolver();

    //start If1 compatibility tests
    @Test
    public void leseAufrufKontextToIf1FirstParameter() {
        de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo aufrufKontextTo = createDummyAufrufkontextToIf1();
        Object[] args = new Object[3];
        args[0] = aufrufKontextTo;
        args[1] = new Object();
        args[2] = new Object();

        Optional<AufrufKontextTo> opt = resolver.leseAufrufKontextTo(args);

        assertTrue(opt.isPresent());
        assertAufrufKontextToBridgeEquals(aufrufKontextTo, opt.get());
    }

    // both IF1 and IF2 present: always return IF2
    @Test
    public void leseAufrufKontextToMixed() {
        AufrufKontextTo aufrufKontextTo = createDummyAufrufkontextTo();
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung("isy");
        de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo aufrufKontextTo2 = createDummyAufrufkontextToIf1();
        aufrufKontextTo2.setDurchfuehrenderBenutzerKennung("plis");

        Object[] args = new Object[3];
        args[0] = aufrufKontextTo;
        args[1] = aufrufKontextTo2;
        args[2] = null;

        Optional<AufrufKontextTo> opt = resolver.leseAufrufKontextTo(args);

        assertTrue(opt.isPresent());
        assertEquals(aufrufKontextTo, opt.get());
        assertEquals("isy", opt.get().getDurchfuehrenderBenutzerKennung());
    }

    private de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo createDummyAufrufkontextToIf1() {
        de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo aufrufKontextTo =
            new de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo();
        aufrufKontextTo.setDurchfuehrendeBehoerde("if1123456");
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung("if1benutzer");
        aufrufKontextTo.setDurchfuehrenderBenutzerPasswort("if1passwort");
        return aufrufKontextTo;
    }

    private void assertAufrufKontextToBridgeEquals(
            de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo aufrufKontextToIf1,
            AufrufKontextTo aufrufKontextToIf2){
        if (aufrufKontextToIf1 == null){
            assertNull(aufrufKontextToIf2);
        } else {
           assertNotNull(aufrufKontextToIf2);
           assertEquals(aufrufKontextToIf1.getKorrelationsId(), aufrufKontextToIf2.getKorrelationsId());
           assertEquals(aufrufKontextToIf1.getDurchfuehrendeBehoerde(), aufrufKontextToIf2.getDurchfuehrendeBehoerde());
           assertEquals(aufrufKontextToIf1.getDurchfuehrenderBenutzerKennung(), aufrufKontextToIf2.getDurchfuehrenderBenutzerKennung());
           assertEquals(aufrufKontextToIf1.getDurchfuehrenderBenutzerPasswort(), aufrufKontextToIf2.getDurchfuehrenderBenutzerPasswort());
           assertEquals(aufrufKontextToIf1.getDurchfuehrenderSachbearbeiterName(), aufrufKontextToIf2.getDurchfuehrenderSachbearbeiterName());
           assertArrayEquals(aufrufKontextToIf1.getRolle(), aufrufKontextToIf2.getRolle());
           assertEquals(aufrufKontextToIf1.isRollenErmittelt(), aufrufKontextToIf2.isRollenErmittelt());
        }
    }


    /* Start of: all IF2-only usecases should still work.
    * Copied from DefaultAufrufKontextToResolverTest */
    @Test
    public void leseAufrufKontextToReadNull() {
        Optional<AufrufKontextTo> opt = resolver.leseAufrufKontextTo(null);
        assertFalse(opt.isPresent());
    }

    @Test
    public void leseAufrufKontextToEmptyObjectArray() {
        Optional<AufrufKontextTo> opt = resolver.leseAufrufKontextTo(new Object[0]);
        assertFalse(opt.isPresent());
    }

    // standard positive test case: One AufrufKontextTo as first param
    @Test
    public void leseAufrufKontextToFirstParameter() {
        AufrufKontextTo aufrufKontextTo = createDummyAufrufkontextTo();
        Object[] args = new Object[3];
        args[0] = aufrufKontextTo;
        args[1] = new Object();
        args[2] = new Object();

        Optional<AufrufKontextTo> opt = resolver.leseAufrufKontextTo(args);

        assertTrue(opt.isPresent());
        assertEquals(aufrufKontextTo, opt.get());
    }

    // multiple AufrufKontextTo: should return first one found
    @Test
    public void leseAufrufKontextToMultiple() {
        AufrufKontextTo aufrufKontextTo = createDummyAufrufkontextTo();
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung("first");
        AufrufKontextTo aufrufKontextTo2 = createDummyAufrufkontextTo();
        aufrufKontextTo2.setDurchfuehrenderBenutzerKennung("second");
        AufrufKontextTo aufrufKontextTo3 = createDummyAufrufkontextTo();
        aufrufKontextTo3.setDurchfuehrenderBenutzerKennung("third");

        Object[] args = new Object[3];
        args[0] = aufrufKontextTo;
        args[1] = aufrufKontextTo2;
        args[2] = aufrufKontextTo3;

        Optional<AufrufKontextTo> opt = resolver.leseAufrufKontextTo(args);

        assertTrue(opt.isPresent());
        assertEquals(aufrufKontextTo, opt.get());
        assertEquals("first", opt.get().getDurchfuehrenderBenutzerKennung());
    }

    // AufrufKontextTo not as the first argument
    @Test
    public void leseAufrufKontextToNotFirstArgument() {
        AufrufKontextTo aufrufKontextTo = createDummyAufrufkontextTo();

        Object[] args = new Object[3];
        args[0] = new Object();
        args[1] = aufrufKontextTo;
        args[2] = new Object();

        Optional<AufrufKontextTo> opt = resolver.leseAufrufKontextTo(args);

        assertTrue(opt.isPresent());
        assertEquals(aufrufKontextTo, opt.get());
    }

    private AufrufKontextTo createDummyAufrufkontextTo() {
        AufrufKontextTo aufrufKontextTo = new AufrufKontextTo();
        aufrufKontextTo.setDurchfuehrendeBehoerde("123456");
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung("benutzer");
        aufrufKontextTo.setDurchfuehrenderBenutzerPasswort("passwort");
        return aufrufKontextTo;
    }
    /* End of: all IF2-only usecases should still work */
}