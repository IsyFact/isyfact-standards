package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import de.bund.bva.isyfact.batchrahmen.persistence.rahmen.BatchKonfigurationsParameter;
import org.junit.Test;

import static org.junit.Assert.*;

public class BatchKonfigurationsParameterTest {

    private static final BatchKonfigurationsParameter b1 =
        new BatchKonfigurationsParameter("b1", "pn1", "pw1");

    private static final BatchKonfigurationsParameter b2 =
        new BatchKonfigurationsParameter("b1", "pn1", "pw1");

    private static final BatchKonfigurationsParameter b3 =
        new BatchKonfigurationsParameter("b1", "pn1", "pw1");

    private static final BatchKonfigurationsParameter b4 =
        new BatchKonfigurationsParameter("b2", "pn2", "pw2");

    @Test
    public void equalsHashCode_reflexiv() {
        assertTrue(b1.equals(b1));
        assertFalse(b1.equals(b4));
    }

    @Test
    public void equalsHashCode_symmetrisch() {
        assertTrue(b1.equals(b2));
        assertTrue(b2.equals(b1));

        assertTrue(b1.hashCode() == b2.hashCode());
    }

    @Test
    public void equalsHashCode_transitiv() {
        assertTrue(b1.equals(b2));
        assertTrue(b2.equals(b3));
        assertTrue(b1.equals(b3));

        assertTrue(b1.hashCode() == b2.hashCode());
        assertTrue(b2.hashCode() == b3.hashCode());
    }

    @Test
    public void equalsHashCodeNichtGleich() {
        BatchKonfigurationsParameter b1 = new BatchKonfigurationsParameter("b1", "pn1", "pw1");
        BatchKonfigurationsParameter b2 = new BatchKonfigurationsParameter("b2", "pn1", "pw1");

        assertFalse(b1.equals(b2));
        assertFalse(b1.hashCode() == b2.hashCode());

        b2.setBatchId(b1.getBatchId());
        b2.setParameterName("pn2");

        assertFalse(b1.equals(b2));
        assertFalse(b1.hashCode() == b2.hashCode());

        b2.setParameterName(b1.getParameterName());
        b2.setParameterWert("pw2");

        assertFalse(b1.equals(b2));
        assertFalse(b1.hashCode() == b2.hashCode());

    }
}
