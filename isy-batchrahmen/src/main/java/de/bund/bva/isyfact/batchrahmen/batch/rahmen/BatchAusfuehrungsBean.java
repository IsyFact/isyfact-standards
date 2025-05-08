package de.bund.bva.isyfact.batchrahmen.batch.rahmen;

import java.util.Date;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.StatistikEintrag;

/**
 * Interface for the beans, which are attached to the {@link de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen}.
 * <p>
 * These beans contain the concrete batch logic:
 * <ul>
 * <li>They perform the reading of data sets as well as their processing.
 * <li>They read ahead in case of restarts.
 * <li>They log the batch process via the methods
 * {@link #initialisieren(BatchKonfiguration, long, String, BatchStartTyp, Date, BatchErgebnisProtokoll)},
 * {@link #checkpointGeschrieben(long)} and {@link #batchBeendet()}.
 * </ul>
 * The {@link de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen} takes over the transaction handling, the handling of the status table, the initializing
 * of the Spring contexts.
 */
public interface BatchAusfuehrungsBean {

    /**
     * Initializes the execution class: opens the required files or database cursors, reads up
     * to the specified record or database key during the restart procedure.
     *
     * @param konfiguration      The configuration of the batch. Which properties are relevant for the class, is not predefined.
     *                           The properties contain information of the configuration property file
     *                           as well as (overwriting) the specified command line parameters.
     *                           <p>
     *                           At a restart the data of the last run are transferred.
     * @param satzNummer         the record number up to which should be read during a restart. This key can
     *                           be ignored if the parameter dbKey is used for the restart.
     * @param dbKey              the database key from which values are to be read for a restart.
     *                           This key can be ignored if the parameter satzNummer is used for the restart.
     * @param startTyp           The start type (start or restart).
     * @param datumLetzterErfolg Date on which the batch was run successfully the last time.
     * @param protokoll          Result Log of the batch. During the initialization process the required
     *                           {@link StatistikEintrag} should be initialized.
     * @return if known: The total number of records. Otherwise: -1.
     * @throws BatchAusfuehrungsException If an error occurs.
     */
    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
                              BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
            throws BatchAusfuehrungsException;

    /**
     * process the next block.
     *
     * @return The processing result of the block. It consists of:
     * -the database key of the processed record for a later restart of the batch. Null, if no restart via database key is supported.
     * -An indicator that there are more records to be processed.
     * @throws BatchAusfuehrungsException If an error occurs.
     */
    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException;

    /**
     * This method is called in the context of a transaction. It is called when the batch is completed.
     * This can be logged, files can be closed, resources can be freed.
     * <p>
     * Errors on releases should be caught and not thrown on, so that the batch can terminate successfully.
     */
    public void batchBeendet();

    /**
     * A checkpoint has been written.
     * This means that the last transaction has ended and a new one has been started.
     * The batch can log this and reload objects if necessary.
     *
     * @param satzNummer The satzNumber written to the BatchStatus.
     * @throws BatchAusfuehrungsException If an error occurs.
     */
    public void checkpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException;

    /**
     * This method is called immediately before writing a checkpoint (commit).
     *
     * @param satzNummer the number of the last block processed.
     * @throws BatchAusfuehrungsException If an error occurs.
     */
    public void vorCheckpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException;

    /**
     * This method is called when a rollback has been performed.
     * The batch can thus perform necessary cleanup operations, for example.
     */
    public void rollbackDurchgefuehrt();

    /**
     * This method is called immediately before a rollback is performed.
     * The batch can thus perform e.g. necessary cleanup operations.
     */
    public void vorRollbackDurchgefuehrt();
}
