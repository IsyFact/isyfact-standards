package de.bund.bva.pliscommon.persistence.datetime;

import java.time.LocalDate;

import de.bund.bva.isyfact.datetime.core.UngewissesDatum;
import org.junit.Test;

import static org.junit.Assert.*;

public class UngewissesDatumEntitaetTest {

    @Test
    public void toUngewissesDatum() {
        UngewissesDatumEntitaet entitaet = new UngewissesDatumEntitaet();

        LocalDate anfang = LocalDate.of(2017, 1, 1);
        LocalDate ende = LocalDate.of(2017, 6, 30);

        entitaet.setAnfang(anfang);
        entitaet.setEnde(ende);

        UngewissesDatum ungewissesDatum = entitaet.toUngewissesDatum();

        assertEquals(anfang, ungewissesDatum.getAnfang());
        assertEquals(ende, ungewissesDatum.getEnde());
    }
}