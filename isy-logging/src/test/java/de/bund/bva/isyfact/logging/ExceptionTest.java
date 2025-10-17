package de.bund.bva.isyfact.logging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;

/**
 * Test cases for creating exceptions.
 * 
 */
public class ExceptionTest {

    /**
     * Test cast for the creation of error messages. No spring configuration is used but the "Init-Method" of the LoggerFactory.
     */
    @Test
    public void testFehlertextAufloesung() {

        try {
            throw new LogKonfigurationFehler(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK, this.getClass()
                    .getName());
        } catch (LogKonfigurationFehler e) {
            Assertions.assertEquals(
                    "Der Logger der bereitgestetllten SLF4J-Implementierung implementiert nicht das benötigte Inferface LocationAwareLogger. Bereitgestellt wurde: "
                            + this.getClass().getName(), e.getFehlertext());
        }

    }
}
