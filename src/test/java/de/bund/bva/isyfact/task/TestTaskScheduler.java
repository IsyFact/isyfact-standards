package de.bund.bva.isyfact.task;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.task.handler.TaskHandler;
import de.bund.bva.isyfact.task.handler.impl.TaskHandlerImpl;
import de.bund.bva.isyfact.task.impl.TaskSchedulerImpl;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskData;
import de.bund.bva.isyfact.task.model.impl.TaskDataImpl;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tester for the TaskData Scheduler Class.
 * Die Zeitangabe erfolgt über das Pattern: "dd.MM.yyyy HH:mm:ss.SSS"
 * Der Zeitpunkt wird entweder über eine Properties-Datei oder programmatisch festgelegt.
 *
 * @author Alexander Salvanos, msg systems ag
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/timertask.xml" })
public class TestTaskScheduler {
    private Konfiguration konfiguration = mock(Konfiguration.class);
    private SecurityAuthenticator securityAuthenticator = mock(SecurityAuthenticator.class);

    /**
     * Initialize the Tests
     */
    @Before
    public void setUp() throws UnknownHostException {
        when(konfiguration.getAsString(
                KonfigurationSchluessel.DATETIME_PATTERN,
                KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN))
                .thenReturn("dd.MM.yyyy HH:mm:ss.SSS");
        when(konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads")).thenReturn(100);

        when(konfiguration.getAsString("isyfact.task.taskTest1.id")).thenReturn("TaskTest1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.benutzer")).thenReturn("MyTestUser1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.passwort")).thenReturn("MyTestPasswort1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.operationName"))
                .thenReturn("de.bund.bva.isyfact.task.TestOperation1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.host"))
                .thenReturn(InetAddress.getLocalHost().getHostName());

        when(konfiguration.getAsString("isyfact.task.taskTest2.id")).thenReturn("TaskTest2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.benutzer")).thenReturn("MyTestUser2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.passwort")).thenReturn("MyTestPasswort2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.operationName"))
                .thenReturn("de.bund.bva.isyfact.task.TestOperation2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.host"))
                .thenReturn(InetAddress.getLocalHost().getHostName());
        when(konfiguration.getAsString("isyfact.task.taskTest2.fixedRate.days"))
                .thenReturn("0");
        when(konfiguration.getAsString("isyfact.task.taskTest2.fixedRate.hours"))
                .thenReturn("0");
        when(konfiguration.getAsString("isyfact.task.taskTest2.fixedRate.minutes"))
                .thenReturn("0");
        when(konfiguration.getAsString("isyfact.task.taskTest2.fixedRate.seconds"))
                .thenReturn("0");

        when(konfiguration.getAsString("isyfact.task.taskTest3.id")).thenReturn("TaskTest3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.benutzer")).thenReturn("MyTestUser3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.passwort")).thenReturn("MyTestPasswort3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.operationName"))
                .thenReturn("de.bund.bva.isyfact.task.TestOperation3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.host"))
                .thenReturn(InetAddress.getLocalHost().getHostName());
        when(konfiguration.getAsString("isyfact.task.taskTest3.fixedRate.days"))
                .thenReturn("0");
        when(konfiguration.getAsString("isyfact.task.taskTest3.fixedRate.hours"))
                .thenReturn("0");
        when(konfiguration.getAsString("isyfact.task.taskTest3.fixedRate.minutes"))
                .thenReturn("0");
        when(konfiguration.getAsString("isyfact.task.taskTest3.fixedRate.seconds"))
                .thenReturn("0");
        when(konfiguration.getAsString("isyfact.task.taskTest3.fixedDelay.days"))
                .thenReturn("0");
        when(konfiguration.getAsString("isyfact.task.taskTest3.fixedDelay.hours"))
                .thenReturn("0");
        when(konfiguration.getAsString("isyfact.task.taskTest3.fixedDelay.minutes"))
                .thenReturn("0");
        when(konfiguration.getAsString("isyfact.task.taskTest3.fixedDelay.seconds"))
                .thenReturn("0");
    }

    @Test
    public void testSchedule() throws Exception {
        String dateTimePattern = konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN, KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);

        TaskHandler taskHandler = new TaskHandlerImpl();



        String id1 = konfiguration.getAsString("isyfact.task.taskTest1.id");
        String username1 = konfiguration.getAsString("isyfact.task.taskTest1.benutzer");
        String password1 = konfiguration.getAsString("isyfact.task.taskTest1.passwort");
        String operationName1 = konfiguration.getAsString("isyfact.task.taskTest1.operationName");
        String hostName1 = konfiguration.getAsString("isyfact.task.taskTest1.host");
        String executionDateTime1 = LocalDateTime.now().plusSeconds(2).format(dateTimeFormatter);
        TaskData taskData1 = new TaskDataImpl(
                id1,
                username1,
                password1,
                operationName1,
                hostName1,
                executionDateTime1);
        Task task1 = taskHandler.createTask(taskData1);

        String id2 = konfiguration.getAsString("isyfact.task.taskTest2.id");
        String username2 = konfiguration.getAsString("isyfact.task.taskTest2.benutzer");
        String password2 = konfiguration.getAsString("isyfact.task.taskTest2.passwort");
        String operationName2 = konfiguration.getAsString("isyfact.task.taskTest2.operationName");
        String hostName2 = konfiguration.getAsString("isyfact.task.taskTest2.host");
        String executionDateTime2 = LocalDateTime.now().plusSeconds(3).format(dateTimeFormatter);
        long fixedRateDays2 = konfiguration.getAsLong("isyfact.task.taskTest2.fixedRate.days");
        long fixedRateHours2 = konfiguration.getAsLong("isyfact.task.taskTest2.fixedRate.hours");
        long fixedRateMinutes2 = konfiguration.getAsLong("isyfact.task.taskTest2.fixedRate.minutes");
        long fixedRateSeconds2 = konfiguration.getAsLong("isyfact.task.taskTest2.fixedRate.seconds");
        TaskData taskData2 = new TaskDataImpl(
                id2,
                username2,
                password2,
                operationName2,
                hostName2,
                executionDateTime2,
                fixedRateDays2,
                fixedRateHours2,
                fixedRateMinutes2,
                fixedRateSeconds2);
        Task task2 = taskHandler.createTask(taskData2);

        String id3 = konfiguration.getAsString("isyfact.task.taskTest3.id");
        String username3 = konfiguration.getAsString("isyfact.task.taskTest3.benutzer");
        String password3 = konfiguration.getAsString("isyfact.task.taskTest3.passwort");
        String operationName3 = konfiguration.getAsString("isyfact.task.taskTest3.operationName");
        String hostName3 = konfiguration.getAsString("isyfact.task.taskTest3.host");
        String executionDateTime3 = LocalDateTime.now().plusSeconds(3).format(dateTimeFormatter);
        long fixedRateDays3 = konfiguration.getAsLong("isyfact.task.taskTest3.fixedRate.days");
        long fixedRateHours3 = konfiguration.getAsLong("isyfact.task.taskTest3.fixedRate.hours");
        long fixedRateMinutes3 = konfiguration.getAsLong("isyfact.task.taskTest3.fixedRate.minutes");
        long fixedRateSeconds3 = konfiguration.getAsLong("isyfact.task.taskTest3.fixedRate.seconds");
        long fixedDelayDays3 = konfiguration.getAsLong("isyfact.task.taskTest3.fixedDelay.days");
        long fixedDelayHours3 = konfiguration.getAsLong("isyfact.task.taskTest3.fixedDelay.hours");
        long fixedDelayMinutes3 = konfiguration.getAsLong("isyfact.task.taskTest3.fixedDelay.minutes");
        long fixedDelaySeconds3 = konfiguration.getAsLong("isyfact.task.taskTest3.fixedDelay.seconds");

        TaskData taskData3 = new TaskDataImpl(
                id3,
                username3,
                password3,
                operationName3,
                hostName3,
                executionDateTime3,
                fixedRateDays3,
                fixedRateHours3,
                fixedRateMinutes3,
                fixedRateSeconds3,
                fixedDelayDays3,
                fixedDelayHours3,
                fixedDelayMinutes3,
                fixedDelaySeconds3
        );
        Task task3 = taskHandler.createTask(taskData3);

        TaskScheduler taskScheduler = new TaskSchedulerImpl(konfiguration, securityAuthenticator);
        taskScheduler.schedule(task1);
        taskScheduler.schedule(task2);
        taskScheduler.schedule(task3);
        taskScheduler.awaitTerminationInSeconds(20);

        int amount_of_threads = konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads");
        assertEquals(amount_of_threads, 100);

        System.out.println("ScheduledExecuterService will shut down now!");
        taskScheduler.shutDownNow();
    }
}