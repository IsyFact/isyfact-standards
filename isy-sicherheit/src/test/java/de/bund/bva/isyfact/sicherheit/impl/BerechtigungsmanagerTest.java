/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.sicherheit.impl;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.isyfact.sicherheit.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.Recht;
import de.bund.bva.isyfact.sicherheit.common.exception.RollenRechteMappingException;
import de.bund.bva.isyfact.sicherheit.config.IsySicherheitConfigurationProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests the implementation of the Berechtigungsmanager.
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BerechtigungsmanagerTest {

    /** The Berechtigungsmanager to be tested. */
    private BerechtigungsmanagerImpl berechtigungsmanager;

    /** The user identifier of the Aufrufkontext that the Berechtigungsmanager operates on. */
    private static final String AUFRUF_KONTEXT_NUTZERKENNUNG = "test02@test.de";

    /** The agency code of the Aufrufkontext that the Berechtigungsmanager operates on. */
    private static final String AUFRUF_KONTEXT_BHKNZ = "123456";

    /** The roles of the Aufrufkontext that the Berechtigungsmanager operates on. */
    private static final String[] AUFRUF_KONTEXT_ROLLEN = new String[] {};

    /** The path to the role-rights mapping file. */
    private static final String ROLLENRECHTE_PFAD = "/resources/sicherheit/rollenrechte.xml";

    /** The AufrufKontext from which the Berechtigungsmanager is created. */
    private AufrufKontext aufrufKontext;

    /** The parsed role-rights mapping file. */
    private RollenRechteMapping mapping;

    /**
     * Sets up the Berechtigungsmanager to be tested.
     */
    @Before
    public void setup() {
        this.aufrufKontext = new AufrufKontextImpl();
        this.aufrufKontext.setDurchfuehrenderBenutzerKennung(AUFRUF_KONTEXT_NUTZERKENNUNG);
        this.aufrufKontext.setDurchfuehrendeBehoerde(AUFRUF_KONTEXT_BHKNZ);
        this.aufrufKontext.setRolle(AUFRUF_KONTEXT_ROLLEN);
        BerechtigungsmanagerImpl tempBerechtigungsmanager =
                new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
        XmlAccess access = new XmlAccess();
        this.mapping = access.parseRollenRechteFile(ROLLENRECHTE_PFAD);
        tempBerechtigungsmanager.setRollenRechteMapping(this.mapping);
        this.berechtigungsmanager = tempBerechtigungsmanager;
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
            // this is expected
        }
        try {
            this.berechtigungsmanager = new BerechtigungsmanagerImpl(this.aufrufKontext.getRolle());
            // berechtigungsmanager.setRollenRechteMapping is not called - error!
            this.berechtigungsmanager.getRecht("Recht_A");
            fail("Erwartete Exception wird nicht ausgelöst");
        } catch (RollenRechteMappingException e) {
            // this is expected
        }
    }

    @Test
    public void testKeineRollen() {
        new BerechtigungsmanagerImpl(null);
    }

    @Test(expected = RollenRechteMappingException.class)
    public void testRechtWurdeNichtKonfiguriert() throws Exception {
        AufrufKontextImpl aufrufTestKontext = new AufrufKontextImpl();
        aufrufTestKontext.setDurchfuehrenderBenutzerKennung(AUFRUF_KONTEXT_NUTZERKENNUNG);
        aufrufTestKontext.setDurchfuehrendeBehoerde(AUFRUF_KONTEXT_BHKNZ);
        aufrufTestKontext.setRolle(AUFRUF_KONTEXT_ROLLEN);
        aufrufTestKontext.setRollenErmittelt(true);

        AufrufKontextVerwalterImpl aufrufKontextVerwalter = new AufrufKontextVerwalterImpl();
        aufrufKontextVerwalter.setAufrufKontext(aufrufTestKontext);

        SicherheitImpl
                sicherheit = new SicherheitImpl(ROLLENRECHTE_PFAD, aufrufKontextVerwalter, null, null, new IsySicherheitConfigurationProperties());
        sicherheit.afterPropertiesSet();

        Berechtigungsmanager berechtigungsManager = sicherheit.getBerechtigungsManager();
        berechtigungsManager.hatRecht("Dieses Recht kommt in der RollenRechteMappingDatei nicht vor.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHatRechtMitLeererEingabe() {
        this.berechtigungsmanager.hatRecht("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHatRechtMitNullEingabe() {
        this.berechtigungsmanager.hatRecht(null);
    }

    @Test(expected = RollenRechteMappingException.class)
    public void testHatRechtMitNichtDefiniertemRecht() {
        // Prerequisite: Mapping does not contain this right
        this.berechtigungsmanager.hatRecht("NichtExistierendesRecht");
    }

    @Test
    public void testGetRechtMitNichtExistierendemRecht() {
        assertNull("Erwartetes Ergebnis für nicht definiertes Recht ist null.",
                this.berechtigungsmanager.getRecht("NichtExistierendesRecht"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRechtMitLeererEingabe() {
        this.berechtigungsmanager.getRecht("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRechtMitNullEingabe() {
        this.berechtigungsmanager.getRecht(null);
    }

    @Test(expected = RollenRechteMappingException.class)
    public void testBerechneRechteAusRollenOhneMapping() {
        this.berechtigungsmanager.setRollenRechteMapping(null);
        this.berechtigungsmanager.getRechte();  // This should trigger the exception
    }

}
