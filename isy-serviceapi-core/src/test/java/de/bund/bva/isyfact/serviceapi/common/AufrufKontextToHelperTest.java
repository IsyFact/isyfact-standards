package de.bund.bva.isyfact.serviceapi.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public class AufrufKontextToHelperTest {

    @Test
    public void leseAufrufKontextToReadNull() {
        AufrufKontextTo result = AufrufKontextToHelper.leseAufrufKontextTo(null);
        assertNull(result);
    }

    @Test
    public void leseAufrufKontextToEmptyObjectArray() {
        AufrufKontextTo result = AufrufKontextToHelper.leseAufrufKontextTo(new Object[0]);
        assertNull(result);
    }

    // standard positive test case: One AufrufKontextTo as first param
    @Test
    public void leseAufrufKontextToFirstParameter() {
        AufrufKontextTo aufrufKontextTo = createDummyAufrufkontextTo();
        Object[] args = new Object[3];
        args[0] = aufrufKontextTo;
        args[1] = new Object();
        args[2] = new Object();

        AufrufKontextTo result = AufrufKontextToHelper.leseAufrufKontextTo(args);

        assertNotNull(result);
        assertEquals(aufrufKontextTo, result);
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

        AufrufKontextTo result = AufrufKontextToHelper.leseAufrufKontextTo(args);

        assertNotNull(result);
        assertEquals(aufrufKontextTo, result);
        assertEquals("first", result.getDurchfuehrenderBenutzerKennung());
    }

    // AufrufKontextTo not as the first argument
    @Test
    public void leseAufrufKontextToNotFirstArgument() {
        AufrufKontextTo aufrufKontextTo = createDummyAufrufkontextTo();

        Object[] args = new Object[3];
        args[0] = new Object();
        args[1] = aufrufKontextTo;
        args[2] = new Object();

        AufrufKontextTo result = AufrufKontextToHelper.leseAufrufKontextTo(args);

        assertNotNull(result);
        assertEquals(aufrufKontextTo, result);
    }

    private AufrufKontextTo createDummyAufrufkontextTo() {
        AufrufKontextTo aufrufKontextTo = new AufrufKontextTo();
        aufrufKontextTo.setDurchfuehrendeBehoerde("123456");
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung("benutzer");
        aufrufKontextTo.setDurchfuehrenderBenutzerPasswort("passwort");
        return aufrufKontextTo;
    }

}
