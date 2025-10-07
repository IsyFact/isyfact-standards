package de.bund.bva.isyfact.logging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import de.bund.bva.isyfact.logging.impl.IsyLocationAwareLoggerImpl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Test cases for the 'enabled' methods.
 */
public class EnabledTest extends AbstractLogTest {
    
    /**
     * Testing the 'enabled' methods, by calling them one by one in all constellations
     */
    @Test
    public void testEnabledErfolgreich() {
        
        Logger logger = (Logger) LoggerFactory.getLogger(EnabledTest.class);
        IsyLogger isyLogger = new IsyLocationAwareLoggerImpl(logger);
        
        logger.setLevel(Level.TRACE);
        Assertions.assertTrue(isyLogger.isTraceEnabled());
        Assertions.assertTrue(isyLogger.isDebugEnabled());
        Assertions.assertTrue(isyLogger.isInfoEnabled());
        Assertions.assertTrue(isyLogger.isWarnEnabled());
        Assertions.assertTrue(isyLogger.isErrorEnabled());
        Assertions.assertTrue(isyLogger.isFatalEnabled());
        
        logger.setLevel(Level.DEBUG);
        Assertions.assertFalse(isyLogger.isTraceEnabled());
        Assertions.assertTrue(isyLogger.isDebugEnabled());
        Assertions.assertTrue(isyLogger.isInfoEnabled());
        Assertions.assertTrue(isyLogger.isWarnEnabled());
        Assertions.assertTrue(isyLogger.isErrorEnabled());
        Assertions.assertTrue(isyLogger.isFatalEnabled());
        
        logger.setLevel(Level.INFO);
        Assertions.assertFalse(isyLogger.isTraceEnabled());
        Assertions.assertFalse(isyLogger.isDebugEnabled());
        Assertions.assertTrue(isyLogger.isInfoEnabled());
        Assertions.assertTrue(isyLogger.isWarnEnabled());
        Assertions.assertTrue(isyLogger.isErrorEnabled());
        Assertions.assertTrue(isyLogger.isFatalEnabled());
        
        logger.setLevel(Level.WARN);
        Assertions.assertFalse(isyLogger.isTraceEnabled());
        Assertions.assertFalse(isyLogger.isDebugEnabled());
        Assertions.assertFalse(isyLogger.isInfoEnabled());
        Assertions.assertTrue(isyLogger.isWarnEnabled());
        Assertions.assertTrue(isyLogger.isErrorEnabled());
        Assertions.assertTrue(isyLogger.isFatalEnabled());
        
        logger.setLevel(Level.ERROR);
        Assertions.assertFalse(isyLogger.isTraceEnabled());
        Assertions.assertFalse(isyLogger.isDebugEnabled());
        Assertions.assertFalse(isyLogger.isInfoEnabled());
        Assertions.assertFalse(isyLogger.isWarnEnabled());
        Assertions.assertTrue(isyLogger.isErrorEnabled());
        Assertions.assertTrue(isyLogger.isFatalEnabled());
        
    }

}
