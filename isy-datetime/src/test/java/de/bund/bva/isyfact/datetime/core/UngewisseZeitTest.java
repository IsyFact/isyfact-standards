package de.bund.bva.isyfact.datetime.core;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.*;


public class UngewisseZeitTest {

    @Test
    public void leer() {
        UngewisseZeit leer = UngewisseZeit.leer();

        assertTrue(leer.isLeer());
    }

    @Test
    public void ofStunde() {
        UngewisseZeit nurStunde = UngewisseZeit.of(17);

        assertFalse(nurStunde.isLeer());
        assertEquals(LocalTime.of(17, 0, 0), nurStunde.getAnfang());
        assertEquals(LocalTime.of(17, 59, 59), nurStunde.getEnde());
    }

    @Test(expected = DateTimeException.class)
    public void ofStundeNegativ() {
        UngewisseZeit.of(-1);
    }

    @Test(expected = DateTimeException.class)
    public void ofStundeZuGross() {
        UngewisseZeit.of(24);
    }

    @Test
    public void ofStundeMinute() {
        UngewisseZeit stundeMinute = UngewisseZeit.of(17, 30);

        assertFalse(stundeMinute.isLeer());
        assertEquals(LocalTime.of(17, 30, 0), stundeMinute.getAnfang());
        assertEquals(LocalTime.of(17, 30, 59), stundeMinute.getEnde());
    }

    @Test
    public void ofStundeMinuteSekunde() {
        UngewisseZeit stundeMinuteSekunde = UngewisseZeit.of(17, 30, 30);

        assertFalse(stundeMinuteSekunde.isLeer());
        assertEquals(LocalTime.of(17, 30, 30), stundeMinuteSekunde.getAnfang());
        assertEquals(LocalTime.of(17, 30, 30), stundeMinuteSekunde.getEnde());
    }

    @Test
    public void ofLocalTime() {
        UngewisseZeit ofLocalTime = UngewisseZeit.of(LocalTime.of(17, 30, 30), LocalTime.of(17, 45, 45));

        assertFalse(ofLocalTime.isLeer());
        assertEquals(LocalTime.of(17, 30, 30), ofLocalTime.getAnfang());
        assertEquals(LocalTime.of(17, 45, 45), ofLocalTime.getEnde());

        ofLocalTime = UngewisseZeit.of(LocalTime.of(17, 30, 0), LocalTime.of(17, 30, 0));

        assertFalse(ofLocalTime.isLeer());
        assertEquals(UngewisseZeit.of(17, 30, 0), ofLocalTime);
    }

    @Test(expected = NullPointerException.class)
    public void ofLocalDateNullArguments() {
        UngewisseZeit.of(null, null);
    }

    @Test(expected = DateTimeException.class)
    public void ofLocalDateAnfageNachEnde() {
        UngewisseZeit.of(LocalTime.of(17, 45), LocalTime.of(17, 30));
    }

    @Test(expected = NullPointerException.class)
    public void parseNullArgument() {
        UngewisseZeit.parse(null);
    }

    @Test
    public void getStunde() {
        UngewisseZeit zeit = UngewisseZeit.leer();
        assertEquals(Optional.empty(), zeit.getStunde());

        zeit = UngewisseZeit.of(17);
        assertEquals(Optional.of(17), zeit.getStunde());

        zeit = UngewisseZeit.of(LocalTime.of(15, 0), LocalTime.of(17, 0));
        assertEquals(Optional.empty(), zeit.getStunde());
    }

    @Test
    public void getMinute() {
        UngewisseZeit zeit = UngewisseZeit.leer();
        assertEquals(Optional.empty(), zeit.getMinute());

        zeit = UngewisseZeit.of(17, 30);
        assertEquals(Optional.of(30), zeit.getMinute());

        zeit = UngewisseZeit.of(LocalTime.of(15, 30), LocalTime.of(17, 31));
        assertEquals(Optional.empty(), zeit.getMinute());
    }

    @Test
    public void getSekunde() {
        UngewisseZeit zeit = UngewisseZeit.leer();
        assertEquals(Optional.empty(), zeit.getSekunde());

        zeit = UngewisseZeit.of(17, 30, 45);
        assertEquals(Optional.of(45), zeit.getSekunde());

        zeit = UngewisseZeit.of(LocalTime.of(15, 0, 20), LocalTime.of(17, 0, 30));
        assertEquals(Optional.empty(), zeit.getSekunde());
    }

    @Test
    public void toLocalTime() {
        UngewisseZeit zeit = UngewisseZeit.leer();
        assertEquals(Optional.empty(), zeit.toLocalTime());

        zeit = UngewisseZeit.of(17, 30, 45);
        assertEquals(Optional.of(LocalTime.of(17, 30, 45)), zeit.toLocalTime());

        zeit = UngewisseZeit.of(LocalTime.of(15, 0), LocalTime.of(17, 0));
        assertEquals(Optional.empty(), zeit.toLocalTime());
    }
}
