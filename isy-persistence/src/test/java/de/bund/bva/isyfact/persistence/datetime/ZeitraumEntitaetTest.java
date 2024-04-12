package de.bund.bva.isyfact.persistence.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import de.bund.bva.isyfact.datetime.core.Zeitraum;

public class ZeitraumEntitaetTest {
    private static ZonedDateTime ZONEDDATETIME = ZonedDateTime.of(
            LocalDateTime.of(2017, 8, 1, 15, 0), ZoneId.systemDefault()
    );

    private static ZonedDateTime ANFANG = ZONEDDATETIME;
    private static ZonedDateTime ENDE = ZONEDDATETIME.plusHours(12);

    @Test
    public void toZeitraum() {
        ZeitraumEntitaet entitaet = new ZeitraumEntitaet();
        entitaet.setAnfang(ANFANG);
        entitaet.setEnde(ENDE);
        entitaet.setOhneDatum(false);

        Zeitraum zeitraum = entitaet.toZeitraum();

        assertEquals(ANFANG, zeitraum.getAnfangsdatumzeit());
        assertEquals(ENDE, zeitraum.getEndedatumzeit());
        assertFalse(entitaet.isOhneDatum());

        entitaet.setOhneDatum(true);

        zeitraum = entitaet.toZeitraum();

        assertNull(zeitraum.getAnfangsdatumzeit());
        assertNull(zeitraum.getEndedatumzeit());
        assertEquals(ANFANG.toLocalTime(), zeitraum.getAnfangszeit());
        assertEquals(ENDE.toLocalTime(), zeitraum.getEndzeit());
        assertTrue(zeitraum.isOhneDatum());
    }

    @Test
    public void testEqualsAndHashCodeWithEqualObjects() {
        ZeitraumEntitaet entity1 = new ZeitraumEntitaet(ANFANG, ENDE, true);
        entity1.setId(1L);
        assertEquals(1L, entity1.getId());

        ZeitraumEntitaet entity2 = new ZeitraumEntitaet(ANFANG, ENDE, true);
        entity2.setId(1L);

        assertEquals(entity1, entity2);
        // Wenn Referenzen identisch sind, wird restliche Logik von equals übersprungen
        assertEquals(entity1, entity1);

        assertNotEquals(entity1, null);
        assertNotEquals(entity1, "test");

        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentObjects() {
        ZonedDateTime anfang1 = ZonedDateTime.parse("2023-11-03T12:00:00Z");
        ZonedDateTime ende1 = ZonedDateTime.parse("2023-11-03T15:00:00Z");

        ZonedDateTime anfang2 = ZonedDateTime.parse("2023-11-03T13:00:00Z");
        ZonedDateTime ende2 = ZonedDateTime.parse("2023-11-03T16:00:00Z");

        ZeitraumEntitaet entity1 = new ZeitraumEntitaet(anfang1, ende1, false);
        entity1.setId(1L);

        ZeitraumEntitaet entity2 = new ZeitraumEntitaet(anfang2, ende2, false);
        entity2.setId(2L);

        // Test der equals-Methode
        assertNotEquals(entity1, entity2);
    }

    @Test
    public void testHashCodeWithDifferentObjects() {
        ZonedDateTime anfang2 = ZonedDateTime.parse("2023-11-03T13:00:00Z");
        ZonedDateTime ende2 = ZonedDateTime.parse("2023-11-03T16:00:00Z");

        ZeitraumEntitaet entity1 = new ZeitraumEntitaet(ANFANG, ENDE, true);
        entity1.setId(1L);

        ZeitraumEntitaet entity2 = new ZeitraumEntitaet(anfang2, ende2, true);
        entity2.setId(2L);

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    public void testEqualsZeitraum() {
        ZeitraumEntitaet entity = new ZeitraumEntitaet(ANFANG, ENDE, false);

        Zeitraum zeitraum1 = Zeitraum.of(ANFANG, ENDE);
        Zeitraum zeitraum2 = Zeitraum.of(
                ZonedDateTime.parse("2023-11-03T13:00:00Z"),
                ZonedDateTime.parse("2023-11-03T16:00:00Z")
        );

        assertTrue(entity.equalsZeitraum(zeitraum1));
        assertFalse(entity.equalsZeitraum(zeitraum2));
    }

    @Test
    public void testEqualsZeitraumWithEqualZeitraum() {
        boolean ohneDatum = false;

        ZeitraumEntitaet entity = new ZeitraumEntitaet(ANFANG, ENDE, ohneDatum);

        Zeitraum zeitraum = Zeitraum.of(ANFANG, ENDE);

        // Test der equalsZeitraum-Methode
        assertTrue(entity.equalsZeitraum(zeitraum));
    }

    @Test
    public void testEqualsZeitraumWithEqualZeitraumAndOhneDatum() {
        boolean ohneDatum = true;

        ZeitraumEntitaet entity = new ZeitraumEntitaet(ANFANG, ENDE, ohneDatum);

        Zeitraum zeitraum = Zeitraum.of(ANFANG.toLocalTime(), ENDE.toLocalTime());

        // Test der equalsZeitraum-Methode
        assertTrue(entity.equalsZeitraum(zeitraum));
    }

    @Test
    public void testEqualsZeitraumWithEqualZeitraumAndWithDatum() {
        boolean ohneDatum = false;

        ZeitraumEntitaet entity = new ZeitraumEntitaet(ANFANG, ENDE, ohneDatum);

        Zeitraum zeitraum = Zeitraum.of(ANFANG, ENDE);

        // Test der equalsZeitraum-Methode
        assertTrue(entity.equalsZeitraum(zeitraum));
    }

    @Test
    public void testEqualsZeitraumWithDifferentZeitraum() {
        boolean ohneDatum1 = true;

        ZonedDateTime anfang2 = ZonedDateTime.of(2023, 1, 1, 11, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime ende2 = ZonedDateTime.of(2023, 1, 1, 13, 0, 0, 0, ZoneId.systemDefault());

        ZeitraumEntitaet entity = new ZeitraumEntitaet(ANFANG, ENDE, ohneDatum1);

        Zeitraum zeitraum = Zeitraum.of(anfang2, ende2);

        // Test der equalsZeitraum-Methode
        assertFalse(entity.equalsZeitraum(zeitraum));
    }
}