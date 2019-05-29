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
package de.bund.bva.isyfact.aufrufkontext.stub;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import org.springframework.beans.factory.InitializingBean;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;

/**
 * Stellt einer Anwendung einen festen AufrufKontext bereit.
 * <p>
 * Diese Klasse kann in der Entwicklung verwendet werden - ist aber nicht für den Produktiven Einsatz gedacht.
 *
 *
 */
public class AufrufKontextVerwalterStub<T extends AufrufKontext> implements AufrufKontextVerwalter<T>,
    InitializingBean {

    /** Wert für Durchführende Behörde. */
    private String durchfuehrendeBehoerde = "123456";

    /** Wert für Benutzerkennung. */
    private String durchfuehrenderBenutzerKennung = "martha.mustermann@bva.bund.de";

    /** Wert für Passwort. */
    private String durchfuehrenderBenutzerPasswort = null;

    /** Wert für Sachbearbeiter-Name. */
    private String durchfuehrenderSachbearbeiterName = "Martha Mustermann";

    /** Wert für die interne Kennung des Benutzers (Unique-ID). */
    private String durchfuehrenderBenutzerInterneKennung = "Martha007";

    /** Wert für Rollen. */
    private String[] rollen = new String[] {};

    /**
     * wenn {@code true}, dann wird bei jeder Abfrage ein neuer AufrufKontext erzeugt - somit ist dieser nicht
     * veränderlich.
     */
    private boolean festerAufrufKontext;

    /** Zugriff auf die AufrufKontextFactory, um anwendungsspezifische Kontexte herstellen zu können. */
    private AufrufKontextFactory<T> aufrufKontextFactory;

    /** aufrufKontext, der jedes mal zurückgegeben wird. */
    private T aufrufKontext;

    /**
     * Setzt das Feld {@link #festerAufrufKontext}.
     * @param festerAufrufKontext
     *            Neuer Wert für festerAufrufKontext
     */

    public void setFesterAufrufKontext(boolean festerAufrufKontext) {
        this.festerAufrufKontext = festerAufrufKontext;
    }

    /**
     * Setzt das Feld {@link #aufrufKontextFactory}.
     * @param aufrufKontextFactory
     *            Neuer Wert für aufrufKontextFactory
     */

    public void setAufrufKontextFactory(AufrufKontextFactory<T> aufrufKontextFactory) {
        this.aufrufKontextFactory = aufrufKontextFactory;
    }

    /**
     * Setzt das Feld 'durchfuehrendeBehoerde'.
     * @param durchfuehrendeBehoerde
     *            Neuer Wert für durchfuehrendeBehoerde
     */
    public void setDurchfuehrendeBehoerde(String durchfuehrendeBehoerde) {
        this.durchfuehrendeBehoerde = durchfuehrendeBehoerde;
    }

    /**
     * Setzt das Feld 'durchfuehrenderBenutzerKennung'.
     * @param durchfuehrenderBenutzerKennung
     *            Neuer Wert für durchfuehrenderBenutzerKennung
     */
    public void setDurchfuehrenderBenutzerKennung(String durchfuehrenderBenutzerKennung) {
        this.durchfuehrenderBenutzerKennung = durchfuehrenderBenutzerKennung;
    }

    /**
     * Setzt das Feld 'durchfuehrenderBenutzerPasswort'.
     * @param durchfuehrenderBenutzerPasswort
     *            Neuer Wert für durchfuehrenderBenutzerPasswort
     */
    public void setDurchfuehrenderBenutzerPasswort(String durchfuehrenderBenutzerPasswort) {
        this.durchfuehrenderBenutzerPasswort = durchfuehrenderBenutzerPasswort;
    }

    /**
     * Setzt das Feld 'durchfuehrenderSachbearbeiterName'.
     * @param durchfuehrenderSachbearbeiterName
     *            Neuer Wert für durchfuehrenderSachbearbeiterName
     */
    public void setDurchfuehrenderSachbearbeiterName(String durchfuehrenderSachbearbeiterName) {
        this.durchfuehrenderSachbearbeiterName = durchfuehrenderSachbearbeiterName;
    }

    /**
     * Setzt das Feld 'durchfuehrenderBenutzerInterneKennung'.
     * @param durchfuehrenderBenutzerInterneKennung
     *            Neuer Wert für durchfuehrenderBenutzerInterneKennung
     */
    public void setDurchfuehrenderBenutzerInterneKennung(String durchfuehrenderBenutzerInterneKennung) {
        this.durchfuehrenderBenutzerInterneKennung = durchfuehrenderBenutzerInterneKennung;
    }

    /**
     * Setzt das Feld 'rollen'.
     * @param rollen
     *            Neuer Wert für rollen
     */
    public void setRollen(String... rollen) {
        this.rollen = rollen;
    }

    /**
     * {@inheritDoc}
     */
    public T getAufrufKontext() {
        if (!this.festerAufrufKontext) {
            return this.aufrufKontext;
        } else {
            return erzeugeAufrufKontext();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setAufrufKontext(T aufrufKontext) {
        this.aufrufKontext = aufrufKontext;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        this.aufrufKontext = erzeugeAufrufKontext();
    }

    /**
     *
     */
    private T erzeugeAufrufKontext() {
        T aufrufKontext = this.aufrufKontextFactory.erzeugeAufrufKontext();

        aufrufKontext.setDurchfuehrendeBehoerde(this.durchfuehrendeBehoerde);
        aufrufKontext.setDurchfuehrenderBenutzerKennung(this.durchfuehrenderBenutzerKennung);
        aufrufKontext.setDurchfuehrenderBenutzerPasswort(this.durchfuehrenderBenutzerPasswort);
        aufrufKontext.setDurchfuehrenderSachbearbeiterName(this.durchfuehrenderSachbearbeiterName);
        aufrufKontext.setDurchfuehrenderBenutzerInterneKennung(this.durchfuehrenderBenutzerInterneKennung);
        aufrufKontext.setRolle(this.rollen);
        aufrufKontext.setRollenErmittelt(this.rollen != null);

        this.aufrufKontextFactory.nachAufrufKontextVerarbeitung(aufrufKontext);

        return aufrufKontext;
    }

}
