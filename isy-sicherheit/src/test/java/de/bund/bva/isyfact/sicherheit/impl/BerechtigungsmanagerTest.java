package de.bund.bva.isyfact.sicherheit.impl;

import java.util.HashSet;
import java.util.Set;

import de.bund.bva.isyfact.sicherheit.config.IsySicherheitConfigurationProperties;
import de.bund.bva.isyfact.sicherheit.impl.BerechtigungsmanagerImpl;
import de.bund.bva.isyfact.sicherheit.impl.RechtImpl;
import de.bund.bva.isyfact.sicherheit.impl.RollenRechteMapping;
import de.bund.bva.isyfact.sicherheit.impl.SicherheitImpl;
import de.bund.bva.isyfact.sicherheit.impl.XmlAccess;
import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.isyfact.sicherheit.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.Recht;
import de.bund.bva.isyfact.sicherheit.common.exception.RollenRechteMappingException;

import static org.junit.Assert.*;

/**
 * Testet die Implementierung des Berechtigungsmanagers.
 *
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BerechtigungsmanagerTest {

    /** der zu testende Berechtigungsmanager. */
    private BerechtigungsmanagerImpl berechtigungsmanager;

    /** der Nutzerkennung des Aufrufkontext auf dem der Berechtigungsmanager arbeitet. */
    private static final String AUFRUF_KONTEXT_NUTZERKENNUNG = "test02@test.de";

    /** das Behördenkennzeichen des Aufrufkontext auf dem der Berechtigungsmanager arbeitet. */
    private static final String AUFRUF_KONTEXT_BHKNZ = "123456";

    /** der Rollen des Aufrufkontext auf dem der Berechtigungsmanager arbeitet. */
    private static final String[] AUFRUF_KONTEXT_ROLLEN = new String[] {};

    /** der Pfad zur Rollen-Rechte-Mapping Datei. */
    private static final String ROLLENRECHTE_PFAD = "/resources/sicherheit/rollenrechte.xml";

    /** der AufrufKontext, aus dem der Berechtigungsmanager erzeugt wird. */
    private AufrufKontext aufrufKontext;

    /** die geparste RollenRechteMapping-Datei. */
    private RollenRechteMapping mapping;

    /**
     * Erstellt den zu Testenden Berechtigungsmanager.
     */
    @Before
    public void setup() {
        this.aufrufKontext = new AufrufKontextImpl();
        this.aufrufKontext.setDurchfuehrenderBenutzerKennung(AUFRUF_KONTEXT_NUTZERKENNUNG);
        this.aufrufKontext.setDurchfuehrendeBehoerde(AUFRUF_KONTEXT_BHKNZ);
        this.aufrufKontext.setRolle(AUFRUF_KONTEXT_ROLLEN);
        BerechtigungsmanagerImpl berechtigungsmanager =
            new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
        XmlAccess access = new XmlAccess();
        this.mapping = access.parseRollenRechteFile(ROLLENRECHTE_PFAD);
        berechtigungsmanager.setRollenRechteMapping(this.mapping);
        this.berechtigungsmanager = berechtigungsmanager;
    }

    @Test
    public void testGetRechte() {
        Set<Recht> rechte = this.berechtigungsmanager.getRechte();
        assertEquals("RechteSet nicht korrekt", new HashSet<Recht>(), rechte);

        String[] abc = new String[] { "Recht_A", "Recht_B", "Recht_C" };
        Set<Recht> expected = new HashSet<>();
        for (String recht : abc) {
            expected.add(new RechtImpl(recht, null));
        }
        this.aufrufKontext.setRolle(new String[] { "Rolle_ABC" });
        this.berechtigungsmanager = new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
        this.berechtigungsmanager.setRollenRechteMapping(this.mapping);
        rechte = this.berechtigungsmanager.getRechte();
        assertEquals("RechteSet nicht korrekt", expected, rechte);

        String[] a = new String[] { "Recht_A" };
        expected = new HashSet<Recht>();
        for (String recht : a) {
            expected.add(new RechtImpl(recht, null));
        }
        this.aufrufKontext.setRolle(new String[] { "Rolle_A" });
        this.berechtigungsmanager = new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
        this.berechtigungsmanager.setRollenRechteMapping(this.mapping);
        rechte = this.berechtigungsmanager.getRechte();
        assertEquals("RechteSet nicht korrekt", expected, rechte);

        String[] ac = new String[] { "Recht_A", "Recht_C" };
        expected = new HashSet<Recht>();
        for (String recht : ac) {
            expected.add(new RechtImpl(recht, null));
        }
        this.aufrufKontext.setRolle(new String[] { "Rolle_A", "Rolle_C" });
        this.berechtigungsmanager = new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
        this.berechtigungsmanager.setRollenRechteMapping(this.mapping);
        rechte = this.berechtigungsmanager.getRechte();
        assertEquals("RechteSet nicht korrekt", expected, rechte);

    }

    @Test
    public void testHatRecht() {
        assertFalse("Falsches Recht wurde gefunden.", this.berechtigungsmanager.hatRecht("Recht_A"));
        assertFalse("Falsches Recht wurde gefunden.", this.berechtigungsmanager.hatRecht("Recht_B"));
        assertFalse("Falsches Recht wurde gefunden.", this.berechtigungsmanager.hatRecht("Recht_C"));

        this.aufrufKontext.setRolle(new String[] { "Rolle_ABC" });
        this.berechtigungsmanager = new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
        this.berechtigungsmanager.setRollenRechteMapping(this.mapping);
        assertTrue("Recht wurde nicht gefunden.", this.berechtigungsmanager.hatRecht("Recht_A"));
        assertTrue("Recht wurde nicht gefunden.", this.berechtigungsmanager.hatRecht("Recht_B"));
        assertTrue("Recht wurde nicht gefunden.", this.berechtigungsmanager.hatRecht("Recht_C"));

        this.aufrufKontext.setRolle(new String[] { "Rolle_Keine" });
        this.berechtigungsmanager = new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
        this.berechtigungsmanager.setRollenRechteMapping(this.mapping);
        assertFalse("Falsches Recht wurde gefunden.", this.berechtigungsmanager.hatRecht("Recht_A"));
        assertFalse("Falsches Recht wurde gefunden.", this.berechtigungsmanager.hatRecht("Recht_B"));
        assertFalse("Falsches Recht wurde gefunden.", this.berechtigungsmanager.hatRecht("Recht_C"));

        this.aufrufKontext.setRolle(new String[] { "Rolle_C" });
        this.berechtigungsmanager = new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
        this.berechtigungsmanager.setRollenRechteMapping(this.mapping);
        assertFalse("Falsches Recht wurde gefunden.", this.berechtigungsmanager.hatRecht("Recht_A"));
        assertFalse("Falsches Recht wurde gefunden.", this.berechtigungsmanager.hatRecht("Recht_B"));
        assertTrue("Recht wurde nicht gefunden.", this.berechtigungsmanager.hatRecht("Recht_C"));

        this.aufrufKontext.setRolle(new String[] { "Rolle_A", "Rolle_C" });
        this.berechtigungsmanager = new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
        this.berechtigungsmanager.setRollenRechteMapping(this.mapping);
        assertTrue("Recht wurde nicht gefunden.", this.berechtigungsmanager.hatRecht("Recht_A"));
        assertFalse("Falsches Recht wurde gefunden.", this.berechtigungsmanager.hatRecht("Recht_B"));
        assertTrue("Recht wurde nicht gefunden.", this.berechtigungsmanager.hatRecht("Recht_C"));

    }

    @Test
    public void testGetRecht() {
        this.aufrufKontext.setRolle(new String[] { "Rolle_ABC" });
        this.berechtigungsmanager = new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
        this.berechtigungsmanager.setRollenRechteMapping(this.mapping);
        assertNotNull("Recht nicht gefunden.", this.berechtigungsmanager.getRecht("Recht_A"));
        assertNotNull("Recht nicht gefunden.", this.berechtigungsmanager.getRecht("Recht_B"));
        assertNotNull("Recht nicht gefunden.", this.berechtigungsmanager.getRecht("Recht_C"));
        assertNull("Nicht erwartetes Recht gefunden.", this.berechtigungsmanager.getRecht("Rolle_A"));
        try {
            this.berechtigungsmanager.getRecht(null);
            fail("Erwartete Exception wird nicht ausgelöst");
        } catch (IllegalArgumentException e) {
            // hier ist alles in Ordnung
        }
        try {
            this.berechtigungsmanager = new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
            // berechtigungsmanager.setRollenRechteMapping wird nicht aufgerufen - Fehler!
            this.berechtigungsmanager.getRecht("Recht_A");
            fail("Erwartete Exception wird nicht ausgelöst");
        } catch (RollenRechteMappingException e) {
            // hier ist alles in Ordnung
        }
    }

    @Test
    public void testKeineRollen() {
        new BerechtigungsmanagerImpl(null);
    }

    @Test(expected = RollenRechteMappingException.class)
    public void testRechtWurdeNichtKonfiguriert() throws Exception {
        AufrufKontextImpl aufrufKontext = new AufrufKontextImpl();
        aufrufKontext.setDurchfuehrenderBenutzerKennung(AUFRUF_KONTEXT_NUTZERKENNUNG);
        aufrufKontext.setDurchfuehrendeBehoerde(AUFRUF_KONTEXT_BHKNZ);
        aufrufKontext.setRolle(AUFRUF_KONTEXT_ROLLEN);
        aufrufKontext.setRollenErmittelt(true);

        AufrufKontextVerwalterImpl aufrufKontextVerwalter = new AufrufKontextVerwalterImpl();
        aufrufKontextVerwalter.setAufrufKontext(aufrufKontext);

        SicherheitImpl
            sicherheit = new SicherheitImpl(ROLLENRECHTE_PFAD, aufrufKontextVerwalter, null, null, new IsySicherheitConfigurationProperties());
        sicherheit.afterPropertiesSet();

        Berechtigungsmanager berechtigungsManager = sicherheit.getBerechtigungsManager();
        berechtigungsManager.hatRecht("Dieses Recht kommt in der RollenRechteMappingDatei nicht vor.");
    }
}
