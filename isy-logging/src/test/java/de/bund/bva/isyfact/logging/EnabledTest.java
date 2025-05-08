package de.bund.bva.isyfact.logging;



import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import de.bund.bva.isyfact.logging.impl.IsyLocationAwareLoggerImpl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Testfälle zum Testen der 'enabled' Methoden. 
 */
public class EnabledTest extends AbstractLogTest {
    
    /**
     * Testet die 'enabled' Methoden, in dem sie nacheinander in allen Konstellationen aufgerufen werden.
     */
    @Test
    public void testEnabledErfolgreich() {
        
        Logger logger = (Logger) LoggerFactory.getLogger(EnabledTest.class);
        IsyLogger isyLogger = new IsyLocationAwareLoggerImpl(logger);
        
        logger.setLevel(Level.TRACE);
        Assert.assertTrue(isyLogger.isTraceEnabled());
        Assert.assertTrue(isyLogger.isDebugEnabled());
        Assert.assertTrue(isyLogger.isInfoEnabled());
        Assert.assertTrue(isyLogger.isWarnEnabled());
        Assert.assertTrue(isyLogger.isErrorEnabled());
        Assert.assertTrue(isyLogger.isFatalEnabled());
        
        logger.setLevel(Level.DEBUG);
        Assert.assertFalse(isyLogger.isTraceEnabled());
        Assert.assertTrue(isyLogger.isDebugEnabled());
        Assert.assertTrue(isyLogger.isInfoEnabled());
        Assert.assertTrue(isyLogger.isWarnEnabled());
        Assert.assertTrue(isyLogger.isErrorEnabled());
        Assert.assertTrue(isyLogger.isFatalEnabled());
        
        logger.setLevel(Level.INFO);
        Assert.assertFalse(isyLogger.isTraceEnabled());
        Assert.assertFalse(isyLogger.isDebugEnabled());
        Assert.assertTrue(isyLogger.isInfoEnabled());
        Assert.assertTrue(isyLogger.isWarnEnabled());
        Assert.assertTrue(isyLogger.isErrorEnabled());
        Assert.assertTrue(isyLogger.isFatalEnabled());
        
        logger.setLevel(Level.WARN);
        Assert.assertFalse(isyLogger.isTraceEnabled());
        Assert.assertFalse(isyLogger.isDebugEnabled());
        Assert.assertFalse(isyLogger.isInfoEnabled());
        Assert.assertTrue(isyLogger.isWarnEnabled());
        Assert.assertTrue(isyLogger.isErrorEnabled());
        Assert.assertTrue(isyLogger.isFatalEnabled());
        
        logger.setLevel(Level.ERROR);
        Assert.assertFalse(isyLogger.isTraceEnabled());
        Assert.assertFalse(isyLogger.isDebugEnabled());
        Assert.assertFalse(isyLogger.isInfoEnabled());
        Assert.assertFalse(isyLogger.isWarnEnabled());
        Assert.assertTrue(isyLogger.isErrorEnabled());
        Assert.assertTrue(isyLogger.isFatalEnabled());
        
    }

}
