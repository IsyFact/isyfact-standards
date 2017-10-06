package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.model.FixedDateTime;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

/**
 * Der FixedDelayHandler baut FixedDelayTime-Objekte.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface FixedDelayHandler {
    FixedDateTime getFixedDelay(String id, Konfiguration konfiguration);
}
