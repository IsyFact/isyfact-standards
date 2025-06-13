package de.bund.bva.isyfact.aufrufkontext.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.common.exception.AufrufKontextKeinDefaultKonstruktorException;
import de.bund.bva.isyfact.aufrufkontext.test.AufrufKontextWithoutDefaultConstructor;

public class TestAufrufKontextFactoryImpl {

	
	@Test
	public void testErzeugeAufrufKontext() {
		AufrufKontextFactoryImpl<AufrufKontextImpl> factory =
				new AufrufKontextFactoryImpl<AufrufKontextImpl>();		
		AufrufKontext kontext = factory.erzeugeAufrufKontext();
		String kennung = "interneKennung";
		assertNull(kontext.getDurchfuehrenderBenutzerInterneKennung());
		kontext.setDurchfuehrenderBenutzerInterneKennung(kennung);
		assertEquals(kennung, kontext.getDurchfuehrenderBenutzerInterneKennung());
	}
	
	@Test(expected = AufrufKontextKeinDefaultKonstruktorException.class)
	public void testErzeugeAufrufKontext2() {
		AufrufKontextFactoryImpl<AufrufKontextWithoutDefaultConstructor> factory = 
				new AufrufKontextFactoryImpl<AufrufKontextWithoutDefaultConstructor>();		
		factory.setAufrufKontextKlasse(AufrufKontextWithoutDefaultConstructor.class);
		factory.erzeugeAufrufKontext();
	}

}
