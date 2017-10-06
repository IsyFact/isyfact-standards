package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.model.FixedDateTime;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

/**
 * Der FixedRateHandler baut FixedDateTime-Objekte.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface FixedRateHandler {
    FixedDateTime getFixedRate(String id, Konfiguration konfiguration);
}
