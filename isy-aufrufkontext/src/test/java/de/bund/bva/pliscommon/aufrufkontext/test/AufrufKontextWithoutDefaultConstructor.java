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
package de.bund.bva.pliscommon.aufrufkontext.test;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;

public class AufrufKontextWithoutDefaultConstructor implements AufrufKontext {

	private AufrufKontextWithoutDefaultConstructor(){}
	
	@Override
	public String getDurchfuehrenderBenutzerKennung() {
		return null;
	}

	@Override
	public void setDurchfuehrenderBenutzerKennung(String durchfuehrenderBenutzerKennung) {
		// leer
	}

	@Override
	public String getDurchfuehrenderBenutzerPasswort() {
		return null;
	}

	@Override
	public void setDurchfuehrenderBenutzerPasswort(String durchfuehrenderBenutzerPasswort) {
		// leer
	}

	@Override
	public String getDurchfuehrendeBehoerde() {
		return null;
	}

	@Override
	public void setDurchfuehrendeBehoerde(String durchfuehrendeBehoerde) {
		// leer
	}

	@Override
	public String getKorrelationsId() {
		return null;
	}

	@Override
	public void setKorrelationsId(String korrelationsId) {
		// leer
	}

	@Override
	public String[] getRolle() {
		return null;
	}

	@Override
	public void setRolle(String[] rolle) {
		// leer
	}

	@Override
	public boolean isRollenErmittelt() {
		return false;
	}

	@Override
	public void setRollenErmittelt(boolean rollenErmittelt) {
		// leer
	}

	@Override
	public String getDurchfuehrenderSachbearbeiterName() {
		return null;
	}

	@Override
	public void setDurchfuehrenderSachbearbeiterName(String durchfuehrenderSachbearbeiterName) {
		// leer
	}

	@Override
	public String getDurchfuehrenderBenutzerInterneKennung() {
		return null;
	}

	@Override
	public void setDurchfuehrenderBenutzerInterneKennung(String durchfuehrenderBenutzerInterneKennung) {
		// leer
	}

}
