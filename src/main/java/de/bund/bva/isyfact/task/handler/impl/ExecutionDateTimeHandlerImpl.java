package de.bund.bva.isyfact.task.handler.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.handler.ExecutionDateTimeHandler;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationException;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.PRAEFIX;
import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.ZEITPUNKT;

/**
 * Der ExecutionDateTimeHandler ist eine Werkzeugklasse für das Festsetzen der Ausführungszeit eines Tasks.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public class ExecutionDateTimeHandlerImpl implements ExecutionDateTimeHandler {
    private final static IsyLogger LOG = IsyLoggerFactory.getLogger(ExecutionDateTimeHandlerImpl.class);

    /**
     * Liefert den Zeitpunkt, bei dem der Task ausgeführt werden soll.
     *
     * @return
     */
    @Override
    public synchronized LocalDateTime getExecutionDateTime(String id, Konfiguration konfiguration) {
        try {
            String executionDateTime = konfiguration.getAsString(PRAEFIX + id + ZEITPUNKT);
            DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern(KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
            return LocalDateTime.parse(executionDateTime, dateTimeFormatter);
        } catch (KonfigurationException e) {
            return null;
        }
    }
}