package de.bund.bva.isyfact.task;

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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
public class TestTaskScheduler2 {
    private Konfiguration konfiguration = mock(Konfiguration.class);
    private SecurityAuthenticator securityAuthenticator = mock(SecurityAuthenticator.class);

    @Test
    public void testSchedule() throws Exception {
        TaskHandler taskHandler = new TaskHandlerImpl();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS");
        String executionDateTime = LocalDateTime.now().plusSeconds(2).format(dateTimeFormatter);

        TaskData taskData1 = new TaskDataImpl(
                "id1",
                "username1",
                "password1",
                "de.bund.bva.isyfact.task.TestOperation1",
                "localhost",
                executionDateTime);
        Task task1 = taskHandler.createTask(taskData1);

        TaskData taskData2 = new TaskDataImpl(
                "id2",
                "username2",
                "password2",
                "de.bund.bva.isyfact.task.TestOperation2",
                "localhost",
                executionDateTime,
                0L,
                0L,
                0L,
                1L);
        Task task2 = taskHandler.createTask(taskData2);

        TaskScheduler taskScheduler = new TaskSchedulerImpl(konfiguration, securityAuthenticator);
        taskScheduler.schedule(task1);
        taskScheduler.scheduleAtFixedRate(task2);
        taskScheduler.awaitTerminationInSeconds(20);
        taskScheduler.shutDownNow();
    }
}