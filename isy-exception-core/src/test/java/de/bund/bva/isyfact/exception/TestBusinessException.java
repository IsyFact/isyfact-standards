package de.bund.bva.isyfact.exception;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.exception.util.TestExceptionFactory;

public class TestBusinessException {
	
	private BaseException pE;
	private TechnicalRuntimeException ptE;

	@Before
	public void setUp(){
		pE = new BaseException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider, TestExceptionFactory.parameter) {
		};
		ptE = new TechnicalRuntimeException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider, TestExceptionFactory.parameter) {
		};
	}
	
	@Test
	public void testBusinessException(){
		TestExceptionFactory.MyBusinessException pbe = TestExceptionFactory.getBusinessException();
		assertEquals(TestExceptionFactory.ausnahmeId, pbe.getAusnahmeId());
		assertEquals(
			TestExceptionFactory.provider.getMessage(TestExceptionFactory.ausnahmeId, TestExceptionFactory.parameter), pbe.getFehlertext());
	}
	
	@Test
	public void testBusinessException2(){
		TestExceptionFactory.MyBusinessException pbe = TestExceptionFactory.getBusinessException(pE);
		assertEquals(TestExceptionFactory.ausnahmeId, pbe.getAusnahmeId());
		assertEquals(pE, pbe.getCause());
		assertEquals(pE.getMessage(), pbe.getMessage());
	}
	
	@Test
	public void testBusinessException3(){
		RuntimeException e = new RuntimeException("meine Exception");
		TestExceptionFactory.MyBusinessException pbe = TestExceptionFactory.getBusinessException(e);
		assertEquals(TestExceptionFactory.ausnahmeId, pbe.getAusnahmeId());
		assertEquals(e, pbe.getCause());
	}
	
	@Test
	public void testBusinessException4(){
		TestExceptionFactory.MyBusinessException pbe = TestExceptionFactory.getBusinessException(ptE);
		assertEquals(TestExceptionFactory.ausnahmeId, pbe.getAusnahmeId());
		assertEquals(ptE, pbe.getCause());
	}
}