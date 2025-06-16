package de.bund.bva.isyfact.exception;

import static org.junit.Assert.*;

import org.junit.Test;

import de.bund.bva.isyfact.exception.util.TestExceptionFactory;

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
