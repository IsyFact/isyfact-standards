package de.bund.bva.isyfact.log4jbridge;

/*
 * #%L
 * isy-logging-log4j-bridge
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
import java.io.Serializable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * LoggerAdapter auf log4j. Dieser Adapter muss zum Einsatz in isy-logging das Interface LocationAwareAdapter
 * implementieren, da isy-logging dadurch bereitgestellte Methode "log" nutzt.
 */
public class Log4jLoggerAdapter extends MarkerIgnoringBase implements LocationAwareLogger, Serializable {

    /** Eindeutige UID. */
    private static final long serialVersionUID = -754487775175120500L;

    /** Full-Qualified-Class-Name des Adapters. */
    public static String FQCN = Log4jLoggerAdapter.class.getName();

    /** Der log4j-Logger an die die Aufrufe delegiert werden. */
    private transient Logger logger;

    /**
     * Konstruktor der Klasse. Initialisiert die übergebenenen Properties.
     * 
     * @param log4jLogger
     *            der zu verwendende Logger.
     */
    public Log4jLoggerAdapter(Logger log4jLogger) {
        logger = log4jLogger;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#getName()
     */
    @Override
    public String getName() {
        return logger.getName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(java.lang.String)
     */
    @Override
    public void debug(String msg) {
        log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, null, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object)
     */
    @Override
    public void debug(String format, Object arg) {
        logIntern(FQCN, LocationAwareLogger.DEBUG_INT, format, arg, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void debug(String format, Object arg1, Object arg2) {
        logIntern(FQCN, LocationAwareLogger.DEBUG_INT, format, arg1, arg2, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug(String format, Object... arguments) {
        log(null, FQCN, LocationAwareLogger.DEBUG_INT, format, arguments, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void debug(String msg, Throwable t) {
        log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, null, t);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(java.lang.String)
     */
    @Override
    public void info(String msg) {
        log(null, FQCN, LocationAwareLogger.INFO_INT, msg, null, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object)
     */
    @Override
    public void info(String format, Object arg) {
        logIntern(FQCN, LocationAwareLogger.INFO_INT, format, arg, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void info(String format, Object arg1, Object arg2) {
        logIntern(FQCN, LocationAwareLogger.INFO_INT, format, arg1, arg2, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object[])
     */
    @Override
    public void info(String format, Object... arguments) {
        log(null, FQCN, LocationAwareLogger.INFO_INT, format, arguments, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void info(String msg, Throwable t) {
        log(null, FQCN, LocationAwareLogger.INFO_INT, msg, null, t);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(java.lang.String)
     */
    @Override
    public void trace(String msg) {
        log(null, FQCN, LocationAwareLogger.TRACE_INT, msg, null, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object)
     */
    @Override
    public void trace(String format, Object arg) {
        logIntern(FQCN, LocationAwareLogger.TRACE_INT, format, arg, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logIntern(FQCN, LocationAwareLogger.TRACE_INT, format, arg1, arg2, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object[])
     */
    @Override
    public void trace(String format, Object... arguments) {
        log(null, FQCN, LocationAwareLogger.TRACE_INT, format, arguments, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void trace(String msg, Throwable t) {
        log(null, FQCN, LocationAwareLogger.TRACE_INT, msg, null, t);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(Level.WARN);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(java.lang.String)
     */
    @Override
    public void warn(String msg) {
        log(null, FQCN, LocationAwareLogger.WARN_INT, msg, null, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object)
     */
    @Override
    public void warn(String format, Object arg) {
        logIntern(FQCN, LocationAwareLogger.WARN_INT, format, arg, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object[])
     */
    @Override
    public void warn(String format, Object... arguments) {
        log(null, FQCN, LocationAwareLogger.WARN_INT, format, arguments, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logIntern(FQCN, LocationAwareLogger.WARN_INT, format, arg1, arg2, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void warn(String msg, Throwable t) {
        log(null, FQCN, LocationAwareLogger.WARN_INT, msg, null, t);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Level.ERROR);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(java.lang.String)
     */
    @Override
    public void error(String msg) {
        log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object)
     */
    @Override
    public void error(String format, Object arg) {
        logIntern(FQCN, LocationAwareLogger.ERROR_INT, format, arg, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void error(String format, Object arg1, Object arg2) {
        logIntern(FQCN, LocationAwareLogger.ERROR_INT, format, arg1, arg2, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object[])
     */
    @Override
    public void error(String format, Object... arguments) {
        log(null, FQCN, LocationAwareLogger.ERROR_INT, format, arguments, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void error(String msg, Throwable t) {
        log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, t);
    }

    /**
     * Hilfsmethode zum Erstellen von Logeinträgen.
     * 
     * @param fqcn
     *            Full-Qualified-Class-Name des Loggers.
     * @param level
     *            das Log-level.
     * @param message
     *            die Log-Nachricht.
     * @param arg
     *            Wert für einen Platzhalter in der Lognachricht.
     * @param t
     *            zu loggende Exception.
     */
    private void logIntern(String fqcn, int level, String message, Object arg, Throwable t) {
        Object[] argArray = new Object[] { arg };
        log(null, fqcn, level, message, argArray, t);
    }

    /**
     * Hilfsmethode zum Erstellen von Logeinträgen.
     * 
     * @param fqcn
     *            Full-Qualified-Class-Name des Loggers.
     * @param level
     *            das Log-level.
     * @param message
     *            die Log-Nachricht.
     * @param arg1
     *            Wert für den ersten Platzhalter in der Lognachricht.
     * @param arg2
     *            Wert für den zweiten Platzhalter in der Lognachricht.
     * @param t
     *            zu loggende Exception.
     */
    private void logIntern(String fqcn, int level, String message, Object arg1, Object arg2, Throwable t) {
        Object[] argArray = new Object[] {arg1, arg2};
        log(null, fqcn, level, message, argArray, t);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.LocationAwareLogger#log(org.slf4j.Marker, java.lang.String, int, java.lang.String,
     *      java.lang.Object[], java.lang.Throwable)
     */
    @Override
    public void log(Marker marker, String fqcn, int level, String message, Object[] argArray, Throwable t) {

        // Aufbereiten der Nachricht, da log4j keine Platzhalter unterstützt.
        String nachrichtAufbereitet = message;
        if (argArray != null && argArray.length > 0) {
            FormattingTuple ft = MessageFormatter.arrayFormat(message, argArray);
            nachrichtAufbereitet = ft.getMessage();
        }

        logger.log(fqcn, ermittleLog4jLevel(level), nachrichtAufbereitet, t);
    }

    /**
     * Hilfsmethode zum ermitteln des log4j-Log-levels aus dem slf4j-Loglevel.
     * 
     * @param levelInt
     *            das log4j-Loglevel
     * @return das slf4j-Loglevel.
     */
    private Level ermittleLog4jLevel(int levelInt) {
        switch (levelInt) {
        case LocationAwareLogger.TRACE_INT:
            return Level.TRACE;
        case LocationAwareLogger.DEBUG_INT:
            return Level.DEBUG;
        case LocationAwareLogger.INFO_INT:
            return Level.INFO;
        case LocationAwareLogger.WARN_INT:
            return Level.WARN;
        case LocationAwareLogger.ERROR_INT:
            return Level.ERROR;
        default:
            throw new IllegalStateException("Unbekannter Level-Integer: " + levelInt);
        }
    }

}
