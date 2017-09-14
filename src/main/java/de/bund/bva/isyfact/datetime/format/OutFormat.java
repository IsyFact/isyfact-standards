package de.bund.bva.isyfact.datetime.format;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Klasse zur formatierten Ausgabe von Datums-, Zeitwerten und Dauern.
 * <p>
 * Folgende Formate werden für Datum und Zeit unterstützt (Bestandteile in Klammern sind optional):
 * <p>
 * <ul>
 *   <li>(Montag,) 17. Juli 2017 14:35:19 (MESZ)</li>
 *   <li>17.07.2017 14:35:19 (+02:00)</li>
 *   <li>17.07.2017</li>
 *   <li>14:35:19</li>
 * </ul>
 *
 * @author Björn Saxe, msg systems ag
 */
public abstract class OutFormat {

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code cccc, dd. MMMM uuuu HH:mm:ss z}
     */
    public static final DateTimeFormatter DATUM_ZEIT_LANG_TAG_ZONE =
        DateTimeFormatter.ofPattern("cccc, dd. MMMM uuuu HH:mm:ss z", Locale.GERMAN);

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code cccc, dd. MMMM uuuu HH:mm:ss}
     */
    public static final DateTimeFormatter DATUM_ZEIT_LANG_TAG =
        DateTimeFormatter.ofPattern("cccc, dd. MMMM uuuu HH:mm:ss", Locale.GERMAN);

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code dd. MMMM uuuu HH:mm:ss z}
     */
    public static final DateTimeFormatter DATUM_ZEIT_LANG_ZONE =
        DateTimeFormatter.ofPattern("dd. MMMM uuuu HH:mm:ss z", Locale.GERMAN);

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code dd.MM.uuuu HH:mm:ss xxx}
     */
    public static final DateTimeFormatter DATUM_ZEIT_ZONE =
        DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss xxx", Locale.GERMAN);

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code dd.MM.uuuu HH:mm:ss}
     */
    public static final DateTimeFormatter DATUM_ZEIT =
        DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss", Locale.GERMAN);

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code dd.MM.uuuu}
     */
    public static final DateTimeFormatter DATUM = DateTimeFormatter.ofPattern("dd.MM.uuuu", Locale.GERMAN);

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code HH:mm:ss}
     */
    public static final DateTimeFormatter ZEIT = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.GERMAN);

    private static final Map<TemporalUnit, String> zeiteinheitenAbkuerzungen = new HashMap<>();

    static {
        zeiteinheitenAbkuerzungen.put(ChronoUnit.MILLIS, "ms");
        zeiteinheitenAbkuerzungen.put(ChronoUnit.SECONDS, "s");
        zeiteinheitenAbkuerzungen.put(ChronoUnit.MINUTES, "min");
        zeiteinheitenAbkuerzungen.put(ChronoUnit.HOURS, "h");
        zeiteinheitenAbkuerzungen.put(ChronoUnit.DAYS, "d");
        zeiteinheitenAbkuerzungen.put(ChronoUnit.MONTHS, "M");
        zeiteinheitenAbkuerzungen.put(ChronoUnit.YEARS, "a");
    }

    /**
     * Wandelt eine {@link Duration} in einen Dauer String um.
     * <p>
     * Zum Format von Dauern siehe {@link InFormat}.
     *
     * @param period
     *     {@link Duration}, die umgewandelt werden soll, nicht null
     * @return Darstellung der {@link Duration} als Dauer String, nicht null
     */
    public static String toDauerString(Period period) {
        Objects.requireNonNull(period);

        return period.getUnits().stream().filter(unit -> period.get(unit) > 0)
            .map(unit -> period.get(unit) + zeiteinheitenAbkuerzungen.get(unit))
            .collect(Collectors.joining(" "));
    }

    /**
     * Wandelt eine {@link Duration} in einen Dauer String um.
     * <p>
     * Zum Format von Dauern siehe {@link InFormat}.
     *
     * @param duration
     *     {@link Duration}, die umgewandelt werdens soll, nicht null
     * @return Darstellung der {@link Duration} als Dauer String, nicht null
     */
    public static String toDauerString(Duration duration) {
        Map<TemporalUnit, Long> einzelwerte = new LinkedHashMap<>();

        Long stunden = duration.toHours();
        einzelwerte.put(ChronoUnit.HOURS, stunden);
        duration = duration.minusHours(stunden);
        Long minuten = duration.toMinutes();
        einzelwerte.put(ChronoUnit.MINUTES, minuten);
        duration = duration.minusMinutes(minuten);
        einzelwerte.put(ChronoUnit.SECONDS, duration.getSeconds());
        einzelwerte.put(ChronoUnit.MILLIS, Math.round(duration.getNano() / 1000000.0d));

        return einzelwerte.entrySet().stream().filter(e -> e.getValue() > 0)
            .map(e -> e.getValue() + zeiteinheitenAbkuerzungen.get(e.getKey()))
            .collect(Collectors.joining(" "));
    }
}
