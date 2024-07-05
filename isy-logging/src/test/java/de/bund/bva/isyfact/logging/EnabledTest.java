package de.bund.bva.isyfact.logging;

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
 * License). You may not use this file except in compliance with the
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
