package de.bund.bva.isyfact.serviceapi.core.aufrufkontext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public class DefaultAufrufKontextToResolverTest {

    private final AufrufKontextToResolver resolver = new DefaultAufrufKontextToResolver();

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

    private static AufrufKontextTo createDummyAufrufkontextTo() {
        AufrufKontextTo aufrufKontextTo = new AufrufKontextTo();
        aufrufKontextTo.setDurchfuehrendeBehoerde("123456");
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung("benutzer");
        aufrufKontextTo.setDurchfuehrenderBenutzerPasswort("passwort");
        return aufrufKontextTo;
    }

}
