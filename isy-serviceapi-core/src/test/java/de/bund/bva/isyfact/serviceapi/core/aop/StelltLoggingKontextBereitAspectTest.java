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
package de.bund.bva.isyfact.serviceapi.core.aop;

import java.util.UUID;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import de.bund.bva.isyfact.serviceapi.core.aop.test.LoggingKontextAspectService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests that the {@link StelltLoggingKontextBereitInterceptor} sets a KorrelationsID (AufrufKontextTO)
 * for Classes without Annotations as well.
 */
public class StelltLoggingKontextBereitAspectTest {

	private LoggingKontextAspectService service;

	@Before
	public void setUp(){
		// cleanUp
		MdcHelper.entferneKorrelationsIds();

		StelltLoggingKontextBereitInterceptor aspect = new StelltLoggingKontextBereitInterceptor();
		LoggingKontextAspectService service = new LoggingKontextAspectService();
		ProxyFactory proxyFactory = new ProxyFactory(service);
		proxyFactory.addAdvice(aspect);
		this.service = (LoggingKontextAspectService) proxyFactory.getProxy();
	}

	@Test
	public void testAufrufOhneParameter() {
		//just check state before for consistency
		assertNull(MdcHelper.liesKorrelationsId());
		assertNull(service.getKorrelationsIDLetzterAufruf());

		//make call
		service.aufrufOhneParameter();

		//assert MDC was correct during call
		assertNotNull(service.getKorrelationsIDLetzterAufruf());
		//assert MDC state after call
		assertNull(MdcHelper.liesKorrelationsId());
	}

	@Test
	public void testAufrufOhneAufrufKontext() {
		//just check state before for consistency
		assertNull(MdcHelper.liesKorrelationsId());
		assertNull(service.getKorrelationsIDLetzterAufruf());

		//make call
		service.aufrufOhneAufrufKontext(10);

		//assert MDC was correct during call
		assertNotNull(service.getKorrelationsIDLetzterAufruf());
		//assert MDC state after call
		assertNull(MdcHelper.liesKorrelationsId());
	}

	@Test
	public void testAufrufOhneKorrelationsId() {
		//just check state before for consistency
		assertNull(MdcHelper.liesKorrelationsId());
		assertNull(service.getKorrelationsIDLetzterAufruf());

		//call
		AufrufKontextTo to = new AufrufKontextTo();
		service.aufrufMitAufrufKontext(to);

		//assert MDC was correct during call
		assertNotNull(service.getKorrelationsIDLetzterAufruf());
		//assert MDC state after call
		assertNull(MdcHelper.liesKorrelationsId());
	}

	@Test
	public void testAufrufLeereKorrelationsId() {
		//just check state before for consistency
		assertNull(MdcHelper.liesKorrelationsId());
		assertNull(service.getKorrelationsIDLetzterAufruf());

		//call
		AufrufKontextTo to = new AufrufKontextTo();
		to.setKorrelationsId("");
		service.aufrufMitAufrufKontext(to);

		//assert MDC was correct during call
		String korrId = service.getKorrelationsIDLetzterAufruf();
		assertNotNull(korrId);
		assertTrue(korrId.length() > 0);
		//assert MDC state after call
		assertNull(MdcHelper.liesKorrelationsId());
	}

	@Test
	public void testAufrufMitKorrelationsId() {

		//just check state before for consistency
		assertNull(MdcHelper.liesKorrelationsId());
		assertNull(service.getKorrelationsIDLetzterAufruf());

		//prepare call
		AufrufKontextTo to = new AufrufKontextTo();
		UUID korrId = UUID.randomUUID();
		to.setKorrelationsId(korrId.toString());
		//make call
		service.aufrufMitAufrufKontext(to);

		//assert MDC was correct during call (from AufrufKontext)
		assertEquals(korrId.toString(), service.getKorrelationsIDLetzterAufruf());
		//assert MDC state after call
		assertNull(MdcHelper.liesKorrelationsId());
	}

	@Test
	public void testAufrufMitException() {
		//just check state before for consistency
		assertNull(MdcHelper.liesKorrelationsId());
		assertNull(service.getKorrelationsIDLetzterAufruf());

		//make call
		try {
			service.aufrufMitException();
			fail(); //exception should still be thrown
		} catch (Exception ignored){
		}

		//assert MDC was correct during call (from AufrufKontext)
		assertNotNull(service.getKorrelationsIDLetzterAufruf());
		//assert MDC state after call
		assertNull(MdcHelper.liesKorrelationsId());
	}

}
