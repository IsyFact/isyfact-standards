package de.bund.bva.isyfact.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;

/**
 * Special tests for the LoggerFactory.
 */
public class IsyLoggerFactoryTest {

    /**
     * Testing the correct error message if a wrong logging framework is used
     * 
     * @throws Exception
     *             if an error occurs in the test.
     */
    @Test
    public void falschesLoggingFrameworkTest() throws Exception {

        // testing the method must be done by reflection because it is impossible
        // to integrate a second SLF4J implemention in the tests.
        Method pruefeLoggerImplementierungMethod = IsyLoggerFactory.class.getDeclaredMethod(
                "pruefeLoggerImplementierung", Object.class);
        pruefeLoggerImplementierungMethod.setAccessible(true);
        try {
            // using Integer as "Logframework"
            pruefeLoggerImplementierungMethod.invoke(null, new Integer(5));
            Assertions.fail("Erzeugung des Loggers erfolgreich, obwohl nicht "
                    + "unterstütztes Logframework verwendet wird.");
        } catch (InvocationTargetException ite) {
            LogKonfigurationFehler lkf = (LogKonfigurationFehler) ite.getCause();
            Assertions.assertEquals("ISYLO00000", lkf.getAusnahmeId());
        }

    }
}
