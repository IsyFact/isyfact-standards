package de.bund.bva.isyfact.batchrahmen.batch.protokoll;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StatistikEintragTest {

    @Test
    public void testStatistikEintrag() {
        String id = "TestID";
        String text = "TestText";

        StatistikEintrag eintrag = new StatistikEintrag(id, text);

        assertEquals(id, eintrag.getId());
        assertEquals(text, eintrag.getText());
        assertEquals(0, eintrag.getWert());
        assertEquals(0, eintrag.getReihenfolge());
    }

    @Test
    public void testSetWert() {
        StatistikEintrag eintrag = new StatistikEintrag("TestID", "TestText");
        eintrag.setWert(42);

        assertEquals(42, eintrag.getWert());
    }

    @Test
    public void testErhoeheWert() {
        StatistikEintrag eintrag = new StatistikEintrag("TestID", "TestText");
        eintrag.erhoeheWert();

        assertEquals(1, eintrag.getWert());
    }

    @Test
    public void testReihenfolge() {
        StatistikEintrag eintrag1 = new StatistikEintrag("TestID1", "TestText1");
        eintrag1.setReihenfolge(2);

        StatistikEintrag eintrag2 = new StatistikEintrag("TestID2", "TestText2");
        eintrag2.setReihenfolge(1);

        assertEquals(1, eintrag1.compareTo(eintrag2));
        assertEquals(-1, eintrag2.compareTo(eintrag1));
        eintrag1.setReihenfolge(1);
        assertEquals(0, eintrag2.compareTo(eintrag1));
    }
}