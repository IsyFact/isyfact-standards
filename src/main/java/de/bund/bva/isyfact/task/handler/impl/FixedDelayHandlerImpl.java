package de.bund.bva.isyfact.task.handler.impl;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import de.bund.bva.isyfact.task.handler.FixedDelayHandler;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.*;

/**
 * Liest eine Duration ein, die eine festen zeitlichen Abstand zwischen zwei Ausführungen beschreibt.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public class FixedDelayHandlerImpl implements FixedDelayHandler {

    /**
     * @return fixedDateTime
     */
    @Override
    public synchronized Duration getFixedDelay(String id, Konfiguration konfiguration) {
        // TODO Code robuster gestalten. Alternativ isy-datetime benutzen?
        long days = konfiguration.getAsLong(PRAEFIX + id + FIXEDDELAYDAYS);
        long hours = konfiguration.getAsLong(PRAEFIX + id + FIXEDDELAYHOURS);
        long minutes = konfiguration.getAsLong(PRAEFIX + id + FIXEDDELAYMINUTES);
        long seconds = konfiguration.getAsLong(PRAEFIX + id + FIXEDDELAYSECONDS);

        return Duration.ofDays(days).plus(hours, ChronoUnit.HOURS).plus(minutes, ChronoUnit.MINUTES)
            .plus(seconds, ChronoUnit.SECONDS);
    }
}
