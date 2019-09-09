package de.bund.bva.isyfact.datetime.test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 *
 * <i>Mutable</i> Implementierung von {@link Clock} für Tests.
 *
 */
public class TestClock extends Clock {

    private Instant instant;

    private ZoneId zoneId;

    private TestClock(Instant instant, ZoneId zoneId) {
        this.instant = instant;
        this.zoneId = zoneId;
    }

    /**
     * Erstellt eine {@link TestClock} mit {@link Instant} und Zeitzone als initialen Wert.
     *
     * @param instant
     *            der zu verwendende {@link Instant}, nicht null
     * @param zoneId
     *            die zu verwendende Zeitzone als {@link ZoneId}, nicht null
     * @return {@link TestClock} initial auf den Zeitpunkt und Zeitzone gesetzt, nicht null
     */
    public static TestClock at(Instant instant, ZoneId zoneId) {
        Objects.requireNonNull(instant);
        Objects.requireNonNull(zoneId);
        return new TestClock(Objects.requireNonNull(instant), Objects.requireNonNull(zoneId));
    }

    /**
     * Erstellt eine {@link TestClock} mit einer {@link LocalDateTime} als initialen Wert.
     * <p>
     * Als Zeitzone wird die Default-Zeitzone verwendet.
     *
     * @param localDateTime
     *            die zu verwendende {@link LocalDateTime}, nicht null
     * @return {@link TestClock} initial auf die Zeit gesetzt, nicht null
     */
    public static TestClock at(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime);
        return at(localDateTime.atZone(ZoneId.systemDefault()));
    }

    /**
     * Erstellt eine {@link TestClock} mit einer {@link LocalDateTime} mit Zeitzone als initialen Wert.
     *
     * @param localDateTime
     *            die zu verwendende {@link LocalDateTime}, nicht null
     * @param zoneId
     *            die zu verwendende Zeitzone als {@link ZoneId}, nicht null
     * @return {@link TestClock} initial auf die Zeit mit der Zeitzone gesetzt, nicht null
     */
    public static TestClock at(LocalDateTime localDateTime, ZoneId zoneId) {
        Objects.requireNonNull(localDateTime);
        Objects.requireNonNull(zoneId);
        return at(localDateTime.atZone(zoneId));
    }

    /**
     * Erstellt eine {@link TestClock} mit einer {@link LocalDateTime} mit Zeitzone als initialen Wert.
     *
     * @param offsetDateTime
     *            die zu verwendende {@link OffsetDateTime}, nicht null
     * @return {@link TestClock} initial auf die Zeit gesetzt, nicht null
     */
    public static TestClock at(OffsetDateTime offsetDateTime) {
        Objects.requireNonNull(offsetDateTime);
        return TestClock.at(offsetDateTime.toZonedDateTime());
    }

    /**
     * Erstellt eine {@link TestClock} mit einer {@link ZonedDateTime} als initialen Wert.
     *
     * @param zonedDateTime
     *            die zu verwendende {@link ZonedDateTime}, nicht null
     * @return {@link TestClock} initial auf die Zeit gesetzt, nicht null
     */
    public static TestClock at(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime);
        return new TestClock(zonedDateTime.toInstant(), zonedDateTime.getZone());
    }

    /**
     * Erstellt eine {@link TestClock} mit der aktuellen Systemzeit und Zeitzone als initialen Wert.
     *
     * @param zoneId
     *            die zu verwendende Zeitzone als {@link ZoneId}, nicht null
     * @return {@link TestClock} initial auf die Systemzeit mit der Zone gesetzt, nicht null
     */
    public static TestClock now(ZoneId zoneId) {
        Objects.requireNonNull(zoneId);
        return new TestClock(Instant.now(), zoneId);
    }

    /**
     * Erstellt eine {@link TestClock} mit der aktuellen Systemzeit und Default-Zeitzone als initialen Wert.
     *
     * @return {@link TestClock} initial auf die Systemzeit mit der Default-Zeitzone gesetzt, nicht null
     */
    public static TestClock now() {
        return new TestClock(Instant.now(), ZoneId.systemDefault());
    }

    /**
     * Verändere die Zeit der {@link TestClock} um die übergebene {@link Duration}.
     *
     * @param duration
     *            {@link Duration}, mit der die Zeit dieser {@link TestClock} geändert werden soll
     * @return diese {@link TestClock}, mit der geänderten Zeit
     */
    public TestClock advanceBy(Duration duration) {
        Objects.requireNonNull(duration);
        instant = ZonedDateTime.ofInstant(instant, zoneId).plus(duration).toInstant();
        return this;
    }

    /**
     * Verändere die Zeit der {@link TestClock} um die übergebene {@link Period}.
     *
     * @param period
     *            {@link Period}, mit der die Zeit dieser {@link TestClock} geändert werden soll
     * @return diese {@link TestClock}, mit der geänderten Zeit
     */
    public TestClock advanceBy(Period period) {
        Objects.requireNonNull(period);
        instant = ZonedDateTime.ofInstant(instant, zoneId).plus(period).toInstant();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Clock withZone(ZoneId zone) {
        return new TestClock(instant, zone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant instant() {
        return instant;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TestClock: " + ZonedDateTime.ofInstant(instant, zoneId).toString();
    }
}
