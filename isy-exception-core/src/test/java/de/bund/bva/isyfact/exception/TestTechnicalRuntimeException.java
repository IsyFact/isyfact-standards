package de.bund.bva.isyfact.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.exception.util.TestExceptionFactory;

public class TestTechnicalRuntimeException {
	
	BaseException pE;
	TechnicalRuntimeException ptE;
		
	@BeforeEach
	public void setUp(){
		pE = new BaseException(
			TestExceptionFactory.AUSNAHME_ID,
			TestExceptionFactory.PROVIDER,
			TestExceptionFactory.PARAMETER
		) {
		};
		ptE = new TechnicalRuntimeException(
			TestExceptionFactory.AUSNAHME_ID,
			TestExceptionFactory.PROVIDER,
			TestExceptionFactory.PARAMETER
		) {
		};
	}
	
	@Test
	public void testGetterRuntimeException() {
		TestExceptionFactory.MyTechnicalRuntimeException pte = TestExceptionFactory.getTechnicalRuntimeException();
		assertEquals(TestExceptionFactory.AUSNAHME_ID, pte.getAusnahmeId());
	}
	
	@Test
	public void testGetterRuntimeException2() {
		RuntimeException e = new RuntimeException("meine Exception");
		TestExceptionFactory.MyTechnicalRuntimeException pte = TestExceptionFactory.getTechnicalRuntimeException();
		assertEquals(TestExceptionFactory.AUSNAHME_ID, pte.getAusnahmeId());
		assertEquals(
			TestExceptionFactory.PROVIDER.getMessage(TestExceptionFactory.AUSNAHME_ID, TestExceptionFactory.PARAMETER),
			pte.getFehlertext()
		);
	}
	
	@Test
	public void testBusinessException2(){
		TestExceptionFactory.MyTechnicalRuntimeException pte = TestExceptionFactory.getTechnicalRuntimeException(pE);
		assertEquals(TestExceptionFactory.AUSNAHME_ID, pte.getAusnahmeId());
		assertEquals(pE, pte.getCause());
		assertEquals(pE.getMessage(), pte.getMessage());
	}
	
	@Test
	public void testBusinessException4(){
		TestExceptionFactory.MyTechnicalRuntimeException pte = TestExceptionFactory.getTechnicalRuntimeException(ptE);
		assertEquals(TestExceptionFactory.AUSNAHME_ID, pte.getAusnahmeId());
		assertEquals(ptE, pte.getCause());
	}
	
	@Test
	public void testBusinessException5(){
		Exception e = new Exception();
		TestExceptionFactory.MyTechnicalRuntimeException pte = TestExceptionFactory.getTechnicalRuntimeException(e);
		assertEquals(TestExceptionFactory.AUSNAHME_ID, pte.getAusnahmeId());
		assertEquals(e, pte.getCause());
	}
}
