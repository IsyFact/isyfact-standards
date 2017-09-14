package de.bund.bva.isyfact.datetime.util;

import java.time.DateTimeException;
import java.time.LocalDate;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Björn Saxe, msg systems ag
 */
public class DateTimeUtilTest {

    private static final LocalDate DATE_2017_7_1 = LocalDate.of(2017, 7, 1);

    private static final LocalDate DATE_2017_8_1 = LocalDate.of(2017, 8, 1);

    private static final LocalDate DATE_2017_9_1 = LocalDate.of(2017, 9, 1);

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
}