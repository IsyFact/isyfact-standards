package de.bund.bva.isyfact.datetime.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.Test;


public class UngewissesDatumTest {

    @Test
    public void leer() throws Exception {
        UngewissesDatum leer = UngewissesDatum.leer();

        assertTrue(leer.isLeer());
    }

    @Test
    public void ofJahr() throws Exception {
        UngewissesDatum nurJahr = UngewissesDatum.of(2017);

        assertFalse(nurJahr.isLeer());
        assertEquals(LocalDate.of(2017, 1, 1), nurJahr.getAnfang());
        assertEquals(LocalDate.of(2017, 12, 31), nurJahr.getEnde());
    }

    @Test
    public void ofJahrMonat() throws Exception {
        UngewissesDatum jahrMonat = UngewissesDatum.of(2016, 2);

        assertFalse(jahrMonat.isLeer());
        assertEquals(LocalDate.of(2016, 2, 1), jahrMonat.getAnfang());
        assertEquals(LocalDate.of(2016, 2, 29), jahrMonat.getEnde());
    }

    @Test
    public void ofJahrMonatTag() throws Exception {
        UngewissesDatum jahrMontagTag = UngewissesDatum.of(2017, 8, 1);

        assertFalse(jahrMontagTag.isLeer());
        assertEquals(LocalDate.of(2017, 8, 1), jahrMontagTag.getAnfang());
        assertEquals(LocalDate.of(2017, 8, 1), jahrMontagTag.getEnde());
    }

    @Test
    public void ofLocalDate() throws Exception {
        UngewissesDatum ofLocalDate = UngewissesDatum.of(LocalDate.of(2017, 8, 1), LocalDate.of(2017, 8, 31));

        assertFalse(ofLocalDate.isLeer());
        assertEquals(LocalDate.of(2017, 8, 1), ofLocalDate.getAnfang());
        assertEquals(LocalDate.of(2017, 8, 31), ofLocalDate.getEnde());

        ofLocalDate = UngewissesDatum.of(LocalDate.of(2017, 8, 1), LocalDate.of(2017, 8, 1));

        assertFalse(ofLocalDate.isLeer());
        assertEquals(UngewissesDatum.of(2017, 8, 1), ofLocalDate);
    }

    @Test(expected = NullPointerException.class)
    public void ofLocalDateNullArguments() throws Exception {
        UngewissesDatum.of(null, null);
    }

    @Test(expected = DateTimeException.class)
    public void ofLocalDateAnfageNachEnde() throws Exception {
        UngewissesDatum.of(LocalDate.of(2017, 8, 1), LocalDate.of(2017, 7, 1));
    }

    @Test(expected = DateTimeException.class)
    public void ofLocalDateJahreVerschieden() throws Exception {
        UngewissesDatum.of(LocalDate.of(2017, 1, 1), LocalDate.of(2018, 8, 1));
    }

    @Test(expected = NullPointerException.class)
    public void parseNullArgument() throws Exception {
        UngewissesDatum.parse(null);
    }

    @Test
    public void getJahr() throws Exception {
        UngewissesDatum datum = UngewissesDatum.leer();
        assertEquals(Optional.empty(), datum.getJahr());

        datum = UngewissesDatum.of(2017);
        assertEquals(Optional.of(2017), datum.getJahr());

        datum = UngewissesDatum.of(LocalDate.of(2017, 8, 1), LocalDate.of(2017, 8, 10));
        assertEquals(Optional.of(2017), datum.getJahr());
    }

    @Test
    public void getMonat() throws Exception {
        UngewissesDatum datum = UngewissesDatum.leer();
        assertEquals(Optional.empty(), datum.getMonat());

        datum = UngewissesDatum.of(2017, 8);
        assertEquals(Optional.of(8), datum.getMonat());

        datum = UngewissesDatum.of(LocalDate.of(2017, 8, 1), LocalDate.of(2017, 8, 10));
        assertEquals(Optional.of(8), datum.getMonat());

        datum = UngewissesDatum.of(LocalDate.of(2017, 8, 1), LocalDate.of(2017, 9, 10));
        assertEquals(Optional.empty(), datum.getMonat());
    }

    @Test
    public void getTag() throws Exception {
        UngewissesDatum datum = UngewissesDatum.leer();
        assertEquals(Optional.empty(), datum.getTag());

        datum = UngewissesDatum.of(2017, 8, 1);
        assertEquals(Optional.of(1), datum.getTag());

        datum = UngewissesDatum.of(LocalDate.of(2017, 8, 1), LocalDate.of(2017, 8, 10));
        assertEquals(Optional.empty(), datum.getTag());
    }

    @Test
    public void toLocalDate() throws Exception {
        UngewissesDatum datum = UngewissesDatum.leer();
        assertEquals(Optional.empty(), datum.toLocalDate());

        datum = UngewissesDatum.of(2017, 8, 1);
        assertEquals(Optional.of(LocalDate.of(2017, 8, 1)), datum.toLocalDate());
    }

    @Test
    public void equalsWithEqualObjects() {
        UngewissesDatum date1 = UngewissesDatum.of(2023, 1, 1);
        UngewissesDatum date2 = UngewissesDatum.of(2023, 1, 1);

        assertEquals(date1, date2);
        // Wenn Referenzen identisch sind, wird restliche Logik von equals übersprungen
        assertEquals(date1, date1);
        assertEquals(date1.hashCode(), date2.hashCode());
    }

    @Test
    public void equalsWithDifferentObjects() {
        UngewissesDatum date1 = UngewissesDatum.of(2023, 1, 1);
        UngewissesDatum date2 = UngewissesDatum.of(2023, 1, 2);

        assertNotEquals(date1, date2);
    }

    @Test
    public void equalsWithNull() {
        UngewissesDatum date1 = UngewissesDatum.of(2023, 1, 1);
        assertNotEquals(null, date1);
    }

    @Test
    public void equalsWithDifferentTypes() {
        UngewissesDatum date1 = UngewissesDatum.of(2023, 1, 1);
        String otherObject = "2023-01-01";
        assertNotEquals(date1, otherObject);
    }

    @Test
    public void testHashCodeWithEqualObjects() {
        UngewissesDatum date1 = UngewissesDatum.of(2023, 1, 1);
        UngewissesDatum date2 = UngewissesDatum.of(2023, 1, 1);

        assertEquals(date1.hashCode(), date2.hashCode());
    }

    @Test
    public void testHashCodeWithDifferentObjects() {
        UngewissesDatum date1 = UngewissesDatum.of(2023, 1, 1);
        UngewissesDatum date2 = UngewissesDatum.of(2023, 1, 2);

        assertNotEquals(date1.hashCode(), date2.hashCode());
    }
}
