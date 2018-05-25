package de.bund.bva.pliscommon.persistence.datetime;

import java.time.LocalTime;

import de.bund.bva.isyfact.datetime.core.UngewisseZeit;
import org.junit.Test;

import static org.junit.Assert.*;

public class UngewisseZeitEntitaetTest {

    @Test
    public void toUngewisseZeit() {
        LocalTime anfang = LocalTime.of(12, 0);
        LocalTime ende = LocalTime.of(18, 30);

        UngewisseZeitEntitaet entitaet = new UngewisseZeitEntitaet();
        entitaet.setAnfang(anfang);
        entitaet.setEnde(ende);

        UngewisseZeit ungewisseZeit = entitaet.toUngewisseZeit();

        assertEquals(anfang, ungewisseZeit.getAnfang());
        assertEquals(ende, ungewisseZeit.getEnde());
    }
}