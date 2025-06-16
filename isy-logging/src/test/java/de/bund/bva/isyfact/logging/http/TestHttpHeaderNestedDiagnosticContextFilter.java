package de.bund.bva.isyfact.logging.http;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

public class TestHttpHeaderNestedDiagnosticContextFilter {

	@Test
	public void testGetNestedDiagnosticContextMessage() {
		HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
		when(mock.getHeader("X-Correlation-Id")).thenReturn("someId");
		
		HttpHeaderNestedDiagnosticContextFilter filter = new HttpHeaderNestedDiagnosticContextFilter();
		String id = filter.getNestedDiagnosticContextMessage(mock);
		
		assertEquals("someId", id);
	}
	
	@Test
	public void testGetNestedDiagnosticContextMessage2() {
		HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
		
		HttpHeaderNestedDiagnosticContextFilter filter = new HttpHeaderNestedDiagnosticContextFilter();
		filter.setCorrelationIdHttpHeaderName(null);
		String id = filter.getNestedDiagnosticContextMessage(mock);
		
		assertNotNull(id);
	}
	
	@Test
	public void testGetNestedDiagnosticContextMessage3() {
		HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
		when(mock.getHeader("Correlation-Id")).thenReturn(null);
		
		HttpHeaderNestedDiagnosticContextFilter filter = new HttpHeaderNestedDiagnosticContextFilter();
		String id = filter.getNestedDiagnosticContextMessage(mock);
		
		assertNotNull(id);
	}

}
