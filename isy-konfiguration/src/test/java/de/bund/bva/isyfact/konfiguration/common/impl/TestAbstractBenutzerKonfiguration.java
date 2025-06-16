package de.bund.bva.isyfact.konfiguration.common.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.bund.bva.isyfact.konfiguration.common.Benutzerkonfiguration;

public class TestAbstractBenutzerKonfiguration {

	private static final String PARAMETER = "param";
	
	@Test
	public void testSetzeWertInteger() {
		Benutzerkonfiguration bk = new TestBenutzerKonfiguration();
		
		int wert = 14;
		bk.setValue(PARAMETER, wert);
		assertEquals(wert, bk.getAsInteger(PARAMETER));
	}
	
	@Test
	public void testSetzeWertLong() {
		Benutzerkonfiguration bk = new TestBenutzerKonfiguration();
		
		long wert = 1l;
		bk.setValue(PARAMETER, wert);
		assertEquals(wert, bk.getAsLong(PARAMETER));
	}
	
	@Test
	public void testSetzeWertDouble() {
		Benutzerkonfiguration bk = new TestBenutzerKonfiguration();
		
		double wert = 1.0;
		bk.setValue(PARAMETER, wert);
		assertEquals(wert, bk.getAsDouble(PARAMETER),0.0);
	}
	
	@Test
	public void testSetzeWertBoolean() {
		Benutzerkonfiguration bk = new TestBenutzerKonfiguration();
		
		boolean wert = true;
		bk.setValue(PARAMETER, wert);
		assertEquals(wert, bk.getAsBoolean(PARAMETER));
	}
	
	
	class TestBenutzerKonfiguration extends AbstractBenutzerkonfiguration {
		
		private Map<String, String> konfiguration = new HashMap<String, String>();
		
		@Override
		public Set<String> getSchluessel() {
			return konfiguration.keySet();
		}

		@Override
		protected boolean containsKey(String schluessel) {
			return konfiguration.containsKey(schluessel);
		}

		@Override
		protected String getValue(String schluessel) {
			return konfiguration.get(schluessel);
		}

		@Override
		public void setValue(String schluessel, String wert) {
			konfiguration.put(schluessel, wert);
		}

		@Override
		public boolean removeValue(String schluessel) {
			return konfiguration.remove(schluessel) != null; 
		}
		
	}

}
