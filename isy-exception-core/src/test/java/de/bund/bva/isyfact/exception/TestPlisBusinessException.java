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

import de.bund.bva.isyfact.exception.util.TestExceptionFactory;
import org.junit.Before;
import org.junit.Test;

public class TestPlisBusinessException {
	
	private PlisException pE;
	private PlisTechnicalRuntimeException ptE;

	@Before
	public void setUp(){
		pE = new PlisException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider, TestExceptionFactory.parameter) {
		};
		ptE = new PlisTechnicalRuntimeException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider, TestExceptionFactory.parameter) {
		};
	}
	
	@Test
	public void testPlisBusinessException(){		
		TestExceptionFactory.MyPlisBusinessException pbe = TestExceptionFactory.getPlisBusinessException();
		assertEquals(TestExceptionFactory.ausnahmeId, pbe.getAusnahmeId());
		assertEquals(
			TestExceptionFactory.provider.getMessage(TestExceptionFactory.ausnahmeId, TestExceptionFactory.parameter), pbe.getFehlertext());
	}
	
	@Test
	public void testPlisBusinessException2(){
		TestExceptionFactory.MyPlisBusinessException pbe = TestExceptionFactory.getPlisBusinessException(pE);
		assertEquals(TestExceptionFactory.ausnahmeId, pbe.getAusnahmeId());
		assertEquals(pE, pbe.getCause());
		assertEquals(pE.getMessage(), pbe.getMessage());
	}
	
	@Test
	public void testPlisBusinessException3(){
		RuntimeException e = new RuntimeException("meine Exception");
		TestExceptionFactory.MyPlisBusinessException pbe = TestExceptionFactory.getPlisBusinessException(e);
		assertEquals(TestExceptionFactory.ausnahmeId, pbe.getAusnahmeId());
		assertEquals(e, pbe.getCause());
	}
	
	@Test
	public void testPlisBusinessException4(){
		TestExceptionFactory.MyPlisBusinessException pbe = TestExceptionFactory.getPlisBusinessException(ptE);
		assertEquals(TestExceptionFactory.ausnahmeId, pbe.getAusnahmeId());
		assertEquals(ptE, pbe.getCause());
	}
}