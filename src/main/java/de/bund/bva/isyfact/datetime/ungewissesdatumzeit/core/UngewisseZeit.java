package de.bund.bva.isyfact.datetime.ungewissesdatumzeit.core;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.Optional;

// TODO Verschieben nach de.bund.bva.isyfact.datetime.core
/**
 * Darstellung einer ungewissen Zeit. Eine Zeit ist ungewiss, wenn Teile der Zeit nicht bekannt sind.
 * <p>
 * Die Klasse ist zur Verwendung im Anwendungskern gedacht.
 *
 * @author Björn Saxe, msg systems ag
 */
public class UngewisseZeit {

    private static final DateTimeFormatter format =
        new DateTimeFormatterBuilder().appendPattern("['xx:xx:xx']").appendPattern("[HH:'xx:xx']")
            .appendPattern("[HH:mm:'xx']").appendPattern("[HH:mm:ss]").parseStrict().toFormatter();

    private LocalTime anfang;

    private LocalTime ende;

    private UngewisseZeit() {

    }

    private UngewisseZeit(LocalTime anfang, LocalTime ende) {
        this.anfang = anfang;
        this.ende = ende;
    }

    /**
     * Erstellt ein {@link UngewisseZeit}, bei der kein Wert gesetzt ist.
     *
     * @return ein {@link UngewisseZeit} ohne Stunde, Minute oder Sekunde gesetzt
     */
    public static UngewisseZeit leer() {
        return new UngewisseZeit();
    }

    /**
     * Erstellt eine {@link UngewisseZeit}, bei nur die Stunde bekannt ist.
     *
     * @param stunde
     *     die Stunde
     * @return eine {@link UngewisseZeit} mit der Stunde gesetzt
     */
    public static UngewisseZeit of(int stunde) {
        // TODO Maximum und Minimumwerte aus ChronoField beziehen?
        return new UngewisseZeit(LocalTime.of(stunde, 0, 0), LocalTime.of(stunde, 59, 59));
    }

    /**
     * Erstellt eine {@link UngewisseZeit}, bei der Stunde und Minute bekannt sind.
     *
     * @param stunde
     *     die Stunde
     * @param minute
     *     die Minute
     * @return eine {@link UngewisseZeit} mit der Stunde und Minute gesetzt
     */
    public static UngewisseZeit of(int stunde, int minute) {
        // TODO Maximum und Minimumwerte aus ChronoField beziehen?
        return new UngewisseZeit(LocalTime.of(stunde, minute, 0), LocalTime.of(stunde, minute, 59));
    }

    /**
     * Erstellt eine {@link UngewisseZeit}, bei der Stunde, Minute und Sekunde bekannt sind.
     * Damit ist die Zeit nicht mehr ungewiss und kann mit {@link UngewisseZeit#toLocalTime()}
     * in eine {@link java.time.LocalDateTime} konvertiert werden.
     *
     * @param stunde
     *     die Stunde
     * @param minute
     *     die Minute
     * @param sekunde
     *     die Sekunde
     * @return eine {@link UngewisseZeit} mit der Stunde, Minute und Sekunde gesetzt
     */
    public static UngewisseZeit of(int stunde, int minute, int sekunde) {
        return new UngewisseZeit(LocalTime.of(stunde, minute, sekunde),
            LocalTime.of(stunde, minute, sekunde));
    }

    /**
     * Erstellt eine {@link UngewisseZeit} bei der Anfang und Ende des Zeitraums übergeben werden.
     *
     * @param vonInklusive
     *     der Anfang des Zeitraums inklusive, nicht null
     * @param bisInklusive
     *     das Ende des Zeitraums inklusive, nicht null
     * @return eine {@link UngewisseZeit} mit dem gesetzten Zeitraum
     */
    public static UngewisseZeit of(LocalTime vonInklusive, LocalTime bisInklusive) {
        Objects.requireNonNull(vonInklusive);
        Objects.requireNonNull(bisInklusive);

        // TODO Idee: Robust reagieren und Zeitraum einfach herumdrehen?
        if (vonInklusive.isAfter(bisInklusive)) {
            // TODO Sprechende Fehlermeldung
            throw new DateTimeException(null);
        }

        return new UngewisseZeit(vonInklusive, bisInklusive);
    }

    /**
     * Gibt wahr zurück, wenn in dieser {@link UngewisseZeit} alle Werte unbekannt sind.
     *
     * @return true wenn alle Werte unbekannt
     */
    public boolean isLeer() {
        return anfang == null && ende == null;
    }

    /**
     * Gibt wahr zurück, wenn in dieser {@link UngewisseZeit} mindestens ein Wert unbekannt ist.
     *
     * @return true wenn mindestens ein Wert unbekannt
     */
    public boolean isUngewiss() {
        return anfang == null || !anfang.equals(ende);
    }

    /**
     * Gibt die {@link LocalTime} zurück, die den Anfang des Zeitraums darstellt, der durch diese
     * {@link UngewisseZeit} dargestellt wird.
     *
     * @return Anfang des Zeitraums als {@link LocalTime}
     */
    public LocalTime getAnfang() {
        return anfang;
    }

    /**
     * Gibt die {@link LocalTime} zurück, die das Ende (inklusive) des Zeitraums darstellt, der durch diese
     * {@link UngewisseZeit} dargestellt wird.
     *
     * @return Ende (inklusive) des Zeitraums als {@link LocalTime}
     */
    public LocalTime getEnde() {
        return ende;
    }

    /**
     * Gibt ein {@link java.util.Optional} zurück, das die Stunde dieser {@link UngewisseZeit} enthält.
     *
     * @return ein {@link java.util.Optional} mit der Stunde, wenn dieses gesetzt ist, sonst ein leeres {@link java.util.Optional}
     */
    public Optional<Integer> getStunde() {
        if (nurStundeBekannt()) {
            return Optional.of(anfang.getHour());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gibt ein {@link java.util.Optional} zurück, das die Minute dieser {@link UngewisseZeit} enthält.
     * Sind der Anfang und das Ende dieser ungewissen Zeit nicht in der selben Minute, wird ein leeres
     * {@link java.util.Optional} zurückgegeben.
     *
     * @return ein {@link java.util.Optional} mit der Minute, wenn gesetzt und eindeutig, sonst ein leeres {@link java.util.Optional}
     */
    public Optional<Integer> getMinute() {
        if (nurMinuteUndStundeBekannt()) {
            return Optional.of(anfang.getMinute());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gibt ein {@link java.util.Optional} zurück, das die Sekunde dieser {@link UngewisseZeit} enthält.
     * Sind der Anfang und das Ende dieser ungewissen Zeit nicht in der selben Sekunde, wird ein leeres
     * {@link java.util.Optional} zurückgegeben.
     *
     * @return ein {@link java.util.Optional} mit der Sekunde, wenn gesetzt und eindeutig, sonst ein leeres {@link java.util.Optional}
     */
    public Optional<Integer> getSekunde() {
        if (isUngewiss()) {
            return Optional.empty();
        } else {
            return Optional.of(anfang.getSecond());
        }
    }

    /**
     * Gibt ein {@link java.util.Optional} zurück, das diese {@link UngewisseZeit} als {@link java.time.LocalTime} enthält.
     *
     * @return ein {@link java.util.Optional} mit der {@link java.time.LocalTime}, wenn eindeutige Werte gesetzt sind, sonst ein leeres {@link java.util.Optional}
     */
    public Optional<LocalTime> toLocalTime() {
        if (isUngewiss()) {
            return Optional.empty();
        } else {
            return Optional.of(anfang);
        }
    }

    /**
     * Parst eine ungewisse Zeit.
     * <p>
     * Folgende Formate werden unterstützt:
     * <p>
     * <table summary="Unterstütze Formate" border="1">
     * <tr><th>Fall</th><th>Eingabe</th><th>Interner Zeitraum</th></tr>
     * <tr><td>Sekunde unbekannt</td><td>14:34:xx</td><td>14:34:00 – 14:34:59</td></tr>
     * <tr><td>Sekunde und Minute unbekannt</td><td>14:xx:xx</td><td>14:00:00 – 14:59:59</td></tr>
     * <tr><td>Zeit komplett unbekannt</td><td>xx:xx:xx</td><td>nicht gesetzt (null)</td></tr>
     * </table>
     *
     * @param text
     *     der Text, der geparst werden soll
     * @return die geparste {@link UngewisseZeit}
     * @throws DateTimeParseException
     *     wenn der Text nicht geparst werden kann
     */
    public static UngewisseZeit parse(String text) {
        Objects.requireNonNull(text);

        if (text.isEmpty()) {
            // TODO Sprechende Fehlermeldung
            throw new DateTimeParseException(null, text, 0);
        }

        TemporalAccessor ta = format.parse(text);

        if (ta.isSupported(ChronoField.SECOND_OF_MINUTE) && ta.get(ChronoField.SECOND_OF_MINUTE) != 0) {
            return UngewisseZeit.of(ta.get(ChronoField.HOUR_OF_DAY), ta.get(ChronoField.MINUTE_OF_HOUR),
                ta.get(ChronoField.SECOND_OF_MINUTE));
        } else if (ta.isSupported(ChronoField.MINUTE_OF_HOUR) && ta.get(ChronoField.MINUTE_OF_HOUR) != 0) {
            return UngewisseZeit.of(ta.get(ChronoField.HOUR_OF_DAY), ta.get(ChronoField.MINUTE_OF_HOUR));
        } else if (ta.isSupported(ChronoField.HOUR_OF_DAY)) {
            return UngewisseZeit.of(ta.get(ChronoField.HOUR_OF_DAY));
        } else {
            return UngewisseZeit.leer();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UngewisseZeit)) {
            return false;
        }
        UngewisseZeit that = (UngewisseZeit) o;
        return Objects.equals(anfang, that.anfang) && Objects.equals(ende, that.ende);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anfang, ende);
    }

    /**
     * Gibt diese {@link UngewisseZeit} als String im Format {@code HH:mm.ss} zurück.
     * Unbekannte Werte werde mit {@code xx} dargestellt, z.B. {@code 14:xx:xx}. Ist die Zeit so nicht darstellbar,
     * wird sie als Zeitraum dargestellt, z.B. {@code 12:30:00 - 18:30:00}.
     *
     * @return Repräsentation dieser {@link UngewisseZeit} als {@link String}
     */
    @Override
    public String toString() {
        String toString;

        DateTimeFormatter HHmmss = DateTimeFormatter.ofPattern("HH:mm:ss");

        if (isLeer()) {
            toString = "xx:xx:xx";
        } else if (anfang.equals(ende)) {
            toString = anfang.format(HHmmss);
        } else if (nurStundeBekannt()) {
            toString = String.format("%02d:xx:xx", anfang.getHour());
        } else if (nurMinuteUndStundeBekannt()) {
            toString = String.format("%02d:%02d:xx", anfang.getHour(), anfang.getMinute());
        } else {
            toString = String.format("%s - %s", anfang.format(HHmmss), ende.format(HHmmss));
        }

        return toString;
    }

    private boolean nurMinuteUndStundeBekannt() {
        return anfang != null && anfang.getSecond() == 0 && anfang.plusMinutes(1).equals(ende.plusSeconds(1));
    }

    private boolean nurStundeBekannt() {
        return anfang != null && anfang.getMinute() == 0 && anfang.getSecond() == 0 && anfang.plusHours(1)
            .equals(ende.plusSeconds(1));
    }
}
