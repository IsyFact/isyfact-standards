package de.bund.bva.isyfact.logging.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyMarker;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach;
import de.bund.bva.isyfact.logging.impl.IsyLocationAwareLoggerImpl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class LogHelperActiveLogLevelTest {

    private ListAppender<ILoggingEvent> appender;
    Logger logger;
    IsyLogger isyLogger;
    LogHelper logHelper;

    @Before
    public void setUp() {
        logHelper = new LogHelper(
                true,
                true,
                true,
                true,
                true,
                0L
        );

        appender = new ListAppender<>();
        appender.start();
        logger = (Logger) LoggerFactory.getLogger(LogHelperActiveLogLevelTest.class);
        logger.addAppender(appender);

        isyLogger = Mockito.spy(new IsyLocationAwareLoggerImpl(logger));
    }

    @After
    public void tearDown() {
        logger.detachAppender(appender);
        appender.list.clear();
        appender.stop();
    }

    @Test
    public void testLoggeAufrufCheckINFOLogLevel() throws NoSuchMethodException {
        // log with level OFF, expect 0 additional log entrys
        logger.setLevel(Level.OFF);
        assertThat(appender.list).isEmpty();
        logHelper.loggeAufruf(isyLogger, TestBeanEinfach.class.getMethod("setEinString", String.class));
        verify(isyLogger, never()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class)
        );
        assertThat(appender.list).isEmpty();

        // log with level INFO, expect 1 additional log entry
        logger.setLevel(Level.INFO);
        logHelper.loggeAufruf(isyLogger, TestBeanEinfach.class.getMethod("setEinString", String.class));
        verify(isyLogger, atLeastOnce()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class)
        );
        // expect INFO Level
        assertThat(appender.list)
                .hasSize(1)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.INFO);
    }

    @Test
    public void testLoggeErgebnisCheckINFOLogLevel() throws NoSuchMethodException {
        // log with level OFF, expect 0 additional log entry
        logger.setLevel(Level.OFF);
        assertThat(appender.list).isEmpty();
        logHelper.loggeErgebnis(
                isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                false,
                null,
                "Ergebnis"
        );
        verify(isyLogger, never()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class)
        );
        assertThat(appender.list).isEmpty();

        // log with level INFO, expect 1 additional log entry
        logger.setLevel(Level.INFO);
        logHelper.loggeErgebnis(
                isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                false,
                null,
                "Ergebnis"
        );
        verify(isyLogger, atLeastOnce()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class)
        );
        // expect INFO Level
        assertThat(appender.list)
                .hasSize(1)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.INFO);
    }

    @Test
    public void testLoggeErgebnisCheckDEBUGLogLevel() throws NoSuchMethodException {
        // log with level OFF, expect 0 additional log entry
        logger.setLevel(Level.OFF);
        assertThat(appender.list).isEmpty();
        logHelper.loggeErgebnis(
                isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                false,
                null,
                "Ergebnis"
        );
        verify(isyLogger, never()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class)
        );
        assertThat(appender.list).isEmpty();

        // log with level DEBUG, expect 2 additional log entrys
        logger.setLevel(Level.DEBUG);
        logHelper.loggeErgebnis(
                isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                false,
                null,
                "Ergebnis"
        );
        verify(isyLogger, atLeastOnce()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class)
        );
        // expect INFO and DEBUG Level
        assertThat(appender.list)
                .hasSize(2)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.INFO, Level.DEBUG);
    }

    @Test
    public void testLoggeDauerCheckINFOLogLevel() throws NoSuchMethodException {
        // log with level OFF, expect 0 additional log entrys
        logger.setLevel(Level.OFF);
        assertThat(appender.list).isEmpty();
        logHelper.loggeDauer(
                isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                0L,
                false
        );
        verify(isyLogger, never()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class),
                any(IsyMarker.class)
        );
        assertThat(appender.list).isEmpty();

        // log with level INFO, expect 1 additional log entry
        logger.setLevel(Level.INFO);
        logHelper.loggeDauer(isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                0L,
                false
        );
        verify(isyLogger, atLeastOnce()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class),
                any(IsyMarker.class)
        );
        // expect INFO Level
        assertThat(appender.list)
                .hasSize(1)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.INFO);

    }

    @Test
    public void testLoggeNachbarsystemAufrufCheckINFOLogLevel() throws NoSuchMethodException {
        // log with level OFF, expect 0 additional log entrys
        logger.setLevel(Level.OFF);
        assertThat(appender.list).isEmpty();
        logHelper.loggeNachbarsystemAufruf(
                isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                "EinNachbarsystemName",
                "http://ein.nachbarsystem.name/"
        );
        verify(isyLogger, never()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class)
        );
        assertThat(appender.list).isEmpty();

        // log with level INFO, expect 1 additional log entry
        logger.setLevel(Level.INFO);
        logHelper.loggeNachbarsystemAufruf(isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                "EinNachbarsystemName",
                "http://ein.nachbarsystem.name/"
        );
        verify(isyLogger, atLeastOnce()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class)
        );
        // expect INFO Level
        assertThat(appender.list)
                .hasSize(1)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.INFO);
    }

    @Test
    public void testLoggeNachbarsystemErgebnisCheckINFOLogLevel() throws NoSuchMethodException {
        // log with level OFF, expect 0 additional log entrys
        logger.setLevel(Level.OFF);
        assertThat(appender.list).isEmpty();
        logHelper.loggeNachbarsystemErgebnis(
                isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                "EinNachbarsystemName",
                "http://ein.nachbarsystem.name/",
                false
        );
        verify(isyLogger, never()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class)
        );
        assertThat(appender.list).isEmpty();

        // log with level INFO, expect 1 additional log entry
        logger.setLevel(Level.INFO);
        logHelper.loggeNachbarsystemErgebnis(isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                "EinNachbarsystemName",
                "http://ein.nachbarsystem.name/",
                false
        );
        verify(isyLogger, atLeastOnce()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class)
        );
        // expect INFO Level
        assertThat(appender.list)
                .hasSize(1)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.INFO);
    }

    @Test
    public void testLoggeNachbarsystemDauerCheckINFOLogLevel() throws NoSuchMethodException {
        // log with level OFF, expect 0 additional log entrys
        logger.setLevel(Level.OFF);
        assertThat(appender.list).isEmpty();
        logHelper.loggeNachbarsystemDauer(
                isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                0L,
                "EinNachbarsystemName",
                "http://ein.nachbarsystem.name/",
                false
        );
        verify(isyLogger, never()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class),
                any(IsyMarker.class)
        );
        assertThat(appender.list).isEmpty();

        // log with level INFO, expect 1 additional log entry
        logger.setLevel(Level.INFO);
        logHelper.loggeNachbarsystemDauer(isyLogger,
                TestBeanEinfach.class.getMethod("setEinString", String.class),
                0L,
                "EinNachbarsystemName",
                "http://ein.nachbarsystem.name/",
                false
        );
        verify(isyLogger, atLeastOnce()).info(
                any(LogKategorie.class),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(IsyMarker.class),
                any(IsyMarker.class)
        );
        // expect INFO Level
        assertThat(appender.list)
                .hasSize(1)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.INFO);
    }
}
