package de.bund.bva.isyfact.sicherheit.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.sicherheit.impl.RechtImpl;
import org.junit.Test;

public class RechtImplTest {

	@Test(expected = IllegalArgumentException.class)
	public void testRechtOhneID() {
		new RechtImpl(null, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRechtLeereID() {
		new RechtImpl("", null);
	}
	
	@Test()
	public void testRechtNullProps() {
		Map<String, Object> map = new HashMap<>();
		map.put("key", "value");
		RechtImpl recht = new RechtImpl("ro1", map);
		assertEquals("value", recht.getProperty("key"));
	}
	
	@Test
	public void testEqualsRechteIdentisch(){
		RechtImpl recht = new RechtImpl("id", new HashMap<>());
		assertTrue(recht.equals(recht));
	}
	
	@Test
	public void testEqualsZweitesRechtNull(){
		RechtImpl recht = new RechtImpl("id", new HashMap<>());
		assertFalse(recht.equals(null));
	}
	
	@Test
	public void testEqualsZweitesRechtAnderesObjekt(){
		RechtImpl recht = new RechtImpl("id", new HashMap<>());
		assertFalse(recht.equals(new Integer(1)));
	}
	
	@Test
	public void testEqualsRechteIdsGleich(){
		RechtImpl r1 = new RechtImpl("r1", new HashMap<>());
		RechtImpl r2 = new RechtImpl("r1", new HashMap<>());
		assertTrue(r1.equals(r2));
	}

	@Test
	public void testEqualsRechteIdsVerschieden(){
		RechtImpl r1 = new RechtImpl("r1", new HashMap<>());
		RechtImpl r2 = new RechtImpl("r2", new HashMap<>());
		assertFalse(r1.equals(r2));
	}
}
