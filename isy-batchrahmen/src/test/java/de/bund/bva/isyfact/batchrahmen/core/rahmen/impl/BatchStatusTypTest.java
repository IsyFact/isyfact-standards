package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;

import org.junit.Assert;
import org.junit.Test;


public class BatchStatusTypTest {
    @Test
    public void testGetName() {
        Assert.assertEquals("neu", BatchStatusTyp.NEU.getName());
        Assert.assertEquals("laeuft", BatchStatusTyp.LAEUFT.getName());
        Assert.assertEquals("abgebrochen", BatchStatusTyp.ABGEBROCHEN.getName());
        Assert.assertEquals("beendet", BatchStatusTyp.BEENDET.getName());
    }

    @Test
    public void testFromCode() {
        Assert.assertEquals(BatchStatusTyp.NEU, BatchStatusTyp.fromCode("neu"));
        Assert.assertEquals(BatchStatusTyp.LAEUFT, BatchStatusTyp.fromCode("laeuft"));
        Assert.assertEquals(BatchStatusTyp.ABGEBROCHEN, BatchStatusTyp.fromCode("abgebrochen"));
        Assert.assertEquals(BatchStatusTyp.BEENDET, BatchStatusTyp.fromCode("beendet"));
        Assert.assertNull(BatchStatusTyp.fromCode("ungueltig"));
    }
}
