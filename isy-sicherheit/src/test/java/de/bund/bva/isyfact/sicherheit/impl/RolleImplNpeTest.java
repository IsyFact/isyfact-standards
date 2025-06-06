package de.bund.bva.isyfact.sicherheit.impl;

import de.bund.bva.isyfact.sicherheit.common.exception.RollenRechteMappingException;
import de.bund.bva.isyfact.sicherheit.impl.RolleImpl;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class RolleImplNpeTest {

    @Test
    public void testEqualsZweiUnterschiedlicheRollen() {
        RolleImpl r1 = new RolleImpl("r1", null);
        RolleImpl r2 = new RolleImpl("r2", "notnull");
        assertFalse(r1.equals(r2));
    }

    @Test
    public void testEqualsGleichesRollenObjekt() {
        RolleImpl r1 = new RolleImpl("r1", null);
        assertTrue(r1.equals(r1));
    }

    @Test
    public void testEqualsNullParameter() {
        RolleImpl r1 = new RolleImpl("r1", null);
        assertFalse(r1.equals(null));
    }

    @Test
    public void testEqualsAnderesObjekt() {
        RolleImpl r1 = new RolleImpl("r1", null);
        assertFalse(r1.equals(5));
    }

    @Test(expected = RollenRechteMappingException.class)
    public void testRolleIdNull() {
        RolleImpl r1 = new RolleImpl(null, null);
    }

    @Test
    public void testEqualsGleicheIdsBeideNamenNull() {
        RolleImpl r1 = new RolleImpl("r1", null);
        RolleImpl r2 = new RolleImpl("r1", null);
        assertTrue(r1.equals(r2));
    }

    @Test
    public void testEqualsGleicheIdsUnterschiedlicheNamen() {
        RolleImpl r1 = new RolleImpl("r1", "null");
        RolleImpl r2 = new RolleImpl("r1", "notnull");
        assertFalse(r1.equals(r2));
    }

    @Test
    public void testEqualsGleicheIdsGleicheNamen() {
        RolleImpl r1 = new RolleImpl("r1", "null");
        RolleImpl r2 = new RolleImpl("r1", r1.getName());
        assertTrue(r1.equals(r2));
        assertEquals(r1.toString(), r2.toString());
    }

    @Test
    public void testEqualsGleicheIdsEinNameNull() {
        RolleImpl r1 = new RolleImpl("r1", null);
        RolleImpl r2 = new RolleImpl(r1.getId());
        r2.setName("notnull");
        assertFalse(r1.equals(r2));
    }
}
