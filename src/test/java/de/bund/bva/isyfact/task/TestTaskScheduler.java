package de.bund.bva.isyfact.task;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.task.impl.TaskSchedulerImpl;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationParameterException;
import de.bund.bva.pliscommon.konfiguration.common.konstanten.NachrichtenSchluessel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
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

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Initialize the Tests
     */
    @Before
    public void setUp() throws UnknownHostException {
        when(konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN,
            KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN)).thenReturn("dd.MM.yyyy HH:mm:ss.SSS");
        when(konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads")).thenReturn(100);

        when(konfiguration.getAsString("isyfact.task.taskTest1.benutzer")).thenReturn("MyTestUser1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.passwort")).thenReturn("MyTestPasswort1");
        when(konfiguration.getAsString("isyfact.task.taskTest1.ausfuehrung")).thenReturn("ONCE");
        String dateTimePattern = konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN,
            KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
        String executionDateTime1 = LocalDateTime.now().plusSeconds(5).format(dateTimeFormatter);
        when(konfiguration.getAsString("isyfact.task.taskTest1.zeitpunkt")).thenReturn(executionDateTime1);

        when(konfiguration.getAsString("isyfact.task.taskTest2.benutzer")).thenReturn("MyTestUser2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.passwort")).thenReturn("MyTestPasswort2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.ausfuehrung")).thenReturn("FIXED_RATE");
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest2.initial-delay.days"), anyLong()))
            .thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest2.initial-delay.hours"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest2.initial-delay.minutes"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest2.initial-delay.seconds"), anyLong())).thenReturn(5L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest2.fixed-rate.days"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest2.fixed-rate.hours"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest2.fixed-rate.minutes"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest2.fixed-rate.seconds"), anyLong())).thenReturn(2L);
        when(konfiguration.getAsString("isyfact.task.taskTest2.zeitpunkt")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest2.zeitpunkt"));

        when(konfiguration.getAsString("isyfact.task.taskTest3.benutzer")).thenReturn("MyTestUser3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.passwort")).thenReturn("MyTestPasswort3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.ausfuehrung")).thenReturn("FIXED_DELAY");
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest3.initial-delay.days"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest3.initial-delay.hours"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest3.initial-delay.minutes"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest3.initial-delay.seconds"), anyLong())).thenReturn(5L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest3.fixed-delay.days"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest3.fixed-delay.hours"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest3.fixed-delay.minutes"), anyLong())).thenReturn(0L);
        when(konfiguration.getAsLong(eq("isyfact.task.taskTest3.fixed-delay.seconds"), anyLong())).thenReturn(3L);
        when(konfiguration.getAsString("isyfact.task.taskTest3.zeitpunkt")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest3.zeitpunkt"));

        when(konfiguration.getAsString(endsWith("host")))
            .thenReturn(InetAddress.getLocalHost().getHostName());
    }

    @Test
    public void testSchedule() throws Exception {
        TaskSchedulerImpl taskScheduler = new TaskSchedulerImpl(konfiguration);
        taskScheduler.setApplicationContext(applicationContext);
        taskScheduler.starteKonfigurierteTasks();

        taskScheduler.awaitTerminationInSeconds(2000);

        int amount_of_threads = konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads");
        assertEquals(amount_of_threads, 100);

        System.out.println("ScheduledExecuterService will shut down now!");
        //taskScheduler.shutDownNow();
    }
}