package de.bund.bva.isyfact.logging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import de.bund.bva.isyfact.logging.util.MdcHelper;

/**
 * Test cases of the MDC-Helper.
 *
 */
public class MdcHelperTest extends AbstractLogTest {
    
    /**
     * Testing to set the correlation-ID in the MDC.
     */
    @Test
    public void testKorrelationsId() {
        
        // remove correlation-ID
        MDC.remove(MdcHelper.MDC_KORRELATIONS_ID);
        
        // correlation-ID has not been set and therefore must
        // be null when removing and reading.
        String korrelationsid = MdcHelper.entferneKorrelationsId();
        Assertions.assertNull(korrelationsid);
        
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertNull(korrelationsid);
        
        // add correlation-ID "1" , read and remove
        MdcHelper.pushKorrelationsId("1");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1", korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assertions.assertEquals("1", korrelationsid);
        
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertNull(korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assertions.assertNull(korrelationsid);
        
        // add correlation-ID "1", "2" and "3" , read and remove
        MdcHelper.pushKorrelationsId("1");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1", korrelationsid);
        
        MdcHelper.pushKorrelationsId("2");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1;2", korrelationsid);
        
        MdcHelper.pushKorrelationsId("3");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1;2;3", korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assertions.assertEquals("3", korrelationsid);
        
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1;2", korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assertions.assertEquals("2", korrelationsid);
        
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1", korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assertions.assertEquals("1", korrelationsid);
        
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertNull(korrelationsid);
        
        korrelationsid = MdcHelper.entferneKorrelationsId();
        Assertions.assertNull(korrelationsid);
        

    }
    
    /**
     * Testing the removing of all correlation-ids with MDC-Helper.
     */
    @Test
    public void testKorrelationsIdAlleEntfernen() {
        
        String korrelationsid;
        
        // remove correlation ID
        MDC.remove(MdcHelper.MDC_KORRELATIONS_ID);
        
        // add correlation-ID "1", "2" and "3" , read and remove
        MdcHelper.pushKorrelationsId("1");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1", korrelationsid);
        
        MdcHelper.pushKorrelationsId("2");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1;2", korrelationsid);
        
        MdcHelper.pushKorrelationsId("3");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1;2;3", korrelationsid);
        
        // remove all ids multiple times
        
        MdcHelper.entferneKorrelationsIds();
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertNull(korrelationsid);

        MdcHelper.entferneKorrelationsIds();
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertNull(korrelationsid);
        
        MdcHelper.entferneKorrelationsIds();
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertNull(korrelationsid);
        
        // Again adding correlation-ID "1", "2" and "3" , read and remove
        MdcHelper.pushKorrelationsId("1");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1", korrelationsid);
        
        MdcHelper.pushKorrelationsId("2");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1;2", korrelationsid);
        
        MdcHelper.pushKorrelationsId("3");
        korrelationsid = MdcHelper.liesKorrelationsId();
        Assertions.assertEquals("1;2;3", korrelationsid);
    }
    
    /**
     * Testing setting and reading 'MDC-Fachdaten' flags.
     */
    @Test
    public void testMdcFachdaten() {
        MdcHelper.setzeMarkerFachdaten(true);
        Assertions.assertEquals(true, MdcHelper.liesMarkerFachdaten());
        
        MdcHelper.setzeMarkerFachdaten(false);
        Assertions.assertEquals(false, MdcHelper.liesMarkerFachdaten());
        
        MdcHelper.entferneMarkerFachdaten();
        Assertions.assertEquals(null, MDC.get(MdcHelper.MDC_FACHDATEN));
    }

}
