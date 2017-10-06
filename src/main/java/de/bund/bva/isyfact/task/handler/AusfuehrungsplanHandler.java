package de.bund.bva.isyfact.task.handler;

import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

/**
 * Der Ausführungsplan eines Tasks.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface AusfuehrungsplanHandler {
    enum Ausfuehrungsplan {ONCE, FIXED_RATE, FIXED_DELAY}

    Ausfuehrungsplan getAusfuehrungsplan(String id, Konfiguration konfiguration);
}
