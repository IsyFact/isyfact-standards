package de.bund.bva.isyfact.logging;

import java.net.URL;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import de.bund.bva.isyfact.logging.util.MdcHelper;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;


/**
 * test cases to test different configuration settings
 */
public class LogbackConfigTest extends AbstractLogTest {

    /**
     * testing configuration of parameter INCLUDE_MDC, which leads to the intake of
     * the complete MDC in the context
     *
     * @throws Exception
     *             if an exception is thrown during the test.
     */
    @Test
    public void testMdcConfig() throws Exception {
        
        IsyLoggerStandard logger = IsyLoggerFactory.getLogger(this.getClass());

        // load and test the MDC-Log-configuration
        konfiguriereLogback("logback-MDC-test.xml");
        MdcHelper.setzeMarkerFachdaten(true);
        logger.debug("Eine Testnachricht mit vollständigen MDC.");
        
        // test for loading the Batch-MDC-Log-configuration
        konfiguriereLogback("logback-batch-MDC-test.xml");
        MdcHelper.setzeMarkerFachdaten(true);
        logger.debug("Eine Testnachricht mit vollständigen MDC.");
        
        // rollback to standard configuration.
        konfiguriereLogback("logback-test.xml");
        
        pruefeLogdatei("testMdcConfig");
    }

    /**
     * helper method to configure alternativ logging configurations
     * 
     * @param konfigDatei
     *            name of the configuration file to be used.
     * @throws JoranException
     *             if an exceptions occurs reading the file.
     */
    public void konfiguriereLogback(String konfigDatei) throws JoranException {
        URL url = this.getClass().getClassLoader().getResource(konfigDatei);
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(url);
    }

}
