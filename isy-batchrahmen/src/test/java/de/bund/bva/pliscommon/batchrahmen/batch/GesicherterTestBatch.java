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
package de.bund.bva.pliscommon.batchrahmen.batch;

import java.util.Date;

import de.bund.bva.pliscommon.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.pliscommon.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.AuthenticationCredentials;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;
import de.bund.bva.pliscommon.sicherheit.annotation.Gesichert;

/**
 * Verhält sich wie der BasicTest-Batch - jedoch wird eine Autorisierung vor der Verarbeitung eines Satzes durchgeführt.
 * <p>
 * Die Daten, die zur Autorisierung verwendet werden, können in der Konfiguration hinterlegt werden.
 * 
 * 
 */
public class GesicherterTestBatch extends BasicTestBatch {

    /** Konfigurationsschlüssel für den verwendeten Nutzer. */
    public static final String BATCH_BENUTZER = "batch.benutzer";

    /** Konfigurationsschlüssel für das verwendeten Psaswort. */
    public static final String BATCH_PASSWORT = "batch.passwort";

    /** Konfigurationsschlüssel für das verwendete Behördenkennzeichen. */
    public static final String BATCH_BHKNZ = "batch.bhknz";

    /** Zugriff auf die Batch-Konfiguration. */
    private BatchKonfiguration konfiguration;

    /**
     * Diese Methode wird gesichert. {@inheritDoc}
     */
    @Gesichert("Recht")
    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException {
        return super.verarbeiteSatz();
    }

    /**
     * speichert die Referenz auf die Konfiguration. {@inheritDoc}
     */
    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
        BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
        throws BatchAusfuehrungsException {
        this.konfiguration = konfiguration;
        return super
            .initialisieren(konfiguration, satzNummer, dbKey, startTyp, datumLetzterErfolg, protokoll);
    }

    /**
     * Liefert das Feld {@link #batchBenutzerKennung} zurück.
     * @return Wert von batchBenutzerKennung
     */
    protected String getBatchBenutzerKennung(BatchKonfiguration konfiguration) {
        return konfiguration.getAsString(BATCH_BENUTZER);
    }

    /**
     * Liefert das Feld {@link #batchBenutzerPasswort} zurück.
     * @return Wert von batchBenutzerPasswort
     */
    protected String getBatchBenutzerPasswort(BatchKonfiguration konfiguration) {
        return konfiguration.getAsString(BATCH_PASSWORT);
    }

    /**
     * Liefert das Feld {@link #batchBenutzerBhknz} zurück.
     * @return Wert von batchBenutzerBhknz
     */
    protected String getBatchBenutzerBhknz(BatchKonfiguration konfiguration) {
        return konfiguration.getAsString(BATCH_BHKNZ);
    }
    
    /**
     * Dieser Batch verwendet keine Sicherung.
     * {@inheritDoc}
     */
    @Override
    public AuthenticationCredentials getAuthenticationCredentials(BatchKonfiguration konfiguration) {
        AuthenticationCredentials authentifizierung = new AuthenticationCredentials();
        authentifizierung.setBehoerdenkennzeichen(getBatchBenutzerBhknz(konfiguration));
        authentifizierung.setBenutzerkennung(getBatchBenutzerKennung(konfiguration));
        authentifizierung.setPasswort(getBatchBenutzerPasswort(konfiguration));
        return authentifizierung;
    }

}
