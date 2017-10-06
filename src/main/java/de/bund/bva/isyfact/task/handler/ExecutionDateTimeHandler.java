package de.bund.bva.isyfact.task.handler;

import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import java.time.LocalDateTime;

/**
 * Der ExecutionDateTimeHandler ist eine Werkzeugklasse für das Festsetzen der Ausführungszeit eines Tasks.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface ExecutionDateTimeHandler {
    LocalDateTime getExecutionDateTime(String id, Konfiguration konfiguration);
}
