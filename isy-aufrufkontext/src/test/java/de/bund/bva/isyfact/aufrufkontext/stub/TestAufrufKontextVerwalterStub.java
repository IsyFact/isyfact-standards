package de.bund.bva.isyfact.aufrufkontext.stub;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextFactoryImpl;

public class TestAufrufKontextVerwalterStub {

	private AufrufKontext aKontext;
	private AufrufKontextVerwalterStub<AufrufKontext> stub;

	@Before
	public void setUp() {
		stub = new AufrufKontextVerwalterStub<>();
		stub.setAufrufKontextFactory(new AufrufKontextFactoryImpl<>());
		stub.setFesterAufrufKontext(true);
		aKontext = stub.getAufrufKontext();
	}

	private void assertEqualsAufrufKontext(AufrufKontext k1, AufrufKontext k2) {
		assertEquals(k1.getDurchfuehrendeBehoerde(), k2.getDurchfuehrendeBehoerde());
		assertEquals(k1.getDurchfuehrenderBenutzerKennung(), k2.getDurchfuehrenderBenutzerKennung());
		assertEquals(k1.getDurchfuehrenderBenutzerPasswort(), k2.getDurchfuehrenderBenutzerPasswort());
		assertEquals(k1.getDurchfuehrenderSachbearbeiterName(), k2.getDurchfuehrenderSachbearbeiterName());
		assertEquals(k1.getDurchfuehrenderBenutzerInterneKennung(), k2.getDurchfuehrenderBenutzerInterneKennung());
		assertArrayEquals(k1.getRolle(), k2.getRolle());
	}

	@Test
	public void testErzeugeAufrufKontext() {
		AufrufKontext kontext = stub.getAufrufKontext();
		assertEqualsAufrufKontext(kontext, aKontext);
	}

	@Test
	public void testErzeugeAufrufKontext2() {

		String durchfuehrendeBehoerde = "654321";
		String durchfuehrenderBenutzerKennung = "max.mustermann@bva.bund.de";
		String durchfuehrenderBenutzerPasswort = "passwort";
		String durchfuehrenderSachbearbeiterName = "Max Mustermann";
		String durchfuehrenderBenutzerInterneKennung = "Max013";
		String[] rollen = null;

		stub.setDurchfuehrendeBehoerde(durchfuehrendeBehoerde);
		stub.setDurchfuehrenderBenutzerKennung(durchfuehrenderBenutzerKennung);
		stub.setDurchfuehrenderBenutzerPasswort(durchfuehrenderBenutzerPasswort);
		stub.setDurchfuehrenderSachbearbeiterName(durchfuehrenderSachbearbeiterName);
		stub.setDurchfuehrenderBenutzerInterneKennung(durchfuehrenderBenutzerInterneKennung);
		stub.setRollen(rollen);

		stub.setFesterAufrufKontext(false);
		stub.afterPropertiesSet();

		AufrufKontext kontext = stub.getAufrufKontext();

		assertEquals(durchfuehrendeBehoerde, kontext.getDurchfuehrendeBehoerde());
		assertEquals(durchfuehrenderBenutzerKennung, kontext.getDurchfuehrenderBenutzerKennung());
		assertEquals(durchfuehrenderBenutzerPasswort, kontext.getDurchfuehrenderBenutzerPasswort());
		assertEquals(durchfuehrenderSachbearbeiterName, kontext.getDurchfuehrenderSachbearbeiterName());
		assertEquals(durchfuehrenderBenutzerInterneKennung, kontext.getDurchfuehrenderBenutzerInterneKennung());
		assertArrayEquals(new String[]{}, kontext.getRolle());
	}

	@Test
	public void testErzeugeAufrufKontext3() {

		stub.setAufrufKontext(aKontext);
		stub.setFesterAufrufKontext(false);
		AufrufKontext kontext = stub.getAufrufKontext();
		
		assertEquals(aKontext, kontext);
	}

}
