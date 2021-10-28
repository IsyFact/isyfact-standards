package de.bund.bva.isyfact.task.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import de.bund.bva.isyfact.task.AbstractTaskTest;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.isyfact.task.test.util.MemoryLogAppender;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationParameterException;
import de.bund.bva.pliscommon.konfiguration.common.konstanten.NachrichtenSchluessel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
//import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(locations = { "/spring/timertask-test.xml", "/spring/task_exception.xml" })
public class TestCompletionWatchdogLogging extends AbstractTaskTest {

    @Autowired
    private TaskMonitor taskMonitor;

    private MemoryLogAppender memoryLogAppender;

    @Before
    public void setup() throws Exception {

        // get Logger for TaskSchedulerImpl.class
        Logger logger = (Logger) LoggerFactory.getLogger(TaskSchedulerImpl.class);
        memoryLogAppender = new MemoryLogAppender();
        memoryLogAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryLogAppender);
        memoryLogAppender.start();
    }

    @After
    public void cleanUp() {
        memoryLogAppender.clearLog();
        memoryLogAppender.stop();
    }

    @Test
    public void taskMitExceptionLoggtExceptionStacktrace() throws Exception {
        when(konfiguration.getAsString("isyfact.task.taskMitException.benutzer")).thenReturn("TestUser1");
        when(konfiguration.getAsString("isyfact.task.taskMitException.passwort")).thenReturn("TestPasswort1");
        when(konfiguration.getAsString("isyfact.task.taskMitException.bhkz")).thenReturn("BHKZ1");
        when(konfiguration.getAsString("isyfact.task.taskMitException.ausfuehrung")).thenReturn("FIXED_RATE");
        when(konfiguration.getAsString("isyfact.task.taskMitException.zeitpunkt")).thenThrow(
                new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                        "isyfact.task.taskMitException.zeitpunkt"));
        when(konfiguration.getAsString(eq("isyfact.task.taskMitException.initial-delay"), anyString()))
                .thenReturn("1s");
        when(konfiguration.getAsString("isyfact.task.taskMitException.fixed-rate")).thenReturn("3s");
        when(konfiguration.getAsString("isyfact.task.taskMitException.fixed-delay")).thenThrow(
                new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                        "isyfact.task.taskMitException.fixed-delay"));

        when(konfiguration.getAsInteger(eq(KonfigurationSchluessel.WATCHDOG_RESTART_INTERVAL), anyInt()))
                .thenReturn(1);

        taskScheduler.starteKonfigurierteTasks();
        SECONDS.sleep(2);
        taskScheduler.shutdownMitTimeout(10);

        assertTrue(memoryLogAppender.containsLog("Exception von Task", Level.WARN));
        ILoggingEvent event = memoryLogAppender.findByMessageAndLevel("Exception von Task", Level.WARN).get(0);
        assertNotNull(event.getThrowableProxy());
        assertEquals(java.util.concurrent.ExecutionException.class.getName(), event.getThrowableProxy().getClassName());
    }
}