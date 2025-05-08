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
 * Behaves like the BasicTest batch - but authorization is performed before initialization.
 * <p>
 * The data used for authorization can be stored in the configuration.
 */
@Secured("PRIV_Recht1")
public class GesicherterTestBatch2 extends BasicTestBatch {

    /**
     * This method is secured. {@inheritDoc}
     */
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
