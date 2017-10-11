package de.bund.bva.isyfact.task.konfiguration;

import java.time.Duration;

import de.bund.bva.isyfact.datetime.format.InFormat;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.PRAEFIX;

public class DurationUtil {

    public static Duration leseInitialDelay(String id, Konfiguration konfiguration) {
        return InFormat.parseToDuration(
            konfiguration.getAsString(PRAEFIX + id + KonfigurationSchluessel.INITIAL_DELAY, "0s"));
    }

    public static Duration leseFixedRate(String id, Konfiguration konfiguration) {
        return InFormat.parseToDuration(
            konfiguration.getAsString(PRAEFIX + id + KonfigurationSchluessel.FIXED_RATE, "0s"));
    }

    public static Duration leseFixedDelay(String id, Konfiguration konfiguration) {
        return InFormat.parseToDuration(
            konfiguration.getAsString(PRAEFIX + id + KonfigurationSchluessel.FIXED_DELAY, "0s"));
    }

    private DurationUtil() {
    }
}
