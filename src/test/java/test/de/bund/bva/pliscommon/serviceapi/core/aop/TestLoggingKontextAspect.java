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
package test.de.bund.bva.pliscommon.serviceapi.core.aop;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;

import de.bund.bva.pliscommon.serviceapi.core.aop.LoggingKontextAspect;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import test.de.bund.bva.pliscommon.serviceapi.core.aop.test.LoggingKontextAspectService;

public class TestLoggingKontextAspect {

	private Appender mockAppender;
	private ArgumentCaptor<LoggingEvent> captorLoggingEvent;
	private LoggingKontextAspectService service;
	
	@Before
	public void setUp(){
		captorLoggingEvent = ArgumentCaptor.forClass(LoggingEvent.class);

		Logger logger = (Logger) LoggerFactory.getLogger("de.bund.bva.pliscommon.serviceapi.core.aop.LoggingKontextAspect");
		mockAppender = mock(Appender.class);
        when(mockAppender.getName()).thenReturn("MOCK");

		logger.addAppender(mockAppender);

		LoggingKontextAspect aspect = new LoggingKontextAspect();
		LoggingKontextAspectService service = new LoggingKontextAspectService();
		ProxyFactory proxyFactory = new ProxyFactory(service);
		proxyFactory.addAdvice(aspect);
		this.service = (LoggingKontextAspectService) proxyFactory.getProxy();
	}
	
	@Test
	public void testAufrufOhneParameter() {		
		service.aufrufOhneParameter();		
		verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
		assertEquals("Es wurde kein AufrufKontext übermittelt. Erzeuge neue Korrelations-ID.",
				captorLoggingEvent.getAllValues().get(0).getMessage());
	}
	
	@Test
	public void testAufrufOhneAufrufKontext() {		
		service.aufrufOhneAufrufKontext(10);		
		verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
		assertEquals("Es wurde kein AufrufKontext übermittelt. Erzeuge neue Korrelations-ID.",
				captorLoggingEvent.getAllValues().get(0).getMessage());
	}
	
	@Test
	public void testAufrufOhneKorrelationsId() {		
		AufrufKontextTo to = new AufrufKontextTo();
		service.aufrufMitAufrufKontext(to);		
		verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
		assertEquals("Es wurde keine Korrelations-ID übermittelt. Erzeuge neue Korrelations-ID",
				captorLoggingEvent.getAllValues().get(0).getMessage());
	}
	
	@Test
	public void testAufrufLeereKorrelationsId() {		
		AufrufKontextTo to = new AufrufKontextTo();
		to.setKorrelationsId("");
		service.aufrufMitAufrufKontext(to);		
		verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
		assertEquals("Es wurde keine Korrelations-ID übermittelt. Erzeuge neue Korrelations-ID",
				captorLoggingEvent.getAllValues().get(0).getMessage());
	}
	
	@Test
	public void testAufrufMitKorrelationsId() {		
		AufrufKontextTo to = new AufrufKontextTo();
		UUID korrId = UUID.randomUUID();
		to.setKorrelationsId(korrId.toString());
		service.aufrufMitAufrufKontext(to);		
		verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
		assertEquals("Setzte Korrelations-ID aus AufrufKontext.",
				captorLoggingEvent.getAllValues().get(0).getMessage());
	}
	
	@Test(expected = Exception.class)
	public void testAufrufMitException() throws Exception {
		service.aufrufMitException();
	}

}
