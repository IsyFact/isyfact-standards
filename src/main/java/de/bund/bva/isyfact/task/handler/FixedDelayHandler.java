package de.bund.bva.isyfact.task.handler;

import java.time.Duration;

import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

/**
 * Der FixedDelayHandler baut FixedDelayTime-Objekte.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface FixedDelayHandler {
    Duration getFixedDelay(String id, Konfiguration konfiguration);
}
