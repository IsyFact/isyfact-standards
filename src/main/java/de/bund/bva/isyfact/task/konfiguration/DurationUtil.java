package de.bund.bva.isyfact.task.konfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.*;

public class DurationUtil {

    public static Duration leseInitialDelay(String id, Konfiguration konfiguration) {
        return leseDuration(id, konfiguration, KonfigurationSchluessel.INITIAL_DELAY);
    }

    public static Duration leseFixedRate(String id, Konfiguration konfiguration) {
        return leseDuration(id, konfiguration, KonfigurationSchluessel.FIXED_RATE);
    }

    public static Duration leseFixedDelay(String id, Konfiguration konfiguration) {
        return leseDuration(id, konfiguration, KonfigurationSchluessel.FIXED_DELAY);
    }

    protected static Duration leseDuration(String id, Konfiguration konfiguration, String art) {
        // TODO Alternativ isy-datetime benutzen?
        long days = konfiguration.getAsLong(PRAEFIX + id + art + TAGE, 0);
        long hours = konfiguration.getAsLong(PRAEFIX + id + art + STUNDEN, 0);
        long minutes = konfiguration.getAsLong(PRAEFIX + id + art + MINUTEN, 0);
        long seconds = konfiguration.getAsLong(PRAEFIX + id + art + SEKUNDEN, 0);

        return Duration.ofDays(days).plus(hours, ChronoUnit.HOURS).plus(minutes, ChronoUnit.MINUTES)
            .plus(seconds, ChronoUnit.SECONDS);
    }

    private DurationUtil() {
    }
}
