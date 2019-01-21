package de.bund.bva.isyfact.persistence.datetime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import de.bund.bva.isyfact.datetime.core.Zeitraum;
import de.bund.bva.isyfact.persistence.datetime.ZeitraumEntitaet;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZeitraumEntitaetTest {

    @Test
    public void toZeitraum() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.of(2017, 8, 1, 15, 0), ZoneId.systemDefault());

        ZonedDateTime anfang = zonedDateTime;
        ZonedDateTime ende = zonedDateTime.plusHours(12);

        ZeitraumEntitaet entitaet = new ZeitraumEntitaet();
        entitaet.setAnfang(anfang);
        entitaet.setEnde(ende);
        entitaet.setOhneDatum(false);

        Zeitraum zeitraum = entitaet.toZeitraum();

        assertEquals(anfang, zeitraum.getAnfangsdatumzeit());
        assertEquals(ende, zeitraum.getEndedatumzeit());
        assertFalse(entitaet.isOhneDatum());

        entitaet.setOhneDatum(true);

        zeitraum = entitaet.toZeitraum();

        assertNull(zeitraum.getAnfangsdatumzeit());
        assertNull(zeitraum.getEndedatumzeit());
        assertEquals(anfang.toLocalTime(), zeitraum.getAnfangszeit());
        assertEquals(ende.toLocalTime(), zeitraum.getEndzeit());
        assertTrue(zeitraum.isOhneDatum());
    }
}