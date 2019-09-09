package de.bund.bva.isyfact.datetime.util;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Utility-Funktionen für Datums- und Zeitberechnungen und Zeitabfragen.
 *
 */
public abstract class DateTimeUtil {

    private static Clock clock = Clock.systemDefaultZone();

    public static Clock getClock() {
        return clock;
    }

    public static void setClock(Clock clock) {
        Objects.requireNonNull(clock);

        DateTimeUtil.clock = clock;
    }

    /**
     * Prüft, ob ein Datum zwischen zwei anderen Datumswerten liegt.
     *
     * @param datum
     *            Datum das geprüft werden soll
     * @param anfang
     *            Startwert
     * @param ende
     *            Endwert
     * @return true, wenn anfang &lt;= datum &lt;= ende
     * @throws DateTimeException
     *             wenn anfang nach ende liegt
     */
    public static boolean datumLiegtZwischen(LocalDate datum, LocalDate anfang, LocalDate ende) {
        Objects.requireNonNull(datum);
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(ende);

        if (ende.isBefore(anfang)) {
            throw new DateTimeException("Das Enddatum " + ende + " liegt vor dem Anfangsdatum " + anfang);
        }

        return (datum.isEqual(anfang) || datum.isAfter(anfang)) && (datum.isBefore(ende) || datum
            .isEqual(ende));
    }

    /**
     * Prüft, ob ein Datum zwischen zwei anderen Datumswerten liegt, wobei das Anfangs- und das End-Datum
     * außerhalb des Bereiches liegen.
     *
     * @param datum
     *            Datum das geprüft werden soll
     * @param anfang
     *            Startwert
     * @param ende
     *            Endwert
     * @return true, wenn anfang &lt; datum &lt; ende
     * @throws DateTimeException
     *             wenn anfang nach ende liegt
     */
    public static boolean datumLiegtZwischenExklusive(LocalDate datum, LocalDate anfang, LocalDate ende) {
        Objects.requireNonNull(datum);
        Objects.requireNonNull(anfang);
        Objects.requireNonNull(ende);

        if (ende.isBefore(anfang)) {
            throw new DateTimeException("Das Enddatum " + ende + " liegt vor dem Anfangsdatum " + anfang);
        }

        return datum.isAfter(anfang) && datum.isBefore(ende);
    }

    /**
     * Liefert den 1.1. des Jahres zurück, in dem sich das übergebene Datum befindet.
     *
     * @param datum
     *            das Datum als {@link LocalDate}, das in dem Jahr liegt
     * @return der erste Januar des Jahres, in dem das übergebene Datum liegt; alle weiteren Anteile sind 0;
     *         <code>null</code>, wenn das übergebene Datum <code>null</code> ist.
     */
    public static LocalDate getJahresanfang(LocalDate datum) {
        return datum == null ? null : datum.withDayOfYear(1);
    }

    /**
     * Liefert den 1. des Monats zurück, in dem sich das übergebene Datum befindet.
     *
     * @param datum
     *            das Datum als {@link LocalDate}, das in dem Jahr liegt
     * @return der erste Tag des Monats, in dem das übergebene Datum liegt; alle weiteren Zeitanteile sind 0;
     *         <code>null</code>, wenn das übergebene Datum <code>null</code> ist.
     */
    public static LocalDate getMonatsanfang(LocalDate datum) {
        return datum == null ? null : datum.withDayOfMonth(1);
    }

    /**
     * Liefert den letzten Tag des Monats zurück, in dem sich das übergebene Datum befindet.
     *
     * @param datum
     *            das Datum als {@link LocalDate}, das in dem Monat liegt
     * @return der letzte Tag des Monats, in dem das übergebene Datum liegt; alle weiteren Zeitanteile sind 0;
     *         <code>null</code>, wenn das übergebene Datum <code>null</code> ist.
     */
    public static LocalDate getMonatsende(LocalDate datum) {
        return datum == null ? null : datum.withDayOfMonth(datum.getMonth().length(datum.isLeapYear()));
    }

    /**
     * Liefert den nächsten Werktag (Montag), wenn das eingegebene Datum ein Sonntag ist. Ansonsten wird das
     * eingegebene Datum zurückgeliefert.
     *
     * @param datum
     *            Das Datum, das um einen Tag erhöht werden soll, wenn es sich um einen Sonntag handelt.
     * @return Der nächste Montag, falls das eingegebene Datum ein Sonntag ist. Sonst das eingegebene Datum.
     */
    public static LocalDate getWerktag(LocalDate datum) {
        Objects.requireNonNull(datum);

        return datum.getDayOfWeek() == DayOfWeek.SUNDAY ? datum.plusDays(1) : datum;
    }

    public static LocalTime localTimeNow() {
        return LocalTime.now(clock);
    }

    public static LocalDate localDateNow() {
        return LocalDate.now(clock);
    }

    public static LocalDateTime localDateTimeNow() {
        return LocalDateTime.now(clock);
    }

    public static OffsetTime offsetTimeNow() {
        return OffsetTime.now(clock);
    }

    public static OffsetDateTime offsetDateTimeNow() {
        return OffsetDateTime.now(clock);
    }

    public static ZonedDateTime zonedDateTimeNow() {
        return ZonedDateTime.now(clock);
    }
}
