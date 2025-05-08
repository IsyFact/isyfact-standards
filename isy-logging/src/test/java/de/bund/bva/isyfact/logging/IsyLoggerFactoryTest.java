package de.bund.bva.isyfact.logging;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;

/**
 * Spezielle Tests der LoggerFactory.
 */
public class IsyLoggerFactoryTest {

    /**
     * Prüfung der korrekten Fehlermeldung, falls ein falsches Logging-Framework zum Einsatz kommt.
     * 
     * @throws Exception
     *             falls bei der Prüfung ein Fehler aufgetreten ist.
     */
    @Test
    public void falschesLoggingFrameworkTest() throws Exception {

        // Die Prüfung der Methode muss per Reflection erfolgen, da es unmöglich ist eine zweite
        // SLF4J-Implementierung in die Tests zuverlässig zu integrieren.
        Method pruefeLoggerImplementierungMethod = IsyLoggerFactory.class.getDeclaredMethod(
                "pruefeLoggerImplementierung", Object.class);
        pruefeLoggerImplementierungMethod.setAccessible(true);
        try {
            // Integer als "Logframework" zur Prüfung geben.
            pruefeLoggerImplementierungMethod.invoke(null, new Integer(5));
            Assert.fail("Erzeugung des Loggers erfolgreich, obwohl nicht "
                    + "unterstütztes Logframework verwendet wird.");
        } catch (InvocationTargetException ite) {
            LogKonfigurationFehler lkf = (LogKonfigurationFehler) ite.getCause();
            Assert.assertEquals("ISYLO00000", lkf.getAusnahmeId());
        }

    }
}
