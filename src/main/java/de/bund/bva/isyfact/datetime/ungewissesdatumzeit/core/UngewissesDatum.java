package de.bund.bva.isyfact.datetime.ungewissesdatumzeit.core;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.Optional;

// TODO Verschieben nach de.bund.bva.isyfact.datetime.core
/**
 * Darstellung eines ungewissen Datums. Ein Datum ist ungewiss, wenn Teile des Datums nicht bekannt sind.
 * <p>
 * Die Klasse ist zur Verwendung im Anwendungskern gedacht.
 *
 * @author Björn Saxe, msg systems ag
 */
public class UngewissesDatum {

    private static final DateTimeFormatter format =
        new DateTimeFormatterBuilder().appendPattern("[00.00.0000]").appendPattern("[00.00.yyyy]")
            .appendPattern("[00.MM.yyyy]").appendPattern("['xx.xx.xxxx']").appendPattern("['xx.xx.'yyyy]")
            .appendPattern("['xx.'MM.yyyy]").appendPattern("[dd.MM.yyyy]").parseStrict().toFormatter();

    private LocalDate anfang;

    private LocalDate ende;

    private UngewissesDatum() {
    }

    private UngewissesDatum(int jahr) {
        // TODO Minimumwerte aus ChronoField beziehen?
        this.anfang = LocalDate.of(jahr, 1, 1);
        this.ende = anfang.plusYears(1).minusDays(1);
    }

    private UngewissesDatum(int jahr, int monat) {
        // TODO Minimumwerte aus ChronoField beziehen?
        this.anfang = LocalDate.of(jahr, monat, 1);
        this.ende = anfang.plusMonths(1).minusDays(1);
    }

    private UngewissesDatum(int jahr, int monat, int tag) {
        this.anfang = LocalDate.of(jahr, monat, tag);
        this.ende = LocalDate.of(jahr, monat, tag);
    }

    private UngewissesDatum(LocalDate anfang, LocalDate ende) {
        this.anfang = anfang;
        this.ende = ende;
    }

    private static void pruefeJahr(int jahr) {
        // TODO Wir schränken hier den Bereich ein. ChronoField.YEAR.checkValidIntValue(jahr) nutzen?
        if (jahr < 0 || jahr > 9999) {
            // TODO Exception ohne Nachricht. Entfällt, wenn die Validierung von ChronoField benutzt wird.
            throw new DateTimeException(null);
        }
    }

    private static void pruefeMonat(int monat) {
        // TODO ChronoField.MONTH_OF_YEAR.checkValidIntValue(monat) nutzen?
        if (monat < 0 || monat > 12) {
            // TODO Exception ohne Nachricht. Entfällt, wenn die Validierung von ChronoField benutzt wird.
            throw new DateTimeException(null);
        }
    }

    // TODO Diese Methode ist nicht gut zu verstehen. Validierungslogik entfernen und auf LocalDate vertrauen? Dort wird mit ChronoField validiert.
    private static void pruefeJahrMonatTag(int... feld) {
        for (int i = 0; i < feld.length - 1; i++) {
            if (feld[i] == 0 && feld[i + 1] > 0) {
                throw new DateTimeException(null);
            }
        }

        switch (feld.length) {
        case 1:
            pruefeJahr(feld[0]);
            break;
        case 2:
            pruefeJahr(feld[0]);
            pruefeMonat(feld[1]);
            break;
        case 3:
            try {
                LocalDate.of(feld[0], feld[1], feld[2]);
            } catch (DateTimeException d) {
                throw new DateTimeException(null);
            }
            break;
        default:
            throw new DateTimeException(null);
        }
    }

    /**
     * Erstellt ein {@link UngewissesDatum}, bei dem kein Wert gesetzt ist.
     *
     * @return ein {@link UngewissesDatum} ohne Jahr, Monat oder Tag gesetzt
     */
    public static UngewissesDatum leer() {
        return new UngewissesDatum();
    }

    /**
     * Gibt wahr zurück, wenn in diesem {@link UngewissesDatum} alle Werte unbekannt sind.
     *
     * @return true wenn alle Werte unbekannt
     */
    public boolean isLeer() {
        return anfang == null && ende == null;
    }

    /**
     * Gibt wahr zurück, wenn in diesem {@link UngewissesDatum} mindestens ein Wert unbekannt ist.
     *
     * @return wenn mindestens ein Wert unbekannt
     */
    public boolean isUngewiss() {
        return anfang == null || !anfang.isEqual(ende);
    }

    /**
     * Erstellt ein {@link UngewissesDatum}, bei dem nur das Jahr bekannt ist.
     *
     * @param jahr
     *     das Jahr
     * @return ein {@link UngewissesDatum} mit dem Jahr gesetzt
     * @throws DateTimeException
     *     wenn jahr einen ungültigen Wert hat
     */
    public static UngewissesDatum of(int jahr) {
        pruefeJahrMonatTag(jahr);

        if (jahr == 0) {
            return UngewissesDatum.leer();
        } else {
            return new UngewissesDatum(jahr);
        }
    }

    /**
     * Erstellt ein {@link UngewissesDatum}, bei dem das Jahr und der Monat bekannt sind.
     *
     * @param jahr
     *     das Jahr
     * @param monat
     *     der Monat
     * @return ein {@link UngewissesDatum} mit dem Jahr und Monat gesetzt
     * @throws DateTimeException
     *     wenn jahr oder monat einen ungültigen Wert haben
     */
    public static UngewissesDatum of(int jahr, int monat) {
        pruefeJahrMonatTag(jahr, monat);

        return new UngewissesDatum(jahr, monat);
    }

    /**
     * Erstellt ein {@link UngewissesDatum}, bei dem Jahr, Monat und Tag bekannt sind.
     * Damit ist das Datum nicht mehr ungewiss und kann mit {@link UngewissesDatum#toLocalDate()}
     * in ein {@link LocalDate} konvertiert werden.
     *
     * @param jahr
     *     das Jahr
     * @param monat
     *     der Monat
     * @param tag
     *     der Tag
     * @return ein {@link UngewissesDatum} mit dem Jahr, Monat und Tag gesetzt
     * @throws DateTimeException
     *     wenn jahr, monat oder tag einen ungültigen Wert haben
     */
    public static UngewissesDatum of(int jahr, int monat, int tag) {
        pruefeJahrMonatTag(jahr, monat, tag);

        return new UngewissesDatum(jahr, monat, tag);
    }

    /**
     * Erstellt ein {@link UngewissesDatum} bei dem Anfang und Ende des Zeitraums des Datums übergeben werden.
     *
     * @param vonInklusive
     *     der Anfang des Zeitraums inklusive, nicht null
     * @param bisInklusive
     *     das Ende des Zeitraums inklusive, nicht null
     * @return ein {@link UngewissesDatum} mit dem gesetzten Zeitraum
     * @throws DateTimeException
     *     wenn vonInklusive vor bisInklusive liegt
     */
    public static UngewissesDatum of(LocalDate vonInklusive, LocalDate bisInklusive) {
        Objects.requireNonNull(vonInklusive);
        Objects.requireNonNull(bisInklusive);

        // TODO Idee: Robust reagieren und Zeitraum einfach herumdrehen?
        if (bisInklusive.isBefore(vonInklusive) || vonInklusive.getYear() != bisInklusive.getYear()) {
            // TODO Sprechende Fehlermeldung
            throw new DateTimeException(null);
        }

        return new UngewissesDatum(vonInklusive, bisInklusive);
    }

    /**
     * Parst ein ungewisses Datum.
     * <p>
     * Folgende Formate werden unterstützt:
     * <p>
     * <table summary="Unterstütze Formate" border="1">
     * <tr><th>Fall</th><th>Eingabe mit 0</th><th>Eingabe mit x</th><th>Interner Zeitraum</th></tr>
     * <tr><td>Tag unbekannt</td><td>00.05.1966</td><td>xx.05.1966</td><td>1.5.1966 – 31.5.1966</td></tr>
     * <tr><td>Tag und Monat unbekannt</td><td>00.00.1966</td><td>xx.xx.1966</td><td>1.1.1966 – 31.12.1966</td></tr>
     * <tr><td>Datum komplett unbekannt</td><td>00.00.0000</td><td>xx.xx.xxxx</td><td>nicht gesetzt (null)</td></tr>
     * </table>
     *
     * @param text
     *     der Text, der geparst werden soll
     * @return das geparste {@link UngewissesDatum}
     * @throws DateTimeParseException
     *     wenn der Text nicht geparst werden kann
     */
    public static UngewissesDatum parse(String text) {
        Objects.requireNonNull(text);

        if (text.isEmpty()) {
            throw new DateTimeParseException(null, text, 0);
        }

        TemporalAccessor ta = format.parse(text);

        if (ta.isSupported(ChronoField.DAY_OF_MONTH)) {
            return UngewissesDatum.of(ta.get(ChronoField.YEAR), ta.get(ChronoField.MONTH_OF_YEAR),
                ta.get(ChronoField.DAY_OF_MONTH));
        } else if (ta.isSupported(ChronoField.MONTH_OF_YEAR)) {
            return UngewissesDatum.of(ta.get(ChronoField.YEAR), ta.get(ChronoField.MONTH_OF_YEAR));
        } else if (ta.isSupported(ChronoField.YEAR)) {
            return UngewissesDatum.of(ta.get(ChronoField.YEAR));
        } else {
            return UngewissesDatum.leer();
        }
    }

    /**
     * Gibt ein {@link java.util.Optional} zurück, das das Jahr dieses Datums enthält.
     *
     * @return ein {@link java.util.Optional} mit dem Jahr, wenn dieses gesetzt ist, sonst ein leeres {@link java.util.Optional}
     */
    public Optional<Integer> getJahr() {
        if (anfang != null || nurJahrBekannt()) {
            return Optional.of(anfang.getYear());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gibt ein {@link java.util.Optional} zurück, das den Monat dieses Datums enthält.
     * Sind der Anfang und das Ende dieses ungewissen Datums nicht im selben Monat, wird ein leeres
     * {@link java.util.Optional} zurückgegeben.
     *
     * @return ein {@link java.util.Optional} mit dem Monat, wenn gesetzt und eindeutig, sonst ein leeres {@link java.util.Optional}
     */
    public Optional<Integer> getMonat() {
        if ((anfang != null && anfang.getMonth() == ende.getMonth()) || nurMonatUndJahrBekannt()) {
            return Optional.of(anfang.getMonthValue());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gibt ein {@link java.util.Optional} zurück, das den Tag dieses Datums enthält.
     * Sind der Anfang und das Ende dieses ungewissen Datums nicht am selben Tag, wird ein leeres
     * {@link java.util.Optional} zurückgegeben.
     *
     * @return ein {@link java.util.Optional} mit dem Tag, wenn gesetzt und eindeutig, sonst ein leeres {@link java.util.Optional}
     */
    public Optional<Integer> getTag() {
        if (isUngewiss()) {
            return Optional.empty();
        } else {
            return Optional.of(anfang.getDayOfMonth());
        }
    }

    /**
     * Gibt ein {@link java.util.Optional} zurück, das dieses Datum als {@link LocalDate} enthält.
     *
     * @return ein {@link java.util.Optional} mit dem {@link LocalDate}, wenn eindeutige Werte gesetzt sind, sonst ein leeres {@link java.util.Optional}
     */
    public Optional<LocalDate> toLocalDate() {
        if (isUngewiss()) {
            return Optional.empty();
        } else {
            return Optional.of(LocalDate.of(anfang.getYear(), anfang.getMonth(), anfang.getDayOfMonth()));
        }
    }

    /**
     * Gibt das {@link LocalDate} zurück, das den Anfang des Zeitraums darstellt, der durch dieses
     * {@link UngewissesDatum} dargestellt wird.
     *
     * @return der Anfang des Zeitraums als {@link LocalDate}
     */
    public LocalDate getAnfang() {
        return anfang;
    }

    /**
     * Gibt das {@link LocalDate} zurück, das das Ende (inklusive) des Zeitraums darstellt, der durch dieses
     * {@link UngewissesDatum} dargestellt wird.
     *
     * @return das Ende (inklusive) des Zeitraums als {@link LocalDate}
     */
    public LocalDate getEnde() {
        return ende;
    }

    /**
     * Gibt dieses {@link UngewissesDatum} als String im Format {@code dd.MM.uuuu} zurück.
     * Unbekannte Werte werde mit {@code xx} dargestellt, z.B. {@code xx.08.2017}. Ist das Datum so nicht darstellbar,
     * wird es als Zeitraum dargestellt, z.B. {@code 10.08.2017 - 31.08.2017}.
     *
     * @return Repräsentation dieses {@link UngewissesDatum} als {@link String}
     */
    @Override
    public String toString() {
        String toString;

        DateTimeFormatter ddMMuuuu = DateTimeFormatter.ofPattern("dd.MM.uuuu");

        if (isLeer()) {
            toString = "xx.xx.xxxx";
        } else if (anfang.equals(ende)) {
            toString = anfang.format(ddMMuuuu);
        } else if (nurJahrBekannt()) {
            toString = String.format("xx.xx.%04d", anfang.getYear());
        } else if (nurMonatUndJahrBekannt()) {
            toString = String.format("xx.%02d.%04d", anfang.getMonthValue(), anfang.getYear());
        } else {
            toString = String.format("%s - %s", anfang.format(ddMMuuuu), ende.format(ddMMuuuu));
        }

        return toString;
    }

    private boolean nurJahrBekannt() {
        return anfang != null && anfang.getDayOfMonth() == 1 && anfang.getMonthValue() == 1 && anfang
            .plusYears(1).minusDays(1).isEqual(ende);
    }

    private boolean nurMonatUndJahrBekannt() {
        return anfang != null && anfang.getDayOfMonth() == 1 && anfang.plusMonths(1).minusDays(1)
            .isEqual(ende);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UngewissesDatum)) {
            return false;
        }
        UngewissesDatum that = (UngewissesDatum) o;
        return Objects.equals(anfang, that.anfang) && Objects.equals(ende, that.ende);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anfang, ende);
    }
}