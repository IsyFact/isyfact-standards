package de.bund.bva.isyfact.logging;

import java.net.URL;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import de.bund.bva.isyfact.logging.util.MdcHelper;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;



/**
 * Testfälle zum Testen der verschiedenen Konfigurationsvorlagen.
 */
public class LogbackConfigTest extends AbstractLogTest {

    /**
     * Testet die Konfiguration des Parameters INCLUDE_MDC, der zu einer Aufnahme des vollständigen MDC in den
     * Kontext führt.
     * 
     * @throws Exception
     *             falls eine Exception bei der Testausführung auftritt.
     */
    @Test
    public void testMdcConfig() throws Exception {
        
        IsyLoggerStandard logger = IsyLoggerFactory.getLogger(this.getClass());

        // Laden und Test der MDC-Log-Konfiguration
        konfiguriereLogback("logback-MDC-test.xml");
        MdcHelper.setzeMarkerFachdaten(true);
        logger.debug("Eine Testnachricht mit vollständigen MDC.");
        
        // Laden Test der Batch-MDC-Log-Konfiguration
        konfiguriereLogback("logback-batch-MDC-test.xml");
        MdcHelper.setzeMarkerFachdaten(true);
        logger.debug("Eine Testnachricht mit vollständigen MDC.");
        
        // Zurücksetzen auf die Standardkonfiguration.
        konfiguriereLogback("logback-test.xml");
        
        pruefeLogdatei("testMdcConfig");
    }

    /**
     * Hilfsmethode zum Konfigurieren alternativer Log-Konfigurationsdateien.
     * 
     * @param konfigDatei
     *            die Name der zu verwendenden Datei.
     * @throws JoranException
     *             falls ein Fehler beim Einlesen der Datei auftritt.
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
