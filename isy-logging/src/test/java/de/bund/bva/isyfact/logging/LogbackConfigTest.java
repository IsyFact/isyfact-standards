package de.bund.bva.isyfact.logging;

import java.net.URL;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import de.bund.bva.isyfact.logging.util.MdcHelper;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

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
