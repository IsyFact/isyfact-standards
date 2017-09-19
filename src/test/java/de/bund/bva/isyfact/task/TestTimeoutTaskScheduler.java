package de.bund.bva.isyfact.task;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledFuture;

import de.bund.bva.isyfact.task.handler.TaskDataHandler;
import de.bund.bva.isyfact.task.handler.TaskHandler;
import de.bund.bva.isyfact.task.handler.impl.TaskDataHandlerImpl;
import de.bund.bva.isyfact.task.handler.impl.TaskHandlerImpl;
import de.bund.bva.isyfact.task.impl.TaskSchedulerImpl;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskData;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tester for the TaskData Scheduler Class.
 * Die Zeitangabe erfolgt über das Pattern: "dd.MM.yyyy HH:mm:ss.SSS"
 * Der Zeitpunkt wird entweder über eine Properties-Datei oder programmatisch festgelegt.
 *
 * @author Alexander Salvanos, msg systems ag
 */
@RunWith(MockitoJUnitRunner.class)
public class TestTimeoutTaskScheduler  {

    @Mock
    private Konfiguration konfiguration;

    @Mock
    private SecurityAuthenticator securityAuthenticator;

    private DateTimeFormatter dateTimeFormatter;

    /**
     * Initialize the Tests
     */
    @Before
    public void initialize() {
        when(konfiguration.getAsString("isyfact.task.standard.date_time_pattern"))
                .thenReturn("dd.MM.yyyy HH:mm:ss.SSS");
        when(konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads"))
                .thenReturn(100);

        String dateTimePattern = konfiguration.getAsString(
                KonfigurationSchluessel.DATETIME_PATTERN,
                KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);

        // TODO Konfiguration funktioniert nicht! Warum?
        dateTimePattern = "dd.MM.yyyy HH:mm:ss.SSS";

        dateTimeFormatter =
                DateTimeFormatter.ofPattern(dateTimePattern);

    }

    /**
     *
     *
     * @throws Exception
     */
    @Test
    public void testSchedule() throws Exception {

        when(konfiguration.getAsString("isyfact.task.taskTest1.id"))
                .thenReturn("TaskTest1");

        /*
         * Fake ExecutionDateTime to a delay of 5 seconds, in order to have the test repeatable.
         */
        LocalDateTime genericExecutionDateTime = LocalDateTime.now().plusSeconds(5);
        String sExecutionDateTime = genericExecutionDateTime.format(dateTimeFormatter);
        System.out.println("sExecutionDateTime:" + sExecutionDateTime);
        when(konfiguration.getAsString("isyfact.task.taskTest1.executionDateTime"))
                .thenReturn(sExecutionDateTime);

        when(konfiguration.getAsString("isyfact.task.taskTest1.hostName"))
                .thenReturn("isyfact-VirtualBox");
        when(konfiguration.getAsString("isyfact.task.taskTest1.operationName"))
                .thenReturn("de.bund.bva.isyfact.task.TestOperation1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.username"))
                .thenReturn("MyTestUser1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.password"))
                .thenReturn("MyTestPasswort1");

        String id = konfiguration.getAsString("isyfact.task.taskTest1.id");
        String username = konfiguration.getAsString("isyfact.task.taskTest1.username");
        String password = konfiguration.getAsString("isyfact.task.taskTest1.password");
        String executionDateTime = konfiguration.getAsString("isyfact.task.taskTest1.executionDateTime");
        String operationName = konfiguration.getAsString("isyfact.task.taskTest1.operationName");
        String hostName = konfiguration.getAsString("isyfact.task.taskTest1.hostName");

        TaskDataHandler taskDataHandler = new TaskDataHandlerImpl();
        TaskData taskData = taskDataHandler.createTaskData(
                id,
                username,
                password,
                executionDateTime,
                operationName,
                hostName
        );

        TaskHandler taskHandler = new TaskHandlerImpl();
        Task task = taskHandler.createTask(taskData);

        TaskScheduler taskScheduler = new TaskSchedulerImpl(konfiguration, securityAuthenticator);
        taskScheduler.schedule(task);

        int delay = task.getExecutionDateTime().getSecond() - LocalDateTime.now().getSecond();
        System.out.println("Waiting " + delay + " seconds");

        taskScheduler.awaitTerminationInSeconds(10);

        int amount_of_threads = konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads");
        assertEquals(amount_of_threads, 100);

        System.out.println("ScheduledExecuterService will shut down now!");
        taskScheduler.shutDownNow();

    }
}