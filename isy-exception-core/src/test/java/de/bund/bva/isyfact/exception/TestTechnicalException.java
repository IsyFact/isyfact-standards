package de.bund.bva.isyfact.exception;

import static org.junit.Assert.assertEquals;

import de.bund.bva.isyfact.exception.util.TestExceptionFactory;
import org.junit.Test;

public class TestTechnicalException {
		
	@Test
	public void testTechnicalException() {
		TestExceptionFactory.MyTechnicalException pte = TestExceptionFactory.getTechnicalException();
		assertEquals(TestExceptionFactory.ausnahmeId, pte.getAusnahmeId());
	}
	
	@Test
	public void testTechnicalException2() {
		RuntimeException e = new RuntimeException("meine Exception");
		TestExceptionFactory.MyTechnicalException pte = TestExceptionFactory.getTechnicalException(e);
		assertEquals(TestExceptionFactory.ausnahmeId, pte.getAusnahmeId());
		assertEquals(e, pte.getCause());
	}
}
