package de.bund.bva.isyfact.batchrahmen.batch.protokoll;

import org.junit.Assert;
import org.junit.Test;

public class VerarbeitungsMeldungTest {
    @Test
    public void testConstructorWithId() {
        VerarbeitungsMeldung meldung = new VerarbeitungsMeldung("123", MeldungTyp.INFO, "Eine Testmeldung");
        Assert.assertEquals("123", meldung.getId());
        Assert.assertEquals(MeldungTyp.INFO, meldung.getTyp());
        Assert.assertEquals("Eine Testmeldung", meldung.getText());
        Assert.assertNull(meldung.getFachlicheId());
    }

    @Test
    public void testConstructorWithFachlicheId() {
        VerarbeitungsMeldung meldung = new VerarbeitungsMeldung("123", "456", MeldungTyp.INFO, "Eine Testmeldung");
        Assert.assertEquals("123", meldung.getId());
        Assert.assertEquals("456", meldung.getFachlicheId());
        Assert.assertEquals(MeldungTyp.INFO, meldung.getTyp());
        Assert.assertEquals("Eine Testmeldung", meldung.getText());
    }

    @Test
    public void testSetFachlicheId() {
        VerarbeitungsMeldung meldung = new VerarbeitungsMeldung("123", MeldungTyp.INFO, "Eine Testmeldung");
        Assert.assertNull(meldung.getFachlicheId());
        meldung.setFachlicheId("456");
        Assert.assertEquals("456", meldung.getFachlicheId());
    }
}
