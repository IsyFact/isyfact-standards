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
package de.bund.bva.isyfact.aufrufkontext.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
