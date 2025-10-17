package de.bund.bva.isyfact.logging;

import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.logging.util.MdcHelper;


/**
 * Test cases to mark a MDCs with business data.
 */
public class MdcFachdatenTest extends AbstractLogTest {
    
    /**
     * Testing the log entries with 'MDC-Fachdaten' flags.
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
