/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
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

	/**
	 * Tests the removal of the "Bearer " prefix.
	 */
	@Test
	public void testCuttingOutBearer() {
		stub.setBearerToken("bearer 12345");
		assertEquals("12345", stub.getBearerToken());

		stub.setBearerToken("BEARER 2345");
		assertEquals("2345", stub.getBearerToken());

		stub.setBearerToken("Bearer 12345");
		assertEquals("12345", stub.getBearerToken());

		stub.setBearerToken("2345");
		assertEquals("2345", stub.getBearerToken());
	}

	/**
	 * Tests that null assignment works.
	 */
	@Test
	public void testResettingToNull() {
		stub.setBearerToken("bearer 12345");
		assertEquals("12345", stub.getBearerToken());

		stub.setBearerToken(null);
		assertNull(stub.getBearerToken());
	}

}
