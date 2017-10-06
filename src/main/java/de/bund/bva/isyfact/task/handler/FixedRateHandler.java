package de.bund.bva.isyfact.task.handler;

import java.time.Duration;

import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

/**
 * Liest eine Duration ein, die eine feste Ausführungsrate beschreibt.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface FixedRateHandler {
    Duration getFixedRate(String id, Konfiguration konfiguration);
}
