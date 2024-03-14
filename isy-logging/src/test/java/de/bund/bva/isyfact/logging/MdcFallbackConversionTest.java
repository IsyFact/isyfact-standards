package de.bund.bva.isyfact.logging;

import java.net.URL;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex;
import de.bund.bva.isyfact.logging.util.MdcHelper;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Klasse zum Testen der richtigen Mdc Formattierung, wenn als Parameter ein Objekt mit rekusiever Referenz übergeben wird.
 */
public class MdcFallbackConversionTest extends AbstractLogTest {

    /**
     * Testet die korrekte Formatierung des Mdc Elements in der Log Ausgabe.
     *
     * @throws Exception
     *          wird geworfen wenn das Setzen der Logback Konfiguration scheitert.
     */
    @Test
    public void testMdcFormattierung() throws Exception {
        IsyLoggerStandard logger = IsyLoggerFactory.getLogger(this.getClass());

        // Ein komplexes Objekt mit rekusiver Referenz auf sich selbst.
        TestBeanKomplex tbk = new TestBeanKomplex(true);

        // Setze Logback Konfiguration mit eingeschalteten Mdc Ausgaben.
        konfiguriereLogback("logback-MDC-test.xml");
        MdcHelper.setzeMarkerFachdaten(true);
        logger.debug("Eine Testnachricht mit vollständigen MDC und {} als Parameter.", tbk);

        // Setze Logback Konfiguration zurück.
        konfiguriereLogback("logback-test.xml");

        pruefeLogdatei("testMdcFormatting");
    }

    /**
     * Hilfsmethode zum Konfigurieren alternativer Log-Konfigurationsdateien.
     *
     * @param konfigDatei
     *            die Name der zu verwendenden Datei.
     * @throws JoranException
     *             falls ein Fehler beim Einlesen der Datei auftritt.
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
