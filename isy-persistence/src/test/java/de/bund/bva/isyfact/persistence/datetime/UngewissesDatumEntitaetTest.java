package de.bund.bva.isyfact.persistence.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.LocalDate;

import org.junit.Test;

import de.bund.bva.isyfact.datetime.core.UngewissesDatum;

public class UngewissesDatumEntitaetTest {

    private static LocalDate ANFANG = LocalDate.of(2017, 1, 1);
    private static LocalDate ENDE = LocalDate.of(2017, 1, 10);
    @Test
    public void toUngewissesDatum() {
        UngewissesDatumEntitaet entitaet = new UngewissesDatumEntitaet();

        entitaet.setAnfang(ANFANG);
        entitaet.setEnde(ENDE);

        UngewissesDatum ungewissesDatum = entitaet.toUngewissesDatum();

        assertEquals(ANFANG, ungewissesDatum.getAnfang());
        assertEquals(ENDE, ungewissesDatum.getEnde());
    }

    @Test
    public void testEqualsAndHashCodeWithEqualObjects() {
        UngewissesDatumEntitaet entity1 = new UngewissesDatumEntitaet();
        entity1.setAnfang(ANFANG);
        entity1.setEnde(ENDE);

        UngewissesDatumEntitaet entity2 = new UngewissesDatumEntitaet();
        entity2.setAnfang(ANFANG);
        entity2.setEnde(ENDE);

        assertEquals(entity1, entity2);
        // Wenn Referenzen identisch sind, wird restliche Logik von equals übersprungen
        assertEquals(entity1, entity1);

        assertNotEquals(entity1, null);
        assertNotEquals(entity1, "test");

        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentObjects() {
        LocalDate anfang2 = LocalDate.of(2017, 1, 5);
        LocalDate ende2 = LocalDate.of(2017, 1, 15);

        UngewissesDatumEntitaet entity1 = new UngewissesDatumEntitaet();
        entity1.setAnfang(ANFANG);
        entity1.setEnde(ENDE);

        UngewissesDatumEntitaet entity2 = new UngewissesDatumEntitaet();
        entity2.setAnfang(anfang2);
        entity2.setEnde(ende2);

        // Test der equals-Methode
        assertNotEquals(entity1, entity2);
    }

    @Test
    public void testHashCodeWithDifferentObjects() {
        LocalDate anfang2 = LocalDate.of(2017, 1, 5);
        LocalDate ende2 = LocalDate.of(2017, 1, 15);

        UngewissesDatumEntitaet entity1 = new UngewissesDatumEntitaet();
        entity1.setAnfang(ANFANG);
        entity1.setEnde(ENDE);

        UngewissesDatumEntitaet entity2 = new UngewissesDatumEntitaet();
        entity2.setAnfang(anfang2);
        entity2.setEnde(ende2);

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }
}