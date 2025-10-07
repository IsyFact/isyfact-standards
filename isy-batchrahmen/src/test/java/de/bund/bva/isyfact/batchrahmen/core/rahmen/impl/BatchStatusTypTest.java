package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BatchStatusTypTest {
    @Test
    public void testGetName() {
        Assertions.assertEquals("neu", BatchStatusTyp.NEU.getName());
        Assertions.assertEquals("laeuft", BatchStatusTyp.LAEUFT.getName());
        Assertions.assertEquals("abgebrochen", BatchStatusTyp.ABGEBROCHEN.getName());
        Assertions.assertEquals("beendet", BatchStatusTyp.BEENDET.getName());
    }

    @Test
    public void testFromCode() {
        Assertions.assertEquals(BatchStatusTyp.NEU, BatchStatusTyp.fromCode("neu"));
        Assertions.assertEquals(BatchStatusTyp.LAEUFT, BatchStatusTyp.fromCode("laeuft"));
        Assertions.assertEquals(BatchStatusTyp.ABGEBROCHEN, BatchStatusTyp.fromCode("abgebrochen"));
        Assertions.assertEquals(BatchStatusTyp.BEENDET, BatchStatusTyp.fromCode("beendet"));
        Assertions.assertNull(BatchStatusTyp.fromCode("ungueltig"));
    }
}
