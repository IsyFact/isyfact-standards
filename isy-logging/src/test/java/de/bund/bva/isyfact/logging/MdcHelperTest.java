package de.bund.bva.isyfact.logging;

/*
 * #%L
 * isy-logging
 * %%
 * 
 * %%
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * #L%
 */


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.MDC;

import de.bund.bva.isyfact.logging.util.MdcHelper;

/**
 * Testfälle des MDC-Helper.
 *
 */
public class MdcHelperTest extends AbstractLogTest {
    
    /**
     * Testet des Setzen der Korrelations-ID im MDC.
     */
    @Test
    public void testKorrelationsId() {
        
        // Korrelations-ID leeren
        MDC.remove(MdcHelper.MDC_KORRELATIONS_ID);
        
        // Korrelations-ID ist bisher nicht gesetzt und muss daher beim Entfernen und Lesen null sein.
        String korrelationsid = MdcHelper.entferneKorrelationsId();
        Assert.assertNull(korrelationsid);
        
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertNull(korrelationsid);
        
        // Korrelations-ID "1" ergänzen, lesen und entfernen
        MdcHelper.pushKorrelationsId("1");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1", korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assert.assertEquals("1", korrelationsid);
        
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertNull(korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assert.assertNull(korrelationsid);
        
        // Korrelations-ID "1", "2" und "3" ergänzen, lesen und entfernen
        MdcHelper.pushKorrelationsId("1");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1", korrelationsid);
        
        MdcHelper.pushKorrelationsId("2");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1;2", korrelationsid);
        
        MdcHelper.pushKorrelationsId("3");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1;2;3", korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assert.assertEquals("3", korrelationsid);
        
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1;2", korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assert.assertEquals("2", korrelationsid);
        
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1", korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assert.assertEquals("1", korrelationsid);
        
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertNull(korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assert.assertNull(korrelationsid);
        

    }
    
    /**
     * Testet das Entfernen aller Korrelations-Ids mit dem MDC-Helper.
     */
    @Test
    public void testKorrelationsIdAlleEntfernen() {
        
        String korrelationsid;
        
        // Korrelations-ID leeren
        MDC.remove(MdcHelper.MDC_KORRELATIONS_ID);
        
        // Korrelations-ID "1", "2" und "3" ergänzen, lesen und entfernen
        MdcHelper.pushKorrelationsId("1");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1", korrelationsid);
        
        MdcHelper.pushKorrelationsId("2");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1;2", korrelationsid);
        
        MdcHelper.pushKorrelationsId("3");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1;2;3", korrelationsid);
        
        // Mehrfach alle Ids entfernen
        
        MdcHelper.entferneKorrelationsIds();
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertNull(korrelationsid);

        MdcHelper.entferneKorrelationsIds();
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertNull(korrelationsid);
        
        MdcHelper.entferneKorrelationsIds();
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertNull(korrelationsid);
        
        // Erneut setzen: Korrelations-ID "1", "2" und "3" ergänzen, lesen und entfernen
        MdcHelper.pushKorrelationsId("1");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1", korrelationsid);
        
        MdcHelper.pushKorrelationsId("2");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1;2", korrelationsid);
        
        MdcHelper.pushKorrelationsId("3");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assert.assertEquals("1;2;3", korrelationsid);
    }
    
    /**
     * Testet das Setzen und Auslesen des MDC-Fachdaten flags.
     */
    @Test
    public void testMdcFachdaten() {
        MdcHelper.setzeMarkerFachdaten(true);
        Assert.assertEquals(true, MdcHelper.liesMarkerFachdaten());
        
        MdcHelper.setzeMarkerFachdaten(false);
        Assert.assertEquals(false, MdcHelper.liesMarkerFachdaten());
        
        MdcHelper.entferneMarkerFachdaten();
        Assert.assertEquals(null, MDC.get(MdcHelper.MDC_FACHDATEN));
    }

}
