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
import java.util.Optional;

import org.springframework.security.access.annotation.Secured;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;

/**
 * Behaves like the BasicTest batch - but authorization is performed before a record is processed.
 * <p>
 * The data used for authorization can be stored in the configuration.
 */
public class GesicherterTestBatch extends BasicTestBatch {

    /**
     * This method is secured. {@inheritDoc}
     */
    @Secured("PRIV_Recht")
    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException {
        return super.verarbeiteSatz();
    }

    /**
     * saves the reference to the configuration. {@inheritDoc}
     */
    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
                              BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
            throws BatchAusfuehrungsException {
        return super
                .initialisieren(konfiguration, satzNummer, dbKey, startTyp, datumLetzterErfolg, protokoll);
    }
}
