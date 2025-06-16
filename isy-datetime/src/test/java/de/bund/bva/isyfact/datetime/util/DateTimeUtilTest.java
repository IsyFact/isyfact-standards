package de.bund.bva.isyfact.datetime.util;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

import de.bund.bva.isyfact.datetime.test.TestClock;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;


public class DateTimeUtilTest {

    private static final LocalDate DATE_2017_7_1 = LocalDate.of(2017, 7, 1);

    private static final LocalDate DATE_2017_8_1 = LocalDate.of(2017, 8, 1);

    private static final LocalDate DATE_2017_9_1 = LocalDate.of(2017, 9, 1);

    @After
    public void setClock() {
        DateTimeUtil.setClock(Clock.systemDefaultZone());
    }

    @Test
    public void datumLiegtZwischen() throws Exception {
        assertFalse(DateTimeUtil.datumLiegtZwischen(DATE_2017_7_1, DATE_2017_8_1, DATE_2017_9_1));
        assertTrue(DateTimeUtil.datumLiegtZwischen(DATE_2017_8_1, DATE_2017_8_1, DATE_2017_9_1));
        assertTrue(DateTimeUtil.datumLiegtZwischen(DATE_2017_8_1, DATE_2017_7_1, DATE_2017_9_1));
        assertTrue(DateTimeUtil.datumLiegtZwischen(DATE_2017_9_1, DATE_2017_8_1, DATE_2017_9_1));
        assertTrue(DateTimeUtil.datumLiegtZwischen(DATE_2017_7_1, DATE_2017_7_1, DATE_2017_7_1));
        assertFalse(DateTimeUtil.datumLiegtZwischen(DATE_2017_9_1, DATE_2017_7_1, DATE_2017_8_1));
    }

    @Test(expected = DateTimeException.class)
    public void datumLiegtZwischenAnfangNachEnde() throws Exception {
        DateTimeUtil.datumLiegtZwischen(DATE_2017_7_1, DATE_2017_9_1, DATE_2017_8_1);
    }

    @Test
    public void datumLiegtZwischenExklusive() throws Exception {
        assertFalse(DateTimeUtil.datumLiegtZwischenExklusive(DATE_2017_7_1, DATE_2017_8_1, DATE_2017_9_1));
        assertFalse(DateTimeUtil.datumLiegtZwischenExklusive(DATE_2017_8_1, DATE_2017_8_1, DATE_2017_9_1));
        assertTrue(DateTimeUtil.datumLiegtZwischenExklusive(DATE_2017_8_1, DATE_2017_7_1, DATE_2017_9_1));
        assertFalse(DateTimeUtil.datumLiegtZwischenExklusive(DATE_2017_9_1, DATE_2017_8_1, DATE_2017_9_1));
        assertFalse(DateTimeUtil.datumLiegtZwischenExklusive(DATE_2017_7_1, DATE_2017_7_1, DATE_2017_7_1));
        assertFalse(DateTimeUtil.datumLiegtZwischenExklusive(DATE_2017_9_1, DATE_2017_7_1, DATE_2017_8_1));
    }

    @Test(expected = DateTimeException.class)
    public void datumLiegtZwischenExklusiveAnfangNachEnde() throws Exception {
        DateTimeUtil.datumLiegtZwischenExklusive(DATE_2017_7_1, DATE_2017_9_1, DATE_2017_8_1);
    }

    @Test
    public void getJahresanfang() throws Exception {
        assertNull(DateTimeUtil.getJahresanfang(null));
        assertEquals(LocalDate.of(2017, 1, 1), DateTimeUtil.getJahresanfang(DATE_2017_7_1));
    }

    @Test
    public void getMonatsanfang() throws Exception {
        assertNull(DateTimeUtil.getMonatsanfang(null));
        assertEquals(LocalDate.of(2017, 7, 1), DateTimeUtil.getMonatsanfang(DATE_2017_7_1));
    }

    @Test
    public void getMonatsende() throws Exception {
        assertNull(DateTimeUtil.getMonatsende(null));
        assertEquals(LocalDate.of(2017, 7, 31), DateTimeUtil.getMonatsende(DATE_2017_7_1));
        assertEquals(LocalDate.of(2016, 2, 29), DateTimeUtil.getMonatsende(LocalDate.of(2016, 2, 5)));
    }

    @Test
    public void getWerktag() throws Exception {
        LocalDate SONNTAG = LocalDate.of(2017, 8, 6);
        LocalDate MONTAG = SONNTAG.plusDays(1);

        assertEquals(MONTAG, DateTimeUtil.getWerktag(SONNTAG));
        assertEquals(DATE_2017_9_1, DateTimeUtil.getWerktag(DATE_2017_9_1));
    }

    @Test
    public void localTimeNow() throws Exception {
        assertEquals(LocalTime.now().withNano(0), DateTimeUtil.localTimeNow().withNano(0));

        LocalDateTime testDatumZeit = LocalDateTime.now().minusDays(1);
        DateTimeUtil.setClock(TestClock.at(testDatumZeit));

        assertEquals(testDatumZeit.toLocalTime(), DateTimeUtil.localTimeNow());
    }

    @Test
    public void offsetTimeNow() throws Exception {
        assertEquals(OffsetTime.now().withNano(0), DateTimeUtil.offsetTimeNow().withNano(0));

        OffsetDateTime testDatumZeit = OffsetDateTime.now().minusDays(1);
        DateTimeUtil.setClock(TestClock.at(testDatumZeit));

        assertEquals(testDatumZeit.toOffsetTime(), DateTimeUtil.offsetTimeNow());
    }

    @Test
    public void localDateNow() throws Exception {
        assertEquals(LocalDate.now(), DateTimeUtil.localDateNow());

        LocalDateTime testDatumZeit = LocalDateTime.now().minusDays(1);
        DateTimeUtil.setClock(TestClock.at(testDatumZeit));

        assertEquals(testDatumZeit.toLocalDate(), DateTimeUtil.localDateNow());
    }

    @Test
    public void localDateTimeNow() throws Exception {
        assertEquals(LocalDateTime.now().withNano(0), DateTimeUtil.localDateTimeNow().withNano(0));

        LocalDateTime testDatumZeit = LocalDateTime.now().minusDays(1);
        DateTimeUtil.setClock(TestClock.at(testDatumZeit));

        assertEquals(testDatumZeit, DateTimeUtil.localDateTimeNow());
    }

    @Test
    public void offsetDateTimeNow() throws Exception {
        assertEquals(OffsetDateTime.now().withNano(0), DateTimeUtil.offsetDateTimeNow().withNano(0));

        OffsetDateTime testDatumZeit = OffsetDateTime.now().minusDays(1);
        DateTimeUtil.setClock(TestClock.at(testDatumZeit));

        assertEquals(testDatumZeit, DateTimeUtil.offsetDateTimeNow());
    }

    @Test
    public void ZonedDateTimeNow() throws Exception {
        assertEquals(ZonedDateTime.now().withNano(0), DateTimeUtil.zonedDateTimeNow().withNano(0));

        ZonedDateTime testDatumZeit = ZonedDateTime.now().minusDays(1);
        DateTimeUtil.setClock(TestClock.at(testDatumZeit));

        assertEquals(testDatumZeit, DateTimeUtil.zonedDateTimeNow());
    }
}