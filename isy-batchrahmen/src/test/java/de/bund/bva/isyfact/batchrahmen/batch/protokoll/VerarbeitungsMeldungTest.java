package de.bund.bva.isyfact.batchrahmen.batch.protokoll;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VerarbeitungsMeldungTest {
    @Test
    public void testConstructorWithId() {
        VerarbeitungsMeldung meldung = new VerarbeitungsMeldung("123", MeldungTyp.INFO, "Eine Testmeldung");
        Assertions.assertEquals("123", meldung.getId());
        Assertions.assertEquals(MeldungTyp.INFO, meldung.getTyp());
        Assertions.assertEquals("Eine Testmeldung", meldung.getText());
        Assertions.assertNull(meldung.getFachlicheId());
    }

    @Test
    public void testConstructorWithFachlicheId() {
        VerarbeitungsMeldung meldung = new VerarbeitungsMeldung("123", "456", MeldungTyp.INFO, "Eine Testmeldung");
        Assertions.assertEquals("123", meldung.getId());
        Assertions.assertEquals("456", meldung.getFachlicheId());
        Assertions.assertEquals(MeldungTyp.INFO, meldung.getTyp());
        Assertions.assertEquals("Eine Testmeldung", meldung.getText());
    }

    @Test
    public void testSetFachlicheId() {
        VerarbeitungsMeldung meldung = new VerarbeitungsMeldung("123", MeldungTyp.INFO, "Eine Testmeldung");
        Assertions.assertNull(meldung.getFachlicheId());
        meldung.setFachlicheId("456");
        Assertions.assertEquals("456", meldung.getFachlicheId());
    }
}
