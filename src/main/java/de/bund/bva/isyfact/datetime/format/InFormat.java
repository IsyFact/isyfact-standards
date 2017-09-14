package de.bund.bva.isyfact.datetime.format;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasse zur formatierten Eingabe von Datums-, Zeitwerten und Dauern.
 * <p>
 * Folgende Formate werden unterstützt:
 * <p>
 * <b>Datum: &lt;Tag&gt;.&lt;Monat&gt;.&lt;Jahr&gt;</b>
 * <p>
 * <table summary="Unterstütze Formate für Datum" border="1">
 *   <tr><th>Feld</th><th>Format</th><th>Beispiel</th><th>Pflichtfeld</th></tr>
 *   <tr><td>Tag</td><td>Ganzzahl (1-31)</td><td>3, 03, 14</td><td>Ja</td></tr>
 *   <tr><td>Monat</td><td>Ganzzahl (1-12)</td><td>5, 07, 11</td><td>Ja</td></tr>
 *   <tr><td>Jahr</td><td>Ganzzahl (1-9999)</td><td>9, 476, 2013</td><td>Ja</td></tr>
 * </table>
 * <p>
 * <b>Zeit: &lt;Stunde&gt;:&lt;Minute&gt;:&lt;Sekunde&gt;.&lt;Sekundenbruchteile&gt; &lt;Zeitzone&gt;</b>
 * <p>
 * <table summary="Unterstütze Formate für Zeit" border="1">
 *   <tr><th>Feld</th><th>Format</th><th>Beispiel</th><th>Pflichtfeld</th></tr>
 *   <tr><td>Stunde</td><td>Ganzzahl (1-31)</td><td>3, 03, 14</td><td>Ja</td></tr>
 *   <tr><td>Minute</td><td>Ganzzahl (1-12)</td><td>5, 07, 11</td><td>Ja</td></tr>
 *   <tr><td>Sekunde</td><td>Ganzzahl (1-9999)</td><td>9, 476, 2013</td><td>Nein</td></tr>
 *   <tr><td>Sekundenbruchteile</td><td>max. neunstellige Ganzzahl (1-999999999)</td><td>127 (ms), 235674 (&micro;s), 349672834 (ns)</td><td>Nein</td></tr>
 *   <tr><td>Zeitzone</td><td>Abweichung von UTC oder Name der Zeitzone</td><td>+02:30, -05:00 oder Europe/Berlin</td><td>Nein</td></tr>
 * </table>
 * <p>
 * <b>Dauer: &lt;Anzahl&gt;&lt;Zeiteinheit&gt; (&lt;Anzahl&gt;&lt;Zeiteinheit&gt; ...)</b>
 * <p>
 * <table summary="Unterstütze Einheiten für Dauer" border="1">
 *   <tr><th>Abkürzung</th><th>Zeiteinheit</th></tr>
 *   <tr><td>ms</td><td>Millisekunde</td></tr>
 *   <tr><td>s</td><td>Sekunde</td></tr>
 *   <tr><td>min</td><td>Minute</td></tr>
 *   <tr><td>h</td><td>Stunde</td></tr>
 *   <tr><td>d</td><td>Tag</td></tr>
 *   <tr><td>M</td><td>Monat</td></tr>
 *   <tr><td>a</td><td>Jahr</td></tr>
 * </table>
 *
 * @author Björn Saxe, msg systems ag
 */
public abstract class InFormat {
    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code d.M.u}
     */
    public static final DateTimeFormatter DATUM_D_M_Y = DateTimeFormatter.ofPattern("d.M.u");

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code d.M.uu}
     */
    public static final DateTimeFormatter DATUM_D_M_YY = DateTimeFormatter.ofPattern("d.M.uu");

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code d.M.uuu}
     */
    public static final DateTimeFormatter DATUM_D_M_YYY = DateTimeFormatter.ofPattern("d.M.uuu");

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code d.M.uuuu}
     */
    public static final DateTimeFormatter DATUM_D_M_YYYY = DateTimeFormatter.ofPattern("d.M.uuuu");

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code dd.MM.u}
     */
    public static final DateTimeFormatter DATUM_DD_MM_Y = DateTimeFormatter.ofPattern("dd.MM.u");

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code dd.MM.u}
     */
    public static final DateTimeFormatter DATUM_DD_MM_YY = DateTimeFormatter.ofPattern("dd.MM.uu");

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code dd.MM.uuu}
     */
    public static final DateTimeFormatter DATUM_DD_MM_YYY = DateTimeFormatter.ofPattern("dd.MM.uuu");

    /**
     * {@link DateTimeFormatter} für das Datumsformat {@code dd.MM.uuuu}
     */
    public static final DateTimeFormatter DATUM_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd.MM.uuuu");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code H:mm}
     */
    public static final DateTimeFormatter ZEIT = DateTimeFormatter.ofPattern("H:mm");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code H:mm:ss}
     */
    public static final DateTimeFormatter ZEIT_SEK = DateTimeFormatter.ofPattern("H:mm:ss");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code H:mm:ss.SSS}
     */
    public static final DateTimeFormatter ZEIT_SEK_MILLISEK = DateTimeFormatter.ofPattern("H:mm:ss.SSS");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code H:mm:ss.SSSSSS}
     */
    public static final DateTimeFormatter ZEIT_SEK_MIKROSEK = DateTimeFormatter.ofPattern("H:mm:ss.SSSSSS");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code H:mm:ss.SSSSSSSSS}
     */
    public static final DateTimeFormatter ZEIT_SEK_NANOSEK = DateTimeFormatter.ofPattern("H:mm:ss.SSSSSSSSS");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code H:mm xxx}
     */
    public static final DateTimeFormatter ZEIT_OFFSET = DateTimeFormatter.ofPattern("H:mm xxx");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code H:mm:ss xxx}
     */
    public static final DateTimeFormatter ZEIT_SEK_OFFSET = DateTimeFormatter.ofPattern("H:mm:ss xxx");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code H:mm:ss.SSS xxx}
     */
    public static final DateTimeFormatter ZEIT_SEK_MILLISEK_OFFSET =
        DateTimeFormatter.ofPattern("H:mm:ss.SSS xxx");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code H:mm:ss.SSSSSS xxx}
     */
    public static final DateTimeFormatter ZEIT_SEK_MIKROSEK_OFFSET =
        DateTimeFormatter.ofPattern("H:mm:ss.SSSSSS xxx");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code H:mm:ss.SSSSSSSSS xxx}
     */
    public static final DateTimeFormatter ZEIT_SEK_NANOSEK_OFFSET =
        DateTimeFormatter.ofPattern("H:mm:ss.SSSSSSSSS xxx");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm}
     */
    public static final DateTimeFormatter ZEIT_0H = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss.SSS}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_MILLISEK = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss.SSSSSS}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_MIKROSEK =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss.SSSSSSSSS}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_NANOSEK =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm xxx}
     */
    public static final DateTimeFormatter ZEIT_0H_OFFSET = DateTimeFormatter.ofPattern("HH:mm xxx");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss xxx}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_OFFSET = DateTimeFormatter.ofPattern("HH:mm:ss xxx");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss.SSS xxx}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_MILLISEK_OFFSET =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS xxx");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss.SSSSSS xxx}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_MIKROSEK_OFFSET =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS xxx");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss.SSSSSSSSS xxx}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_NANOSEK_OFFSET =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS xxx");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm z}
     */
    public static final DateTimeFormatter ZEIT_0H_ZONE = DateTimeFormatter.ofPattern("HH:mm z");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss z}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_ZONE = DateTimeFormatter.ofPattern("HH:mm:ss z");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss.SSS z}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_MILLISEK_ZONE =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS z");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss.SSSSSS z}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_MIKROSEK_ZONE =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS z");

    /**
     * {@link DateTimeFormatter} für das Zeitformat {@code HH:mm:ss.SSSSSSSSS z}
     */
    public static final DateTimeFormatter ZEIT_0H_SEK_NANOSEK_ZONE =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS z");

    private static final DateTimeFormatter datumFormatter =
        DateTimeFormatter.ofPattern("d[d].M[M].[u][uu][uuu][uuuu]");

    private static final DateTimeFormatter zeitFormatter =
        DateTimeFormatter.ofPattern("H[H]:mm[:ss[.SSSSSSSSS][.SSSSSS][.SSS]][ xxx][ z]");

    private static final DateTimeFormatter datumZeitFormatter = DateTimeFormatter
        .ofPattern("d[d].M[M].[u][uu][uuu][uuuu] H[H]:mm[:ss[.SSSSSSSSS][.SSSSSS][.SSS]][ xxx][ z]");

    /**
     * Parst einen String in eine {@code LocalTime}.
     *
     * @param text
     *     der Text, der geparst werden soll, nicht null
     * @return die geparste Zeit, nicht null
     * @throws DateTimeParseException
     *     wenn die Eingabe nicht geparst werden kann
     */
    public static LocalTime parseToLocalTime(String text) {
        return zeitFormatter.parse(text, LocalTime::from);
    }

    /**
     * Parst einen String in eine {@code OffsetTime}.
     *
     * @param text
     *     der Text, der geparst werden soll, nicht null
     * @return die geparste Zeit, nicht null
     * @throws DateTimeParseException
     *     wenn die Eingabe nicht geparst werden kann
     */
    public static OffsetTime parseToOffsetTime(String text) {
        return zeitFormatter.parse(text, OffsetTime::from);
    }

    /**
     * Parst einen String in ein {@link LocalDate}.
     *
     * @param text
     *     der Text, der geparst werden soll, nicht null
     * @return das geparste Datum, nicht null
     * @throws DateTimeParseException
     *     wenn die Eingabe nicht geparst werden kann
     */
    public static LocalDate parseToLocalDate(String text) {
        return datumFormatter.parse(text, LocalDate::from);
    }

    /**
     * Parst einen String in eine {@link LocalDateTime}.
     *
     * @param text
     *     der Text, der geparst werden soll, nicht null
     * @return das geparste Datum/Zeit, nicht null
     * @throws DateTimeParseException
     *     wenn die Eingabe nicht geparst werden kann
     */
    public static LocalDateTime parseToLocalDateTime(String text) {
        return datumZeitFormatter.parse(text, LocalDateTime::from);
    }

    /**
     * Parst einen String in eine {@link OffsetDateTime}.
     *
     * @param text
     *     der Text, der geparst werden soll, nicht null
     * @return das geparste Datum/Zeit, nicht null
     * @throws DateTimeParseException
     *     wenn die Eingabe nicht geparst werden kann
     */
    public static OffsetDateTime parseToOffsetDateTime(String text) {
        return datumZeitFormatter.parse(text, OffsetDateTime::from);
    }

    /**
     * Parst einen String in eine {@link ZonedDateTime}.
     *
     * @param text
     *     der Text, der geparst werden soll, nicht null
     * @return das geparste Datum/Zeit, nicht null
     * @throws DateTimeParseException
     *     wenn die Eingabe nicht geparst werden kann
     */
    public static ZonedDateTime parseToZonedDateTime(String text) {
        return datumZeitFormatter.parse(text, ZonedDateTime::from);
    }

    /**
     * Parst einen String in eine {@link Period}.
     * <p>
     * Enthält der String Zeiteinheiten &lt; Tag, wird eine {@link DateTimeParseException} geworfen.
     *
     * @param text
     *     der Text, der geparst werden soll, nicht null
     * @return die geparste Period, nicht null
     * @throws DateTimeParseException
     *     wenn die Eingabe nicht geparst werden kann oder Zeiteinheiten &gt;
     *     Stunden enthalten sind
     */
    public static Period parseToPeriod(String text) {
        Objects.requireNonNull(text);

        ConcurrentMap<ChronoUnit, Pattern> einheitenPattern = new ConcurrentHashMap<>();
        einheitenPattern.put(ChronoUnit.YEARS, Pattern.compile("(\\d+)a"));
        einheitenPattern.put(ChronoUnit.MONTHS, Pattern.compile("(\\d+)M"));
        einheitenPattern.put(ChronoUnit.DAYS, Pattern.compile("(\\d+)d"));

        Map<ChronoUnit, Integer> einheitenWerte = parseDauer(text, einheitenPattern);

        return Period.of(einheitenWerte.get(ChronoUnit.YEARS), einheitenWerte.get(ChronoUnit.MONTHS),
            einheitenWerte.get(ChronoUnit.DAYS));
    }

    /**
     * Parst einen String in eine {@link Duration}.
     * <p>
     * Enthält der String Zeiteinheiten &gt; Stunde, wird eine {@link DateTimeParseException} geworfen.
     *
     * @param text
     *     der Text, der geparst werden soll, nicht null
     * @return die geparste {@link Duration}, nicht null
     * @throws DateTimeParseException
     *     wenn die Eingabe nicht geparst werden kann
     */
    public static Duration parseToDuration(String text) {
        Objects.requireNonNull(text);

        ConcurrentMap<ChronoUnit, Pattern> einheitenPattern = new ConcurrentHashMap<>();
        einheitenPattern.put(ChronoUnit.HOURS, Pattern.compile("(\\d+)h"));
        einheitenPattern.put(ChronoUnit.MINUTES, Pattern.compile("(\\d+)min"));
        einheitenPattern.put(ChronoUnit.SECONDS, Pattern.compile("(\\d+)s"));
        einheitenPattern.put(ChronoUnit.MILLIS, Pattern.compile("(\\d+)ms"));

        Map<ChronoUnit, Integer> einheitenWerte = parseDauer(text, einheitenPattern);

        return Duration.ofHours(einheitenWerte.get(ChronoUnit.HOURS))
            .plusMinutes(einheitenWerte.get(ChronoUnit.MINUTES))
            .plusSeconds(einheitenWerte.get(ChronoUnit.SECONDS))
            .plusMillis(einheitenWerte.get(ChronoUnit.MILLIS));
    }

    private static Map<ChronoUnit, Integer> parseDauer(String s,
        ConcurrentMap<ChronoUnit, Pattern> einheitenPattern) {

        List<String> teile = Arrays.asList(s.trim().split("\\s+"));

        Map<ChronoUnit, Integer> einheitenWerte = new EnumMap<>(ChronoUnit.class);
        einheitenPattern.forEach((einheit, v) -> einheitenWerte.put(einheit, 0));

        for (String teil : teile) {
            boolean noMatch = true;
            for (Map.Entry<ChronoUnit, Pattern> einheit : einheitenPattern.entrySet()) {
                Matcher matcher = einheit.getValue().matcher(teil);
                if (matcher.matches()) {
                    einheitenPattern.remove(einheit.getKey());
                    einheitenWerte.put(einheit.getKey(), Integer.parseInt(matcher.group(1)));
                    noMatch = false;
                    break;
                }
            }

            if (noMatch) {
                throw new DateTimeParseException(null, s, s.indexOf(teil));
            }
        }

        return einheitenWerte;
    }
}
