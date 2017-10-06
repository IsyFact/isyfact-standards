package de.bund.bva.isyfact.task.handler.impl;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import de.bund.bva.isyfact.task.handler.FixedRateHandler;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.*;

/**
 * Liest eine Duration ein, die eine feste Ausführungsrate beschreibt.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public class FixedRateHandlerImpl implements FixedRateHandler {

    /**
     * @return fixedDateTime
     */
    public synchronized Duration getFixedRate(String id, Konfiguration konfiguration) {
        long days = konfiguration.getAsLong(PRAEFIX + id + FIXEDRATEDAYS);
        long hours = konfiguration.getAsLong(PRAEFIX + id + FIXEDRATEHOURS);
        long minutes = konfiguration.getAsLong(PRAEFIX + id + FIXEDRATEMINUTES);
        long seconds = konfiguration.getAsLong(PRAEFIX + id + FIXEDRATESECONDS);

        return Duration.ofDays(days).plus(hours, ChronoUnit.HOURS).plus(minutes, ChronoUnit.MINUTES)
            .plus(seconds, ChronoUnit.SECONDS);
    }
}
