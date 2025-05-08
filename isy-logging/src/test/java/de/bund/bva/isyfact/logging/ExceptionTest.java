package de.bund.bva.isyfact.logging;



import org.junit.Assert;

import org.junit.Test;

import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;

/**
 * Testfälle der Erzeugung von Exceptions.
 * 
 */
public class ExceptionTest {

    /**
     * Testfall zum Testen der Fehlertextauflösung. Es wird keine Spring-Konfiguration, sondern die
     * "Init-Methode" der LoggerFactory verwendet.
     */
    @Test
    public void testFehlertextAufloesung() {

        try {
            throw new LogKonfigurationFehler(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK, this.getClass()
                    .getName());
        } catch (LogKonfigurationFehler e) {
            Assert.assertEquals(
                    "Der Logger der bereitgestetllten SLF4J-Implementierung implementiert nicht das benötigte Inferface LocationAwareLogger. Bereitgestellt wurde: "
                            + this.getClass().getName(), e.getFehlertext());
        }

    }
}
