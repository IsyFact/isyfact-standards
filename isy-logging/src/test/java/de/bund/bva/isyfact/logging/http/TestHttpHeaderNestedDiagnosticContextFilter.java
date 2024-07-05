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
package de.bund.bva.isyfact.logging.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

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
