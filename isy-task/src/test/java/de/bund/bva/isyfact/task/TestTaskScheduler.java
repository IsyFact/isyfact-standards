package de.bund.bva.isyfact.task;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.isyfact.task.model.impl.TaskRunnerImpl;
import de.bund.bva.isyfact.task.sicherheit.impl.NoOpAuthenticator;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationParameterException;
import de.bund.bva.pliscommon.konfiguration.common.konstanten.NachrichtenSchluessel;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(locations = { "/spring/timertask-test.xml", "/spring/tasks1_2_3.xml" })
public class TestTaskScheduler extends AbstractTaskTest {
    @Test
    public void testSchedule() throws Exception {
        when(konfiguration.getAsString("isyfact.task.taskTest1.benutzer")).thenReturn("TestUser1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.passwort")).thenReturn("TestPasswort1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.ausfuehrung")).thenReturn("ONCE");
        String dateTimePattern = konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN,
            KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
        String executionDateTime1 = DateTimeUtil.localDateTimeNow().plusSeconds(5).format(dateTimeFormatter);
        when(konfiguration.getAsString("isyfact.task.taskTest1.zeitpunkt")).thenReturn(executionDateTime1);
        when(konfiguration.getAsString(eq("isyfact.task.taskTest1.initial-delay"), anyString()))
            .thenReturn("0s");
        when(konfiguration.getAsString("isyfact.task.taskTest1.fixed-rate")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest1.fixed-rate"));
        when(konfiguration.getAsString("isyfact.task.taskTest1.fixed-delay")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest1.fixed-delay"));

        when(konfiguration.getAsString("isyfact.task.taskTest2.benutzer")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest2.benutzer"));
        when(konfiguration.getAsString("isyfact.task.taskTest2.ausfuehrung")).thenReturn("FIXED_RATE");
        when(konfiguration.getAsString("isyfact.task.taskTest2.zeitpunkt")).thenThrow(new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
            "isyfact.task.taskTest2.zeitpunkt"));
        when(konfiguration.getAsString(eq("isyfact.task.taskTest2.initial-delay"), anyString()))
            .thenReturn("5s");
        when(konfiguration.getAsString("isyfact.task.taskTest2.fixed-rate")).thenReturn("2s");
        when(konfiguration.getAsString("isyfact.task.taskTest2.fixed-delay")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest2.fixed-delay"));

        when(konfiguration.getAsString("isyfact.task.taskTest3.benutzer")).thenReturn("TestUser3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.passwort")).thenReturn("TestPasswort3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.ausfuehrung")).thenReturn("FIXED_DELAY");
        when(konfiguration.getAsString("isyfact.task.taskTest3.zeitpunkt")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest3.zeitpunkt"));
        when(konfiguration.getAsString(eq("isyfact.task.taskTest3.initial-delay"), anyString()))
            .thenReturn("5s");
        when(konfiguration.getAsString("isyfact.task.taskTest3.fixed-rate")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest3.fixed-rate"));
        when(konfiguration.getAsString("isyfact.task.taskTest3.fixed-delay"))
            .thenReturn("3s");

        when(konfiguration.getAsString("isyfact.task.taskTestOnceInitialDelay.benutzer"))
            .thenReturn("TestUser1");
        when(konfiguration.getAsString("isyfact.task.taskTestOnceInitialDelay.passwort"))
            .thenReturn("TestPasswort1");
        when(konfiguration.getAsString("isyfact.task.taskTestOnceInitialDelay.ausfuehrung"))
            .thenReturn("ONCE");
        when(konfiguration.getAsString("isyfact.task.taskTestOnceInitialDelay.zeitpunkt")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTestOnceInitialDelay.zeitpunkt"));
        when(
            konfiguration.getAsString(eq("isyfact.task.taskTestOnceInitialDelay.initial-delay"), anyString()))
            .thenReturn("10s");
        when(konfiguration.getAsString(eq("isyfact.task.taskTestOnceInitialDelay.fixed-rate"))).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest2.fixed-delay"));
        when(konfiguration.getAsString(eq("isyfact.task.taskTestOnceInitialDelay.fixed-delay"))).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTestOnceInitialDelay.fixed-delay"));

        taskScheduler.starteKonfigurierteTasks();

        assertTrue(isTaskRunning("taskTest1"));
        assertTrue(isTaskRunning("taskTest2"));
        assertTrue(isTaskRunning("taskTest3"));
        assertTrue(isTaskRunning("taskTestOnceInitialDelay"));

        SECONDS.sleep(20);
        taskScheduler.shutdownMitTimeout(1);

        SECONDS.sleep(1);

        assertFalse(isTaskRunning("taskTest2"));
        assertFalse(isTaskRunning("taskTest3"));
        assertFalse(isTaskRunning("taskTest1"));
        assertFalse(isTaskRunning("taskTestOnceInitialDelay"));
    }

    @Test
    public void testScheduleManuell() throws Exception {
        boolean taskDone[] = { false };

        class ManuellerTask extends TestTask1 {

            final IsyLogger LOG = IsyLoggerFactory.getLogger(ManuellerTask.class);

            @Override
            public void execute() {
                for (int i = 0; i < 3; i++) {
                    try {
                        SECONDS.sleep(1);
                        LOG.info(LogKategorie.JOURNAL, "manuellerTask", "{} running Manueller Task",
                            DateTimeUtil.localDateTimeNow());
                    } catch (InterruptedException e) {
                        LOG.debug("Thread unterbrochen");
                        return;
                    }
                }
                taskDone[0] = true;
            }
        }
        ;

        Task manuellerTask = new ManuellerTask();

        TaskKonfiguration taskKonfiguration = new TaskKonfiguration();

        taskKonfiguration.setTaskId("manuellerTask");
        taskKonfiguration.setAuthenticator(new NoOpAuthenticator());
        taskKonfiguration.setHostname("localhost");
        taskKonfiguration.setAusfuehrungsplan(TaskKonfiguration.Ausfuehrungsplan.ONCE);
        taskKonfiguration.setInitialDelay(Duration.ofSeconds(1));

        TaskRunner taskRunner = new TaskRunnerImpl(manuellerTask, taskKonfiguration);

        taskScheduler.addTask(taskRunner);

        taskScheduler.start();

        assertTrue(isTaskRunning("manuellerTask"));

        taskScheduler.shutdownMitTimeout(7);

        assertTrue(taskDone[0]);

        SECONDS.sleep(1);

        assertFalse(isTaskRunning("manuellerTask"));
    }
}