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
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.OPERATION;
import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.PRAEFIX;
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

    @Autowired
    private ApplicationContext applicationContext;

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

        when(konfiguration.getAsString("isyfact.task.taskTest1.id")).thenReturn("taskTest1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.benutzer")).thenReturn("MyTestUser1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.passwort")).thenReturn("MyTestPasswort1");

        String id = konfiguration.getAsString("isyfact.task.taskTest1.id");
        when(konfiguration.getAsString(PRAEFIX + id + OPERATION))
                .thenReturn("de.bund.bva.isyfact.task.TestOperation1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.host"))
                .thenReturn(InetAddress.getLocalHost().getHostName());
        when(konfiguration.getAsString("isyfact.task.taskTest1.ausfuehrungsplan"))
                .thenReturn("ONCE");

        String dateTimePattern = konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN, KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
        String executionDateTime1 = LocalDateTime.now().plusSeconds(5).format(dateTimeFormatter);

        when(konfiguration.getAsString("isyfact.task.taskTest1.executionDateTime"))
                .thenReturn(executionDateTime1);

        when(konfiguration.getAsString("isyfact.task.taskTest2.id")).thenReturn("taskTest2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.benutzer")).thenReturn("MyTestUser2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.passwort")).thenReturn("MyTestPasswort2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.operation"))
                .thenReturn("de.bund.bva.isyfact.task.TestOperation2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.host"))
                .thenReturn(InetAddress.getLocalHost().getHostName());
        when(konfiguration.getAsString("isyfact.task.taskTest2.ausfuehrungsplan"))
                .thenReturn("FIXED_RATE");
        String executionDateTime2 = LocalDateTime.now().plusSeconds(5).format(dateTimeFormatter);
        when(konfiguration.getAsString("isyfact.task.taskTest2.executionDateTime"))
                .thenReturn(executionDateTime2);
        when(konfiguration.getAsLong("isyfact.task.taskTest2.fixedRate.days"))
                .thenReturn(0L);
        when(konfiguration.getAsLong("isyfact.task.taskTest2.fixedRate.hours"))
                .thenReturn(0L);
        when(konfiguration.getAsLong("isyfact.task.taskTest2.fixedRate.minutes"))
                .thenReturn(0L);
        when(konfiguration.getAsLong("isyfact.task.taskTest2.fixedRate.seconds"))
                .thenReturn(2L);

        when(konfiguration.getAsString("isyfact.task.taskTest3.id")).thenReturn("taskTest3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.benutzer")).thenReturn("MyTestUser3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.passwort")).thenReturn("MyTestPasswort3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.operation"))
                .thenReturn("de.bund.bva.isyfact.task.TestOperation3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.host"))
                .thenReturn(InetAddress.getLocalHost().getHostName());
        when(konfiguration.getAsString("isyfact.task.taskTest3.ausfuehrungsplan"))
                .thenReturn("FIXED_DELAY");
        String executionDateTime3 = LocalDateTime.now().plusSeconds(5).format(dateTimeFormatter);
        when(konfiguration.getAsString("isyfact.task.taskTest3.executionDateTime"))
                .thenReturn(executionDateTime3);
        when(konfiguration.getAsLong("isyfact.task.taskTest3.fixedRate.days"))
                .thenReturn(0L);
        when(konfiguration.getAsLong("isyfact.task.taskTest3.fixedRate.hours"))
                .thenReturn(0L);
        when(konfiguration.getAsLong("isyfact.task.taskTest3.fixedRate.minutes"))
                .thenReturn(0L);
        when(konfiguration.getAsLong("isyfact.task.taskTest3.fixedRate.seconds"))
                .thenReturn(3L);
        when(konfiguration.getAsLong("isyfact.task.taskTest3.fixedDelay.days"))
                .thenReturn(0L);
        when(konfiguration.getAsLong("isyfact.task.taskTest3.fixedDelay.hours"))
                .thenReturn(0L);
        when(konfiguration.getAsLong("isyfact.task.taskTest3.fixedDelay.minutes"))
                .thenReturn(0L);
        when(konfiguration.getAsLong("isyfact.task.taskTest3.fixedDelay.seconds"))
                .thenReturn(3L);
    }

    @Test
    public void testSchedule() throws Exception {
        TaskHandler taskHandler = new TaskHandlerImpl();
        Task task1 = taskHandler.createTask(konfiguration.getAsString("isyfact.task.taskTest1.id"), konfiguration, applicationContext);
        Task task2 = taskHandler.createTask(konfiguration.getAsString("isyfact.task.taskTest2.id"), konfiguration, applicationContext);
        Task task3 = taskHandler.createTask(konfiguration.getAsString("isyfact.task.taskTest3.id"), konfiguration, applicationContext);

        TaskScheduler taskScheduler = new TaskSchedulerImpl(konfiguration);
        taskScheduler.addTask(task1);
        taskScheduler.addTask(task2);
        taskScheduler.addTask(task3);
        taskScheduler.start();
        taskScheduler.awaitTerminationInSeconds(20);

        int amount_of_threads = konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads");
        assertEquals(amount_of_threads, 100);

        System.out.println("ScheduledExecuterService will shut down now!");
        taskScheduler.shutDownNow();
    }
}