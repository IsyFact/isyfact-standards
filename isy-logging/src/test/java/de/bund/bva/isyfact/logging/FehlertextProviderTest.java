package de.bund.bva.isyfact.logging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.logging.exceptions.IsyLoggingFehlertextProvider;

/**
 * Tests for the Fehlertextprovider.
 */
public class FehlertextProviderTest {

    /**
     * Testing reading of a message without parameter.
     */
    @Test
    public void testGetMessageOhneParameter() {

        FehlertextProvider provider = new IsyLoggingFehlertextProvider();
        Assertions.assertEquals("Fehler bei der Serialisierung der Aufrufparameter.",
                provider.getMessage("ISYLO01001"));
        Assertions.assertEquals("Fehler bei der Serialisierung der Aufrufparameter.",
                provider.getMessage("ISYLO01001", new String[0]));

    }

}
