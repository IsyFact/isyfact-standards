package de.bund.bva.isyfact.persistence.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.LocalTime;

import org.junit.Test;

import de.bund.bva.isyfact.datetime.core.UngewisseZeit;

public class UngewisseZeitEntitaetTest {
    private static LocalTime ANFANG = LocalTime.of(12, 0);
    private static LocalTime ENDE = LocalTime.of(18, 30);
    @Test
    public void toUngewisseZeit() {
        UngewisseZeitEntitaet entitaet = new UngewisseZeitEntitaet();
        entitaet.setAnfang(ANFANG);
        entitaet.setEnde(ENDE);

        UngewisseZeit ungewisseZeit = entitaet.toUngewisseZeit();

        assertEquals(ANFANG, ungewisseZeit.getAnfang());
        assertEquals(ENDE, ungewisseZeit.getEnde());
    }

    @Test
    public void testEqualsAndHashCodeWithEqualObjects() {
        UngewisseZeitEntitaet entity1 = new UngewisseZeitEntitaet();
        entity1.setAnfang(ANFANG);
        entity1.setEnde(ENDE);

        UngewisseZeitEntitaet entity2 = new UngewisseZeitEntitaet();
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
        LocalTime anfang1 = LocalTime.of(12, 0);
        LocalTime ende1 = LocalTime.of(15, 0);

        LocalTime anfang2 = LocalTime.of(13, 0);
        LocalTime ende2 = LocalTime.of(16, 0);

        UngewisseZeitEntitaet entity1 = new UngewisseZeitEntitaet();
        entity1.setAnfang(anfang1);
        entity1.setEnde(ende1);

        UngewisseZeitEntitaet entity2 = new UngewisseZeitEntitaet();
        entity2.setAnfang(anfang2);
        entity2.setEnde(ende2);

        assertNotEquals(entity1, entity2);
    }

    @Test
    public void testHashCodeWithDifferentObjects() {
        LocalTime anfang2 = LocalTime.of(13, 0);
        LocalTime ende2 = LocalTime.of(16, 0);

        UngewisseZeitEntitaet entity1 = new UngewisseZeitEntitaet();
        entity1.setAnfang(ANFANG);
        entity1.setEnde(ENDE);

        UngewisseZeitEntitaet entity2 = new UngewisseZeitEntitaet();
        entity2.setAnfang(anfang2);
        entity2.setEnde(ende2);

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }
}