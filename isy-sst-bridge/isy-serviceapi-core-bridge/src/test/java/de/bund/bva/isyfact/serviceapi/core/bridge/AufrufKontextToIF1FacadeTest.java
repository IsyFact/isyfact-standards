package de.bund.bva.isyfact.serviceapi.core.bridge;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class AufrufKontextToIF1FacadeTest {

    @Spy
    AufrufKontextTo aufrufKontextToIf1 = createAufrufKontextToIf1();

    de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo aufrufKontextToIf2;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        aufrufKontextToIf2 = new AufrufKontextToIF1Facade(aufrufKontextToIf1);
    }

    @Test
    public void testPassThroughGetters(){
        String if2Result = aufrufKontextToIf2.getKorrelationsId();
        verify(aufrufKontextToIf1).getKorrelationsId();
        assertEquals(aufrufKontextToIf1.getKorrelationsId(), if2Result);

        if2Result = aufrufKontextToIf2.getDurchfuehrendeBehoerde();
        verify(aufrufKontextToIf1).getDurchfuehrendeBehoerde();
        assertEquals(aufrufKontextToIf1.getDurchfuehrendeBehoerde(), if2Result);

        if2Result = aufrufKontextToIf2.getDurchfuehrenderBenutzerKennung();
        verify(aufrufKontextToIf1).getDurchfuehrenderBenutzerKennung();
        assertEquals(aufrufKontextToIf1.getDurchfuehrenderBenutzerKennung(), if2Result);

        if2Result = aufrufKontextToIf2.getDurchfuehrenderBenutzerPasswort();
        verify(aufrufKontextToIf1).getDurchfuehrenderBenutzerPasswort();
        assertEquals(aufrufKontextToIf1.getDurchfuehrenderBenutzerPasswort(), if2Result);

        if2Result = aufrufKontextToIf2.getDurchfuehrenderSachbearbeiterName();
        verify(aufrufKontextToIf1).getDurchfuehrenderSachbearbeiterName();
        assertEquals(aufrufKontextToIf1.getDurchfuehrenderSachbearbeiterName(), if2Result);

        String[] if2ResultRolle = aufrufKontextToIf2.getRolle();
        verify(aufrufKontextToIf1).getRolle();
        assertArrayEquals(aufrufKontextToIf1.getRolle(), if2ResultRolle);

        boolean if2ResultBool = aufrufKontextToIf2.isRollenErmittelt();
        verify(aufrufKontextToIf1).isRollenErmittelt();
        assertEquals(aufrufKontextToIf1.isRollenErmittelt(), if2ResultBool);
    }

    @Test
    public void testPassThroughSetters(){
        String wert = "korrId";
        aufrufKontextToIf2.setKorrelationsId(wert);
        assertEquals(wert, aufrufKontextToIf1.getKorrelationsId());

        wert = "bhkz";
        aufrufKontextToIf2.setDurchfuehrendeBehoerde(wert);
        assertEquals(wert, aufrufKontextToIf1.getDurchfuehrendeBehoerde());

        wert = "ben";
        aufrufKontextToIf2.setDurchfuehrenderBenutzerKennung(wert);
        assertEquals(wert, aufrufKontextToIf1.getDurchfuehrenderBenutzerKennung());

        wert = "pass";
        aufrufKontextToIf2.setDurchfuehrenderBenutzerPasswort(wert);
        assertEquals(wert, aufrufKontextToIf1.getDurchfuehrenderBenutzerPasswort());

        wert = "name";
        aufrufKontextToIf2.setDurchfuehrenderSachbearbeiterName(wert);
        assertEquals(wert, aufrufKontextToIf1.getDurchfuehrenderSachbearbeiterName());

        String[] rollen = {"r1", "r2"};
        aufrufKontextToIf2.setRolle(rollen);
        assertArrayEquals(rollen, aufrufKontextToIf1.getRolle());

        final boolean rolleErmittelt = false;
        aufrufKontextToIf2.setRollenErmittelt(rolleErmittelt);
        assertEquals(rolleErmittelt, aufrufKontextToIf1.isRollenErmittelt());
    }

    private AufrufKontextTo createAufrufKontextToIf1() {
        AufrufKontextTo aufrufKontextTo = new AufrufKontextTo();
        aufrufKontextTo.setDurchfuehrendeBehoerde("TEST1");
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung("TEST2");
        aufrufKontextTo.setDurchfuehrenderBenutzerPasswort("TEST3");
        aufrufKontextTo.setDurchfuehrenderSachbearbeiterName("TEST4");
        aufrufKontextTo.setKorrelationsId("TEST5");
        aufrufKontextTo.setRolle(new String[] { "TEST6" });
        aufrufKontextTo.setRollenErmittelt(true);
        return aufrufKontextTo;
    }
}