package de.bund.bva.isyfact.logging;

import org.junit.Assert;

import org.junit.Test;

import de.bund.bva.isyfact.logging.exceptions.IsyLoggingFehlertextProvider;
import de.bund.bva.isyfact.exception.FehlertextProvider;

/**
 * Die Tests des Fehlertextproviders.
 */
public class FehlertextProviderTest {

    /**
     * Testet das Lesen einer Nachricht ohne Parameter.
     */
    @Test
    public void testGetMessageOhneParameter() {

        FehlertextProvider provider = new IsyLoggingFehlertextProvider();
        Assert.assertEquals("Fehler bei der Serialisierung der Aufrufparameter.",
                provider.getMessage("ISYLO01001"));
        Assert.assertEquals("Fehler bei der Serialisierung der Aufrufparameter.",
                provider.getMessage("ISYLO01001", new String[0]));

    }

}
