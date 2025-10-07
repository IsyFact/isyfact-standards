package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

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
        Set<BatchKonfigurationsParameter> parameters = new HashSet<>();
        BatchKonfigurationsParameter parameter = new BatchKonfigurationsParameter();
        parameters.add(parameter);
        status.setKonfigurationsParameter(parameters);

        assertNotNull(status.getKonfigurationsParameter());
        assertEquals(1, status.getKonfigurationsParameter().size());
        assertTrue(status.getKonfigurationsParameter().contains(parameter));

        assertEquals(0, status.getSatzNummerLetztesCommit());
        status.setSatzNummerLetztesCommit(1);
        assertEquals(1, status.getSatzNummerLetztesCommit());

        assertEquals(null, status.getSchluesselLetztesCommit());
        status.setSchluesselLetztesCommit("schluesselLetztesCommit");
        assertEquals("schluesselLetztesCommit", status.getSchluesselLetztesCommit());

        assertEquals(null, status.getDatumLetzterStart());
        Timestamp timestamp = new Timestamp(1000);
        status.setDatumLetzterStart(timestamp);
        assertEquals(timestamp, status.getDatumLetzterStart());

        assertEquals(null, status.getDatumLetzterAbbruch());
        Date dateLetzerAbbruch = new Date();
        status.setDatumLetzterAbbruch(dateLetzerAbbruch);
        assertEquals(dateLetzerAbbruch, status.getDatumLetzterAbbruch());

        assertEquals(null, status.getDatumLetzterErfolg());
        Date dateLetzerErfolg = new Date();
        status.setDatumLetzterErfolg(dateLetzerErfolg);
        assertEquals(dateLetzerErfolg, status.getDatumLetzterErfolg());
    }
}
