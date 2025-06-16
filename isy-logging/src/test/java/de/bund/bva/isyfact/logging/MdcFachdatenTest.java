package de.bund.bva.isyfact.logging;

import org.junit.Test;

import de.bund.bva.isyfact.logging.util.MdcHelper;



/**
 * Testfälle zum Kennzeichnen des MDCs mit Fachdaten.
 */
public class MdcFachdatenTest extends AbstractLogTest {
    
    /**
     * Tests der Logausgabe des MDC-Fachdaten flags.
     */
    @Test
    public void testMdcFachdaten() {
        IsyLoggerStandard logger = IsyLoggerFactory.getLogger(this.getClass());
        logger.debug("MdcFachdaten wurde nicht gesetzt.");
        
        MdcHelper.setzeMarkerFachdaten(true);
        logger.debug("MdcFachdaten wurde auf 'true' gesetzt.");
        
        MdcHelper.setzeMarkerFachdaten(false);
        logger.debug("MdcFachdaten wurde auf 'false' gesetzt.");
        
        MdcHelper.entferneMarkerFachdaten();
        logger.debug("MdcFachdaten wurde auf 'null' gesetzt.");
        
        pruefeLogdatei("testMdcFachdaten");
    }

}
