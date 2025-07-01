package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import static org.junit.Assert.*;

import org.junit.Test;

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
        assertEquals(b1, b1);
        assertNotEquals(b1, b4);
    }

    @Test
    public void equalsHashCode_symmetrisch() {
        assertEquals(b1, b2);
        assertEquals(b2, b1);

        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void equalsHashCode_transitiv() {
        assertEquals(b1, b2);
        assertEquals(b2, b3);
        assertEquals(b1, b3);

        assertEquals(b1.hashCode(), b2.hashCode());
        assertEquals(b2.hashCode(), b3.hashCode());
    }

    @Test
    public void equalsHashCodeNichtGleich() {
        BatchKonfigurationsParameter b1 = new BatchKonfigurationsParameter("b1", "pn1", "pw1");
        BatchKonfigurationsParameter b2 = new BatchKonfigurationsParameter("b2", "pn1", "pw1");

        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());

        b2.setBatchId(b1.getBatchId());
        b2.setParameterName("pn2");

        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());

        b2.setParameterName(b1.getParameterName());
        b2.setParameterWert("pw2");

        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());

    }

    @Test
    public void checkGetterSetter() {
        BatchKonfigurationsParameter b1 = new BatchKonfigurationsParameter();

        assertNull(b1.getParameterWert());
        b1.setParameterWert("pw1");
        assertEquals("pw1", b1.getParameterWert());

        assertNull(b1.getBatchId());
        b1.setBatchId("b1");
        assertEquals("b1", b1.getBatchId());

        assertNull(b1.getParameterName());
        b1.setParameterName("pn1");
        assertEquals("pn1", b1.getParameterName());
    }

    @Test
    public void equalsNullWerte() {
        BatchKonfigurationsParameter b1 = new BatchKonfigurationsParameter(null, null, null);
        BatchKonfigurationsParameter b2 = new BatchKonfigurationsParameter("b2", "pn2", "pw2");

        assertEquals(b1, b1);
        assertNotEquals(null, b1);
        assertNotEquals("", b1);

        assertNotEquals(b1, b2);
        b1.setBatchId("b1");
        b2.setBatchId(null);
        assertNotEquals(b1, b2);
        b2.setBatchId("b1");

        assertNotEquals(b1, b2);
        b1.setParameterName("pn1");
        b2.setParameterName(null);
        assertNotEquals(b1, b2);
        b2.setParameterName("pn1");

        assertNotEquals(b1, b2);
        b1.setParameterWert("pw1");
        b2.setParameterWert(null);
        assertNotEquals(b1, b2);
        b2.setParameterWert("pw1");

        b1.equals(b2);
    }
}