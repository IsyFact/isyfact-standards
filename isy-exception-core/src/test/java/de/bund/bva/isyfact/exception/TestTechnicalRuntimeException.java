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
package de.bund.bva.isyfact.exception;

import static org.junit.Assert.assertEquals;

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
