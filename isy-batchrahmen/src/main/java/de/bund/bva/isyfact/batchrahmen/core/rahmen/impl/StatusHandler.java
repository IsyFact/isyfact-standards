package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;

import java.sql.Timestamp;

import javax.persistence.EntityManagerFactory;

import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenMaxWiederholungenException;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenParameterException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.BatchKonfigurationsParameter;
import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.BatchStatus;
import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.BatchStatusDao;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

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
     * Parameter key used in {@link BatchStatus#getKonfigurationsParameter()} to store
     * the number of restart attempts for the current failure cycle.
     */
    private static final String RESTART_ZAEHLER_PARAMETER_NAME = "Batchrahmen.RestartZaehler";

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
            resetRestartZaehler(status);
        } else if (BatchStartTyp.RESTART.equals(konfiguration.getStartTyp())) {
            incrementRestartZaehler(status);
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
        long maxWiederholungen =
                konfig.getAsLong(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_MAX_WIEDERHOLUNGEN, -1);
        if (maxWiederholungen >= 0
                && BatchStatusTyp.ABGEBROCHEN.getName().equals(status.getBatchStatus())
                && konfig.getStartTyp() == BatchStartTyp.RESTART
                && leseRestartZaehler(status) >= maxWiederholungen) {
            throw new BatchrahmenMaxWiederholungenException(NachrichtenSchluessel.ERR_MAX_WIEDERHOLUNGEN_UEBERSCHRITTEN,
                    String.valueOf(maxWiederholungen));
        }
    }

    /**
     * Reads the restart counter from the batch status configuration parameters.
     *
     * @param status the batch status
     * @return the current restart count, or 0 if not set
     */
    private long leseRestartZaehler(BatchStatus status) {
        Set<BatchKonfigurationsParameter> params = status.getKonfigurationsParameter();
        if (params == null) {
            return 0;
        }
        return params.stream()
                .filter(p -> RESTART_ZAEHLER_PARAMETER_NAME.equals(p.getParameterName()))
                .findFirst()
                .map(p -> Long.parseLong(p.getParameterWert()))
                .orElse(0L);
    }

    /**
     * Increments the restart counter in the batch status configuration parameters.
     *
     * @param status the batch status
     */
    private void incrementRestartZaehler(BatchStatus status) {
        long current = leseRestartZaehler(status);
        Set<BatchKonfigurationsParameter> params = status.getKonfigurationsParameter();
        if (params == null) {
            params = new HashSet<>();
            status.setKonfigurationsParameter(params);
        }
        params.removeIf(p -> RESTART_ZAEHLER_PARAMETER_NAME.equals(p.getParameterName()));
        params.add(new BatchKonfigurationsParameter(this.batchId, RESTART_ZAEHLER_PARAMETER_NAME,
                String.valueOf(current + 1)));
    }

    /**
     * Resets the restart counter in the batch status configuration parameters.
     *
     * @param status the batch status
     */
    private void resetRestartZaehler(BatchStatus status) {
        Set<BatchKonfigurationsParameter> params = status.getKonfigurationsParameter();
        if (params != null) {
            params.removeIf(p -> RESTART_ZAEHLER_PARAMETER_NAME.equals(p.getParameterName()));
        }
    }

}
