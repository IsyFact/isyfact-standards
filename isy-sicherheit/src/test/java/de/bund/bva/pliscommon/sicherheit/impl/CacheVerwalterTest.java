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
package de.bund.bva.pliscommon.sicherheit.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.Appender;
import de.bund.bva.pliscommon.sicherheit.accessmgr.test.TestAuthentifizierungErgebnis;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CacheVerwalterTest {

	private CacheVerwalter<TestAuthentifizierungErgebnis> verwalter;
	private TestAuthentifizierungErgebnis ergebnis;
	
	@Before
	public void setUp(){
		ergebnis = new TestAuthentifizierungErgebnis();
		verwalter = new CacheVerwalter<TestAuthentifizierungErgebnis>();
	}
	
	@Test
	public void testGetAnzahlElementeImCache(){
		verwalter.setCacheAktiviert(true);
		assertTrue(verwalter.isCacheAktiviert());
		verwalter.putIntoCache("key", ergebnis);
		assertEquals(1, verwalter.getAnzahlElementeImCache());
	}
	
	@Test
	public void testGetAnzahlElementeImCacheCacheAus(){
		verwalter.setCacheAktiviert(false);
		verwalter.putIntoCache("key", ergebnis);;
		assertEquals(0, verwalter.getAnzahlElementeImCache());
	}
	
	@Test
	public void testSetCacheConfiguration(){
		Logger logger = (Logger) LoggerFactory.getLogger(CacheVerwalter.class);
		logger.setLevel(Level.DEBUG);
		Appender mockAppender = mock(Appender.class);
		logger.addAppender(mockAppender);

		verwalter.setCacheKonfiguration("/resources/plis-sicherheit/ehcache/sicherheitcache.default.xml");
		verify(mockAppender, times(4)).doAppend(any());
	}
	
	@Test
	public void testSetCacheConfiguration2(){
		Logger logger = (Logger) LoggerFactory.getLogger(CacheVerwalter.class);
        logger.setLevel(Level.DEBUG);
		Appender mockAppender = mock(Appender.class);
		logger.addAppender(mockAppender);

		verwalter.setCacheKonfiguration("/resources/plis-sicherheit/ehcache/sicherheitcache.default2.xml");
		verify(mockAppender, times(4)).doAppend(any());
	}
}
