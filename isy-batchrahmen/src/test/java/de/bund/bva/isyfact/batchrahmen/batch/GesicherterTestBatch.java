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
package de.bund.bva.isyfact.batchrahmen.batch;

import java.util.Date;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.AuthenticationCredentials;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;
import de.bund.bva.isyfact.sicherheit.annotation.Gesichert;

/**
 * Behaves like the BasicTest batch - but authorization is performed before a record is processed.
 * <p>
 * The data used for authorization can be stored in the configuration.
 */
public class GesicherterTestBatch extends BasicTestBatch {

    /**
     * Configuration key for the user used.
     */
    public static final String BATCH_BENUTZER = "batch.benutzer";

    /**
     * Configuration key for the used password.
     */
    public static final String BATCH_PASSWORT = "batch.passwort";

    /**
     * Configuration key for the Behoerdenkennzeichen used.
     */
    public static final String BATCH_BHKNZ = "batch.bhknz";

    /**
     * Access to the batch configuration.
     */
    private BatchKonfiguration konfiguration;

    /**
     * This method is secured. {@inheritDoc}
     */
    @Gesichert("Recht")
    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException {
        return super.verarbeiteSatz();
    }

    /**
     * saves the reference to the configuration. {@inheritDoc}
     */
    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
                              BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
            throws BatchAusfuehrungsException {
        this.konfiguration = konfiguration;
        return super
                .initialisieren(konfiguration, satzNummer, dbKey, startTyp, datumLetzterErfolg, protokoll);
    }

    /**
     * Returns the field batchBenutzerKennung.
     *
     * @return value of batchBenutzerKennung
     */
    protected String getBatchBenutzerKennung(BatchKonfiguration konfiguration) {
        return konfiguration.getAsString(BATCH_BENUTZER);
    }

    /**
     * Returns the field batchBenutzerPasswort.
     *
     * @return value of batchBenutzerPasswort
     */
    protected String getBatchBenutzerPasswort(BatchKonfiguration konfiguration) {
        return konfiguration.getAsString(BATCH_PASSWORT);
    }

    /**
     * Returns the field batchBenutzerBhknz.
     *
     * @return value of batchBenutzerBhknz
     */
    protected String getBatchBenutzerBhknz(BatchKonfiguration konfiguration) {
        return konfiguration.getAsString(BATCH_BHKNZ);
    }

    @Override
    public AuthenticationCredentials getAuthenticationCredentials(BatchKonfiguration konfiguration) {
        AuthenticationCredentials authentifizierung = new AuthenticationCredentials();
        authentifizierung.setBehoerdenkennzeichen(getBatchBenutzerBhknz(konfiguration));
        authentifizierung.setBenutzerkennung(getBatchBenutzerKennung(konfiguration));
        authentifizierung.setPasswort(getBatchBenutzerPasswort(konfiguration));
        return authentifizierung;
    }

}
