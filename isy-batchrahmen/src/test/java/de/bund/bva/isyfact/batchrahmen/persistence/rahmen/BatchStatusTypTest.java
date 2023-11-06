package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class BatchStatusTypTest {

    @Test
    public void checkGetterSetter() {
        BatchStatus status = new BatchStatus();

        assertEquals(null, status.getBatchName());
        status.setBatchName("batchName");
        assertEquals("batchName", status.getBatchName());

        assertEquals(null, status.getBatchStatus());
        status.setBatchStatus("batchStatus");
        assertEquals("batchStatus", status.getBatchStatus());

        assertEquals(null, status.getKonfigurationsParameter());
        status.setKonfigurationsParameter(null);
        assertEquals(null, status.getKonfigurationsParameter());

        assertEquals(0, status.getSatzNummerLetztesCommit());
        status.setSatzNummerLetztesCommit(1);
        assertEquals(1, status.getSatzNummerLetztesCommit());

        assertEquals(null, status.getSchluesselLetztesCommit());
        status.setSchluesselLetztesCommit("schluesselLetztesCommit");
        assertEquals("schluesselLetztesCommit", status.getSchluesselLetztesCommit());

        assertEquals(null, status.getDatumLetzterStart());
        status.setDatumLetzterStart(null);
        assertEquals(null, status.getDatumLetzterStart());

        assertEquals(null, status.getDatumLetzterAbbruch());
        status.setDatumLetzterAbbruch(null);
        assertEquals(null, status.getDatumLetzterAbbruch());

        assertEquals(null, status.getDatumLetzterErfolg());
        status.setDatumLetzterErfolg(null);
        assertEquals(null, status.getDatumLetzterErfolg());
    }
}
