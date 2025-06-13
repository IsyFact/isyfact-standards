package de.bund.bva.isyfact.exception;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.exception.util.TestExceptionFactory;

public class TestTechnicalRuntimeException {
	
	BaseException pE;
	TechnicalRuntimeException ptE;
		
	@Before
	public void setUp(){
		pE = new BaseException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider, TestExceptionFactory.parameter) {
		};
		ptE = new TechnicalRuntimeException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider, TestExceptionFactory.parameter) {
		};
	}
	
	@Test
	public void testGetterRuntimeException() {
		TestExceptionFactory.MyTechnicalRuntimeException pte = TestExceptionFactory.getTechnicalRuntimeException();
		assertEquals(TestExceptionFactory.ausnahmeId, pte.getAusnahmeId());
	}
	
	@Test
	public void testGetterRuntimeException2() {
		RuntimeException e = new RuntimeException("meine Exception");
		TestExceptionFactory.MyTechnicalRuntimeException pte = TestExceptionFactory.getTechnicalRuntimeException();
		assertEquals(TestExceptionFactory.ausnahmeId, pte.getAusnahmeId());
		assertEquals(
			TestExceptionFactory.provider.getMessage(TestExceptionFactory.ausnahmeId, TestExceptionFactory.parameter), pte.getFehlertext());
	}
	
	@Test
	public void testBusinessException2(){
		TestExceptionFactory.MyTechnicalRuntimeException pte = TestExceptionFactory.getTechnicalRuntimeException(pE);
		assertEquals(TestExceptionFactory.ausnahmeId, pte.getAusnahmeId());
		assertEquals(pE, pte.getCause());
		assertEquals(pE.getMessage(), pte.getMessage());
	}
	
	@Test
	public void testBusinessException4(){
		TestExceptionFactory.MyTechnicalRuntimeException pte = TestExceptionFactory.getTechnicalRuntimeException(ptE);
		assertEquals(TestExceptionFactory.ausnahmeId, pte.getAusnahmeId());
		assertEquals(ptE, pte.getCause());
	}
	
	@Test
	public void testBusinessException5(){
		Exception e = new Exception();
		TestExceptionFactory.MyTechnicalRuntimeException pte = TestExceptionFactory.getTechnicalRuntimeException(e);
		assertEquals(TestExceptionFactory.ausnahmeId, pte.getAusnahmeId());
		assertEquals(e, pte.getCause());
	}
}
