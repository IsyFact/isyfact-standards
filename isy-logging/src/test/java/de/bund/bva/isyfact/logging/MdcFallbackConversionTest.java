package de.bund.bva.isyfact.logging;

import java.net.URL;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex;
import de.bund.bva.isyfact.logging.util.MdcHelper;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * class to test the proper Mdc formating when an object with recursive reference is used as parameter
 */
public class MdcFallbackConversionTest extends AbstractLogTest {

    /**
     * Test the correct formating of the Mdc element in the log
     *
     * @throws Exception
     *          is thrown if setting the logback configuration fails
     */
    @Test
    public void testMdcFormattierung() throws Exception {
        IsyLoggerStandard logger = IsyLoggerFactory.getLogger(this.getClass());

        // a complex object with recursive reference on its own
        TestBeanKomplex tbk = new TestBeanKomplex(true);

        // setting logback configuration with active Mdc outputs
        konfiguriereLogback("logback-MDC-test.xml");
        MdcHelper.setzeMarkerFachdaten(true);
        logger.debug("Eine Testnachricht mit vollständigen MDC und {} als Parameter.", tbk);

        // reset logback configuration
        konfiguriereLogback("logback-test.xml");

        pruefeLogdatei("testMdcFormatting");
    }

    /**
     * Helper method to configure alternative log configurations
     *
     * @param konfigDatei
     *            config file name.
     * @throws JoranException
     *             if an exception occurs reading the file.
     */
    private void konfiguriereLogback(String konfigDatei) throws JoranException {
        URL url = this.getClass().getClassLoader().getResource(konfigDatei);
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(url);
    }

}
