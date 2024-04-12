package de.bund.bva.isyfact.datetime.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import de.bund.bva.isyfact.datetime.format.OutFormat;


public class ZeitraumTest {

    private static final LocalDate localDate = LocalDate.of(2017, 8, 1);

    private static final LocalTime localTime = LocalTime.of(15, 0);

    private Zeitraum zeitraum1 = Zeitraum.of(ZonedDateTime.parse("2023-01-01T08:00:00Z"), ZonedDateTime.parse("2023-01-01T12:00:00Z"));

    private Zeitraum zeitraum2 = Zeitraum.of(ZonedDateTime.parse("2023-01-01T08:00:00Z"), ZonedDateTime.parse("2023-01-01T12:00:00Z"));

    private static final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

    private static final ZonedDateTime zonedDateTime =
        ZonedDateTime.of(localDateTime, ZoneId.systemDefault());

    @Test
    public void ofZonedDateTime() throws Exception {
        ZonedDateTime anfang = zonedDateTime;
        ZonedDateTime ende = zonedDateTime.plusDays(2);

        Zeitraum zeitraum = Zeitraum.of(anfang, ende);

        assertEquals(anfang, zeitraum.getAnfangsdatumzeit());
        assertEquals(ende, zeitraum.getEndedatumzeit());
    }

    @Test
    public void ofZonedDateTimeDuration() throws Exception {
        Duration duration = Duration.ofHours(12);

        Zeitraum zeitraum = Zeitraum.of(zonedDateTime, duration);

        assertEquals(zonedDateTime, zeitraum.getAnfangsdatumzeit());
        assertEquals(zonedDateTime.plus(duration), zeitraum.getEndedatumzeit());
    }

    @Test
    public void ofZonedDateTimePeriod() throws Exception {
        Period period = Period.of(1, 1, 1);

        Zeitraum zeitraum = Zeitraum.of(zonedDateTime, period);

        assertEquals(zonedDateTime, zeitraum.getAnfangsdatumzeit());
        assertEquals(zonedDateTime.plus(period), zeitraum.getEndedatumzeit());
    }

    @Test(expected = NullPointerException.class)
    public void ofZonedDateTimeNullArguments() throws Exception {
        Zeitraum.of(null, (ZonedDateTime) null);
    }

    @Test(expected = NullPointerException.class)
    public void ofZonedDateTimeDurationNullArguments() throws Exception {
        Zeitraum.of((ZonedDateTime) null, (Duration) null);
    }

    @Test(expected = NullPointerException.class)
    public void ofZonedDateTimePeriodNullArguments() throws Exception {
        Zeitraum.of((ZonedDateTime) null, (Period) null);
    }

    @Test(expected = NullPointerException.class)
    public void ofLocalDateTimeNullArguments() throws Exception {
        Zeitraum.of(null, (LocalDateTime) null);
    }

    @Test(expected = NullPointerException.class)
    public void ofLocalDateTimeDurationNullArguments() throws Exception {
        Zeitraum.of((LocalDateTime) null, (Duration) null);
    }

    @Test(expected = NullPointerException.class)
    public void ofLocalDateTimePeriodNullArguments() throws Exception {
        Zeitraum.of((LocalDateTime) null, (Period) null);
    }

    @Test(expected = NullPointerException.class)
    public void ofLocalDateNullArguments() throws Exception {
        Zeitraum.of(null, (LocalDate) null);
    }

    @Test(expected = NullPointerException.class)
    public void ofLocalDatePeriodNullArguments() throws Exception {
        Zeitraum.of((LocalDate) null, (Period) null);
    }

    @Test(expected = DateTimeException.class)
    public void ofZonedDateTimeAnfangNachEnde() {
        Zeitraum.of(zonedDateTime, zonedDateTime.minusDays(1));
    }

    @Test(expected = DateTimeException.class)
    public void ofZonedDateTimeDurationNegative() {
        Zeitraum.of(zonedDateTime, Duration.ofDays(-1));
    }

    @Test(expected = DateTimeException.class)
    public void ofZonedDateTimePeriodNegativ() {
        Zeitraum.of(zonedDateTime, Period.ofDays(-1));
    }

    @Test(expected = DateTimeException.class)
    public void ofLocalDateTimeAnfangNachEnde() {
        Zeitraum.of(localDateTime, localDateTime.minusDays(1));
    }

    @Test(expected = DateTimeException.class)
    public void ofLocalDateTimeDurationNegativ() {
        Zeitraum.of(localDateTime, Duration.ofDays(-1));
    }

    @Test(expected = DateTimeException.class)
    public void ofLocalDateTimePeriodNegativ() {
        Zeitraum.of(localDateTime, Period.ofDays(-1));
    }

    @Test(expected = DateTimeException.class)
    public void ofLocalDateAnfangNachEnde() {
        Zeitraum.of(localDate, localDate.minusDays(1));
    }

    @Test(expected = DateTimeException.class)
    public void ofLocalDatePeriodNegativ() {
        Zeitraum.of(localDate, Period.ofDays(-1));
    }

    @Test(expected = DateTimeException.class)
    public void ofLocalTimeDurationNegativ() {
        Zeitraum.of(localTime, Duration.ofHours(-1));
    }

    @Test
    public void ofLocalDateTime() throws Exception {
        Zeitraum zeitraum = Zeitraum.of(localDateTime, localDateTime.plusHours(10));

        ZonedDateTime anfangExpected = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        ZonedDateTime endeExpected = ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).plusHours(10);

        assertEquals(anfangExpected, zeitraum.getAnfangsdatumzeit());
        assertEquals(endeExpected, zeitraum.getEndedatumzeit());
    }

    @Test
    public void ofLocalDateTimeDuration() throws Exception {
        LocalDateTime anfang = localDateTime;
        Duration duration = Duration.ofHours(12);

        ZonedDateTime anfangExpected = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        ZonedDateTime endeExpected = ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).plus(duration);

        Zeitraum zeitraum = Zeitraum.of(anfang, duration);

        assertEquals(anfangExpected, zeitraum.getAnfangsdatumzeit());
        assertEquals(endeExpected, zeitraum.getEndedatumzeit());
    }

    @Test
    public void ofLocalDateTimePeriod() throws Exception {
        LocalDateTime anfang = localDateTime;
        Period period = Period.of(1, 1, 1);

        ZonedDateTime anfangExpected = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        ZonedDateTime endeExpected = ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).plus(period);

        Zeitraum zeitraum = Zeitraum.of(anfang, period);

        assertEquals(anfangExpected, zeitraum.getAnfangsdatumzeit());
        assertEquals(endeExpected, zeitraum.getEndedatumzeit());
    }

    @Test
    public void ofLocalDate() throws Exception {
        LocalDate ende = localDate.plusDays(7);

        Zeitraum zeitraum = Zeitraum.of(localDate, ende);

        assertEquals(ZonedDateTime.of(localDate, LocalTime.of(0, 0), ZoneId.systemDefault()),
            zeitraum.getAnfangsdatumzeit());
        assertEquals(ZonedDateTime.of(ende, LocalTime.of(0, 0), ZoneId.systemDefault()),
            zeitraum.getEndedatumzeit());
        assertEquals(7, zeitraum.dauer(ChronoUnit.DAYS));
    }

    @Test
    public void ofLocalDatePeriod() throws Exception {
        LocalDate anfang = localDate;
        Period period = Period.of(1, 1, 1);

        Zeitraum zeitraum = Zeitraum.of(anfang, period);

        assertEquals(ZonedDateTime.of(localDate, LocalTime.of(0, 0), ZoneId.systemDefault()),
            zeitraum.getAnfangsdatumzeit());
        assertEquals(ZonedDateTime.of(localDate, LocalTime.of(0, 0), ZoneId.systemDefault()).plus(period),
            zeitraum.getEndedatumzeit());
    }

    @Test
    public void ofLocalTime() throws Exception {
        Zeitraum zeitraum = Zeitraum.of(localTime, localTime.plusMinutes(90));

        assertEquals(90, zeitraum.dauer(ChronoUnit.MINUTES));
        assertEquals(localTime, zeitraum.getAnfangszeit());
        assertEquals(localTime.plusMinutes(90), zeitraum.getEndzeit());
        assertNull(zeitraum.getAnfangsdatumzeit());
        assertNull(zeitraum.getEndedatumzeit());
        assertTrue(zeitraum.isOhneDatum());
    }

    @Test
    public void ofLocalTimeAnfangNachEnde() throws Exception {
        Zeitraum zeitraum = Zeitraum.of(localTime, localTime.minusHours(10));

        assertEquals(14, zeitraum.dauer(ChronoUnit.HOURS));
        assertEquals(localTime, zeitraum.getAnfangszeit());
        assertEquals(localTime.minusHours(10), zeitraum.getEndzeit());
        assertNull(zeitraum.getAnfangsdatumzeit());
        assertNull(zeitraum.getEndedatumzeit());
        assertTrue(zeitraum.isOhneDatum());
    }

    @Test
    public void ofLocalTimeDuration() throws Exception {
        Zeitraum zeitraum = Zeitraum.of(localTime, Duration.ofHours(2));

        assertEquals(2, zeitraum.dauer(ChronoUnit.HOURS));
        assertEquals(localTime, zeitraum.getAnfangszeit());
        assertEquals(localTime.plusHours(2), zeitraum.getEndzeit());
        assertNull(zeitraum.getAnfangsdatumzeit());
        assertNull(zeitraum.getEndedatumzeit());
        assertTrue(zeitraum.isOhneDatum());
    }

    @Test(expected = DateTimeException.class)
    public void ofLocalTimeDurationLaenger24Stunden() {
        Zeitraum.of(localTime, Duration.ofHours(25));
    }

    @Test
    public void dauer() {
        Zeitraum zeitraum = Zeitraum.of(zonedDateTime, zonedDateTime.plusDays(1));

        assertEquals(1, zeitraum.dauer(ChronoUnit.DAYS));

        zeitraum = Zeitraum.of(zonedDateTime, zonedDateTime);

        assertEquals(0, zeitraum.dauer(ChronoUnit.SECONDS));
    }

    @Test
    public void umstellungSommerzeit() throws Exception {
        ZoneId berlin = ZoneId.of("Europe/Berlin");

        ZonedDateTime anfangInNormalzeit = ZonedDateTime.of(2017, 3, 25, 18, 0, 0, 0, berlin);
        ZonedDateTime endeInSommerzeit = ZonedDateTime.of(2017, 3, 26, 18, 0, 0, 0, berlin);

        Zeitraum zeitraum = Zeitraum.of(anfangInNormalzeit, endeInSommerzeit);

        assertEquals(1, zeitraum.dauer(ChronoUnit.DAYS));
        assertEquals(23, zeitraum.dauer(ChronoUnit.HOURS));
    }

    @Test
    public void umstellungNormalzeit() throws Exception {
        ZoneId berlin = ZoneId.of("Europe/Berlin");

        ZonedDateTime anfangInSommerzeit = ZonedDateTime.of(2017, 10, 28, 18, 0, 0, 0, berlin);
        ZonedDateTime endeInNormalzeit = ZonedDateTime.of(2017, 10, 29, 18, 0, 0, 0, berlin);

        Zeitraum zeitraum = Zeitraum.of(anfangInSommerzeit, endeInNormalzeit);

        assertEquals(1, zeitraum.dauer(ChronoUnit.DAYS));
        assertEquals(25, zeitraum.dauer(ChronoUnit.HOURS));
    }

    @Test
    public void isInZeitraumZonedDateTime() {
        ZonedDateTime anfang = zonedDateTime;
        ZonedDateTime ende = zonedDateTime.plusDays(5);
        ZonedDateTime davor = zonedDateTime.minusDays(1);
        ZonedDateTime innerhalb = zonedDateTime.plusDays(1);
        ZonedDateTime danach = zonedDateTime.plusDays(10);

        Zeitraum zeitraum = Zeitraum.of(anfang, ende);

        assertFalse(zeitraum.isInZeitraum(davor));
        assertTrue(zeitraum.isInZeitraum(anfang));
        assertTrue(zeitraum.isInZeitraum(innerhalb));
        assertFalse(zeitraum.isInZeitraum(ende));
        assertFalse(zeitraum.isInZeitraum(danach));
    }

    @Test
    public void isInZeitraumLocalDateTime() {
        LocalDateTime anfang = localDateTime;
        LocalDateTime ende = anfang.plusDays(5);
        LocalDateTime davor = anfang.minusDays(1);
        LocalDateTime innerhalb = anfang.plusDays(1);
        LocalDateTime danach = ende.plusDays(1);

        Zeitraum zeitraum = Zeitraum.of(anfang, ende);

        assertFalse(zeitraum.isInZeitraum(davor));
        assertTrue(zeitraum.isInZeitraum(anfang));
        assertTrue(zeitraum.isInZeitraum(innerhalb));
        assertFalse(zeitraum.isInZeitraum(ende));
        assertFalse(zeitraum.isInZeitraum(danach));
    }

    @Test
    public void isInZeitraumLocalDate() {
        ZonedDateTime anfang = zonedDateTime;
        ZonedDateTime ende = anfang.plusDays(5);
        LocalDate davor = localDate.minusDays(1);
        LocalDate ueberschneidungMitAnfang = anfang.toLocalDate();
        LocalDate innerhalb = localDate.plusDays(2);
        LocalDate ueberschneidungMitEnde = ende.plusDays(1).toLocalDate();
        LocalDate danach = localDate.plusDays(10);

        Zeitraum zeitraum = Zeitraum.of(anfang, ende);

        assertFalse(zeitraum.isInZeitraum(davor));
        // False, weil der Tag nicht komplett im Zeitraum liegt
        assertFalse(zeitraum.isInZeitraum(ueberschneidungMitAnfang));
        assertTrue(zeitraum.isInZeitraum(innerhalb));
        assertFalse(zeitraum.isInZeitraum(ueberschneidungMitEnde));
        assertFalse(zeitraum.isInZeitraum(danach));
    }

    @Test
    public void isInZeitraumLocalTime() {
        LocalTime anfang = localTime;
        LocalTime ende = anfang.plusMinutes(90);
        LocalTime davor = anfang.minusMinutes(1);
        LocalTime innerhalb = anfang.plusMinutes(10);
        LocalTime danach = ende.plusMinutes(10);

        Zeitraum zeitraum = Zeitraum.of(anfang, ende);

        assertFalse(zeitraum.isInZeitraum(davor));
        assertTrue(zeitraum.isInZeitraum(anfang));
        assertTrue(zeitraum.isInZeitraum(innerhalb));
        assertFalse(zeitraum.isInZeitraum(ende));
        assertFalse(zeitraum.isInZeitraum(danach));
    }

    @Test
    public void isInZeitraumZeitraumMitUndOhneDatumGemischt() {
        Zeitraum zeitaumOhneDatum = Zeitraum.of(localTime, localTime.plusMinutes(30));
        Zeitraum zeitraumMitDatum = Zeitraum.of(localDateTime, localDateTime.plusHours(1));

        assertTrue(zeitraumMitDatum.isInZeitraum(localTime.plusMinutes(10)));
        assertFalse(zeitaumOhneDatum.isInZeitraum(zonedDateTime));
        assertFalse(zeitaumOhneDatum.isInZeitraum(localDate));
        assertFalse(zeitaumOhneDatum.isInZeitraum(localDateTime));
    }

    @Test
    public void ueberschneidetSichMitDatumZeit() {
        ZonedDateTime anfang = zonedDateTime;
        ZonedDateTime ende = zonedDateTime.plusDays(5);

        Zeitraum zeitraum = Zeitraum.of(anfang, ende);

        Zeitraum davor = Zeitraum.of(anfang.minusDays(10), anfang.minusDays(5));
        Zeitraum endeGleichAnfang = Zeitraum.of(anfang.minusDays(10), anfang);
        Zeitraum ueberschneidungAnfang = Zeitraum.of(anfang.minusDays(1), anfang.plusDays(1));
        Zeitraum anfangGleich = Zeitraum.of(anfang, anfang.plusDays(1));
        Zeitraum innerhalb = Zeitraum.of(anfang.plusDays(1), ende.minusDays(1));
        Zeitraum ueberschneidungEnde = Zeitraum.of(ende.minusDays(1), ende.plusDays(1));
        Zeitraum endeGleich = Zeitraum.of(anfang.plusDays(1), ende);
        Zeitraum anfangGleichEnde = Zeitraum.of(ende, ende.plusDays(5));
        Zeitraum danach = Zeitraum.of(ende.plusDays(1), ende.plusDays(5));
        Zeitraum umschlossen = Zeitraum.of(anfang.minusDays(1), ende.plusDays(1));

        assertFalse(zeitraum.ueberschneidetSichMit(null));
        assertFalse(zeitraum.ueberschneidetSichMit(davor));
        assertFalse(zeitraum.ueberschneidetSichMit(endeGleichAnfang));
        assertTrue(zeitraum.ueberschneidetSichMit(ueberschneidungAnfang));
        assertTrue(zeitraum.ueberschneidetSichMit(anfangGleich));
        assertTrue(zeitraum.ueberschneidetSichMit(innerhalb));
        assertTrue(zeitraum.ueberschneidetSichMit(ueberschneidungEnde));
        assertTrue(zeitraum.ueberschneidetSichMit(endeGleich));
        assertFalse(zeitraum.ueberschneidetSichMit(anfangGleichEnde));
        assertFalse(zeitraum.ueberschneidetSichMit(danach));
        assertTrue(zeitraum.ueberschneidetSichMit(umschlossen));
        assertTrue(zeitraum.ueberschneidetSichMit(zeitraum));
    }

    @Test
    public void ueberschneidetSichMitZeit() {
        LocalTime anfang = localTime;
        LocalTime ende = localTime.plusMinutes(90);

        Zeitraum zeitraum = Zeitraum.of(anfang, ende);

        Zeitraum davor = Zeitraum.of(anfang.minusMinutes(10), anfang.minusMinutes(5));
        Zeitraum endeGleichAnfang = Zeitraum.of(anfang.minusMinutes(10), anfang);
        Zeitraum ueberschneidungAnfang = Zeitraum.of(anfang.minusMinutes(1), anfang.plusMinutes(1));
        Zeitraum anfangGleich = Zeitraum.of(anfang, anfang.plusMinutes(1));
        Zeitraum innerhalb = Zeitraum.of(anfang.plusMinutes(1), ende.minusMinutes(1));
        Zeitraum ueberschneidungEnde = Zeitraum.of(ende.minusMinutes(1), ende.plusMinutes(1));
        Zeitraum endeGleich = Zeitraum.of(anfang.plusMinutes(1), ende);
        Zeitraum anfangGleichEnde = Zeitraum.of(ende, ende.plusMinutes(5));
        Zeitraum danach = Zeitraum.of(ende.plusMinutes(1), ende.plusMinutes(5));
        Zeitraum umschlossen = Zeitraum.of(anfang.minusMinutes(1), ende.plusMinutes(1));

        assertFalse(zeitraum.ueberschneidetSichMit(null));
        assertFalse(zeitraum.ueberschneidetSichMit(davor));
        assertFalse(zeitraum.ueberschneidetSichMit(endeGleichAnfang));
        assertTrue(zeitraum.ueberschneidetSichMit(ueberschneidungAnfang));
        assertTrue(zeitraum.ueberschneidetSichMit(anfangGleich));
        assertTrue(zeitraum.ueberschneidetSichMit(innerhalb));
        assertTrue(zeitraum.ueberschneidetSichMit(ueberschneidungEnde));
        assertTrue(zeitraum.ueberschneidetSichMit(endeGleich));
        assertFalse(zeitraum.ueberschneidetSichMit(anfangGleichEnde));
        assertFalse(zeitraum.ueberschneidetSichMit(danach));
        assertTrue(zeitraum.ueberschneidetSichMit(umschlossen));
        assertTrue(zeitraum.ueberschneidetSichMit(zeitraum));
    }

    @Test
    public void ueberschneidetSichMitDatumZeitGemischt() {
        ZonedDateTime anfangDatumZeit = zonedDateTime;
        ZonedDateTime endeDatumZeit = zonedDateTime.plusDays(5);

        Zeitraum zeitraum1 = Zeitraum.of(anfangDatumZeit, endeDatumZeit);

        LocalTime anfangZeit = localTime;
        LocalTime endeZeit = localTime.plusMinutes(90);

        Zeitraum zeitraum2 = Zeitraum.of(anfangZeit, endeZeit);

        assertFalse(zeitraum1.ueberschneidetSichMit(zeitraum2));
        assertFalse(zeitraum2.ueberschneidetSichMit(zeitraum1));

        zeitraum1 = Zeitraum.of(ZonedDateTime.now(),
            Duration.ofSeconds(ZonedDateTime.now().getOffset().getTotalSeconds()).plusHours(1));
        zeitraum2 = Zeitraum.of(ZonedDateTime.now().toLocalTime(), Duration.ofHours(23));

        assertFalse(zeitraum1.ueberschneidetSichMit(zeitraum2));
        assertFalse(zeitraum2.ueberschneidetSichMit(zeitraum1));
    }

    @Test
    public void toStringTest() {
        Zeitraum zeitraumMitDatum = Zeitraum.of(zonedDateTime, zonedDateTime.plusDays(3));

        String expected = OutFormat.DATUM_ZEIT_ZONE.format(zeitraumMitDatum.getAnfangsdatumzeit()) + " - "
            + OutFormat.DATUM_ZEIT_ZONE.format(zeitraumMitDatum.getEndedatumzeit());

        assertEquals(expected, zeitraumMitDatum.toString());

        Zeitraum zeitraumOhneDatum = Zeitraum.of(localTime.plusSeconds(10), localTime.plusMinutes(90));

        expected = OutFormat.ZEIT.format(zeitraumOhneDatum.getAnfangszeit()) + " - " + OutFormat.ZEIT
            .format(zeitraumOhneDatum.getEndzeit());

        assertEquals(expected, zeitraumOhneDatum.toString());

        Zeitraum ohneDatum0Sekunden = Zeitraum.of(LocalTime.of(10, 0), LocalTime.of(11, 30));

        expected =
            OutFormat.ZEIT_KURZ.format(ohneDatum0Sekunden.getAnfangszeit()) + " - " + OutFormat.ZEIT_KURZ
                .format(ohneDatum0Sekunden.getEndzeit());

        assertEquals(expected, ohneDatum0Sekunden.toString());
    }

    @Test
    public void testEqualsWithEqualObjects() {
        assertEquals(zeitraum1, zeitraum2);
        // Wenn Referenzen identisch sind, wird restliche Logik von equals übersprungen
        assertEquals(zeitraum1, zeitraum1);
        assertEquals(zeitraum1.hashCode(), zeitraum2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentObjects() {
        Zeitraum differentZeitraum = Zeitraum.of(ZonedDateTime.parse("2023-01-01T13:00:00Z"), ZonedDateTime.parse("2023-01-01T14:00:00Z"));
        assertNotEquals(zeitraum1, differentZeitraum);
    }

    @Test
    public void testEqualsWithNull() {
        assertNotEquals(zeitraum1, null);
    }
}