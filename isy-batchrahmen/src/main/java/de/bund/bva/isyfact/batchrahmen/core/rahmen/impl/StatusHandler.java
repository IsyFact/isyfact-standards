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
package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;

import java.sql.Timestamp;

import jakarta.persistence.EntityManagerFactory;

import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenParameterException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.BatchStatus;
import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.BatchStatusDao;

/**
 * Dieser Handler kapselt die Logik rund um die BatchStatus Tabelle.
 *
 *
 */
public class StatusHandler {
    /** Referenz auf das DAO Objekt fuer die Status-Tabelle. */
    private BatchStatusDao batchStatusDao;

    /** Id des Batches, wird zur Suche nach dem Status-Satz verwendet. */
    private String batchId;

    /**
     * Setzt die benoetigten Querschnittsdaten in der Instanz.
     *
     * @param factory
     *            {@link EntityManagerFactory} Aktuelle EntityManagerFactory für den Zugriff auf die
     *            Persistenz.
     */
    public StatusHandler(EntityManagerFactory factory) {
        this.batchStatusDao = new BatchStatusDao(factory);
    }

    /**
     * fuehrt folgende Aktionen aus:<br>
     * <ul>
     * <li>Lesen bzw. Anlegen des Statusdatenbank-Satzes.
     * <li>Pruefen, ob die Start-Parameter mit der Datenbank harmonieren.
     * <li>Aktualisieren des Statusdatenbank-Satzes (Status laeuft etc.)
     * <li>Bei Start: Aktualisieren der Konfigurationsparameter in der Datenbank
     * <li>Bei Restart: Lesen der Konfigurationsparameter aus der Datenbank und aktualisieren der eigenen
     * Konfiguration.
     * </ul>
     * @param konfiguration
     *            Die Konfiguration des Batch-Aufrufs. Diese wird bei Restart auf die in der Statusdatenbank
     *            abgelegte Konfiguration aktualisiert.
     * @return Der BatchStatus Datensatz fuer den Batch.
     */
    public BatchStatus statusSatzInitialisieren(BatchKonfiguration konfiguration) {
        this.batchId = konfiguration.getAsString(KonfigurationSchluessel.PROPERTY_BATCH_ID);
        String name = konfiguration.getAsString(KonfigurationSchluessel.PROPERTY_BATCH_NAME);
        // Lesen des Batch-Status, ggf. Anlegen des Satzes
        BatchStatus status = this.batchStatusDao.leseBatchStatus(this.batchId);
        if (status == null) {
            status = new BatchStatus();
            status.setBatchId(this.batchId);
            status.setBatchName(name);
            status.setBatchStatus(BatchStatusTyp.NEU.getName());
            this.batchStatusDao.createBatchStatus(status);
        }
        pruefeStatusDbGegenAufrufParameter(status, konfiguration);
        return status;
    }

    /**
     * Vermerkt das Laufen des Batches im BatchStatus.
     * @param konfiguration
     *            Die BatchKonfiguration
     */
    public void setzteStatusSatzAufLaufend(BatchKonfiguration konfiguration) {
        // Batch-Status aktualisieren.
        BatchStatus status = leseBatchStatus();
        status.setBatchStatus(BatchStatusTyp.LAEUFT.getName());
        status.setDatumLetzterStart(new Timestamp(System.currentTimeMillis()));
        if (BatchStartTyp.START.equals(konfiguration.getStartTyp())) {
            status.setSatzNummerLetztesCommit(0);
            status.setSchluesselLetztesCommit(null);
        }
    }

    /**
     * Liest den Status-Satz für den Batch.
     * @return Status-Satz des Batches oder <code>null</code> falls keiner existiert.
     */
    public BatchStatus leseBatchStatus() {
        // Lesen des Batch-Status, ggf. Anlegen des Satzes
        return this.batchStatusDao.leseBatchStatus(this.batchId);
    }

    /**
     * prueft, ob die Parameter fuer das Starten bzw. Restarten des Batches mit dem Status in der Datenbank
     * zusammenpassen.
     *
     * @param status
     *            der Status in der Datenbank
     * @param konfig
     *            die angegebenen Parameter.
     */
    private void pruefeStatusDbGegenAufrufParameter(BatchStatus status, BatchKonfiguration konfig) {
        if (BatchStatusTyp.LAEUFT.getName().equals(status.getBatchStatus())
            && !konfig.getAsBoolean(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_LAUF)) {
            throw new BatchrahmenParameterException(NachrichtenSchluessel.ERR_BATCH_AKTIV,
                KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_LAUF);
        }
        if (BatchStatusTyp.ABGEBROCHEN.getName().equals(status.getBatchStatus())
            && status.getSatzNummerLetztesCommit() >= 0 && konfig.getStartTyp() == BatchStartTyp.START
            && !konfig.getAsBoolean(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART)) {
            throw new BatchrahmenParameterException(NachrichtenSchluessel.ERR_IGNORIERE_RESTART,
                KonfigurationSchluessel.KOMMANDO_PARAM_RESTART,
                KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART);
        }
    }

}
