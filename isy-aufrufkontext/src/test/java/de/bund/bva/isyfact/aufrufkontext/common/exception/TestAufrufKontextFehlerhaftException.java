package de.bund.bva.isyfact.aufrufkontext.common.exception;

import static org.junit.Assert.*;

import de.bund.bva.isyfact.aufrufkontext.common.exception.AufrufKontextFehlerhaftException;
import de.bund.bva.isyfact.aufrufkontext.common.exception.AusnahmeId;
import org.junit.Test;

public class TestAufrufKontextFehlerhaftException {

	@Test
	public void testAufrufKontextFehlerhaftException() {
		AufrufKontextFehlerhaftException e = new AufrufKontextFehlerhaftException();
		assertEquals(AusnahmeId.UEBERGEBENER_PARAMETER_FALSCH.getCode(), e.getAusnahmeId());
	}

}
