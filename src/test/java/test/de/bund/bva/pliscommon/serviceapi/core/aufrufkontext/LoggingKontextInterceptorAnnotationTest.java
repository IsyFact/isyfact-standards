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
package test.de.bund.bva.pliscommon.serviceapi.core.aufrufkontext;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import test.de.bund.bva.pliscommon.serviceapi.core.aop.test.LoggingKontextSstTestBean;

/**
 * Testet die Funktionalität von {@link StelltAufrufKontextBereitInterceptor}.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/loggingKontextInterceptorTest.xml" })
public class LoggingKontextInterceptorAnnotationTest {
    
    /** Simulation einer Service-Schnittstelle, auf die Aufrufe getätigt werden. */
    @Autowired
    public LoggingKontextSstTestBean sst;
    
    private AufrufKontextTo aufrufKontext;

    /**
     * setzt den AufrufKontext vor jedem Test zurück.
     */
    @Before
    public void setUp() {
    	aufrufKontext = new AufrufKontextTo();
    	aufrufKontext.setKorrelationsId(UUID.randomUUID().toString());
    }

    @After
    public void tearDown() {
    	aufrufKontext = null;
    }

    @Test
    public void stelltAufrufKontextNichtBereitOhneParameter() {
        String korrelationsId = sst.stelltAufrufKontextNichtBereitOhneParameter();
        assertEquals(null, korrelationsId);
    }

    @Test
    public void stelltAufrufKontextNichtBereitMitParameter() {
    	aufrufKontext.setKorrelationsId(null);
    	String korrelationsId = sst.stelltAufrufKontextNichtBereitMitParameter(aufrufKontext);
        assertNull(aufrufKontext.getKorrelationsId());
        assertEquals(null, korrelationsId);
    }
    
    @Test
    public void stelltAufrufKontextBereitOhneParameter() {
    	String korrelationsId = sst.stelltAufrufKontextBereitOhneParameter();
    	assertNotNull(korrelationsId);
    	assertNotEquals("", korrelationsId);
    }

    @Test
    public void stelltAufrufKontextBereitMitKorrelation() {
    	String korrelationsId = sst.stelltAufrufKontextBereitMitParameter(aufrufKontext);
    	assertNotNull(korrelationsId);
    	assertEquals(aufrufKontext.getKorrelationsId(), korrelationsId);
    }
    
    @Test
    public void stelltAufrufKontextBereitOhneKorrelation() {
    	aufrufKontext.setKorrelationsId(null);
    	String korrelationsId = sst.stelltAufrufKontextBereitMitParameter(aufrufKontext);
        assertNotNull(aufrufKontext.getKorrelationsId());
        assertNotNull(korrelationsId);
    	assertEquals(aufrufKontext.getKorrelationsId(), korrelationsId);
    }
}
