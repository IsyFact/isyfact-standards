package de.bund.bva.isyfact.aufrufkontext.test;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;

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
