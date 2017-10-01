package de.bund.bva.isyfact.task;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.task.handler.TaskDataHandler;
import de.bund.bva.isyfact.task.handler.TaskHandler;
import de.bund.bva.isyfact.task.handler.impl.TaskDataHandlerImpl;
import de.bund.bva.isyfact.task.handler.impl.TaskHandlerImpl;
import de.bund.bva.isyfact.task.impl.TaskSchedulerImpl;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskData;
import de.bund.bva.isyfact.task.model.TaskKonfiguration;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.junit.After;
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
public class TestTimeoutTaskScheduler {
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
    }

    @Test
    public void testSchedule1() throws Exception {
        String dateTimePattern = konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN, KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);

        TaskDataHandler taskDataHandler = new TaskDataHandlerImpl();
        TaskHandler taskHandler = new TaskHandlerImpl();

        String id1 = konfiguration.getAsString("isyfact.task.taskTest1.id");
        String username1 = konfiguration.getAsString("isyfact.task.taskTest1.benutzer");
        String password1 = konfiguration.getAsString("isyfact.task.taskTest1.passwort");
        String executionDateTime1 = LocalDateTime.now().plusSeconds(3).format(dateTimeFormatter);
        String operationName1 = konfiguration.getAsString("isyfact.task.taskTest1.operationName");
        String hostName1 = konfiguration.getAsString("isyfact.task.taskTest1.host");
        TaskData taskData1 = taskDataHandler.createTaskData(
                id1,
                username1,
                password1,
                executionDateTime1,
                operationName1,
                hostName1);
        Task task1 = taskHandler.createTask(taskData1);

        String id2 = konfiguration.getAsString("isyfact.task.taskTest2.id");
        String username2 = konfiguration.getAsString("isyfact.task.taskTest2.benutzer");
        String password2 = konfiguration.getAsString("isyfact.task.taskTest2.passwort");
        String executionDateTime2 = LocalDateTime.now().plusSeconds(3).format(dateTimeFormatter);
        String operationName2 = konfiguration.getAsString("isyfact.task.taskTest2.operationName");
        String hostName2 = konfiguration.getAsString("isyfact.task.taskTest2.host");
        TaskData taskData2 = taskDataHandler.createTaskData(
                id2,
                username2,
                password2,
                executionDateTime2,
                operationName2,
                hostName2);
        Task task2 = taskHandler.createTask(taskData2);

        TaskScheduler taskScheduler = new TaskSchedulerImpl(konfiguration, securityAuthenticator);
        taskScheduler.schedule(task1);
        taskScheduler.schedule(task2);
        taskScheduler.awaitTerminationInSeconds(20);

        int amount_of_threads = konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads");
        assertEquals(amount_of_threads, 100);

        System.out.println("ScheduledExecuterService will shut down now!");
        taskScheduler.shutDownNow();
    }
}