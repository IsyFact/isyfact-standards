package de.bund.bva.isyfact.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.exception.util.TestExceptionFactory;

public class TestBusinessException {
	
	private BaseException pE;
	private TechnicalRuntimeException ptE;

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
	public void testBusinessException(){
		TestExceptionFactory.MyBusinessException pbe = TestExceptionFactory.getBusinessException();
		assertEquals(TestExceptionFactory.AUSNAHME_ID, pbe.getAusnahmeId());
		assertEquals(
			TestExceptionFactory.PROVIDER.getMessage(TestExceptionFactory.AUSNAHME_ID, TestExceptionFactory.PARAMETER),
			pbe.getFehlertext()
		);
	}
	
	@Test
	public void testBusinessException2(){
		TestExceptionFactory.MyBusinessException pbe = TestExceptionFactory.getBusinessException(pE);
		assertEquals(TestExceptionFactory.AUSNAHME_ID, pbe.getAusnahmeId());
		assertEquals(pE, pbe.getCause());
		assertEquals(pE.getMessage(), pbe.getMessage());
	}
	
	@Test
	public void testBusinessException3(){
		RuntimeException e = new RuntimeException("meine Exception");
		TestExceptionFactory.MyBusinessException pbe = TestExceptionFactory.getBusinessException(e);
		assertEquals(TestExceptionFactory.AUSNAHME_ID, pbe.getAusnahmeId());
		assertEquals(e, pbe.getCause());
	}
	
	@Test
	public void testBusinessException4(){
		TestExceptionFactory.MyBusinessException pbe = TestExceptionFactory.getBusinessException(ptE);
		assertEquals(TestExceptionFactory.AUSNAHME_ID, pbe.getAusnahmeId());
		assertEquals(ptE, pbe.getCause());
	}
}