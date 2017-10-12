package de.bund.bva.isyfact.task;

import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationParameterException;
import de.bund.bva.pliscommon.konfiguration.common.konstanten.NachrichtenSchluessel;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tester for the TaskData Scheduler Class.
 * Die Zeitangabe erfolgt über das Pattern: "dd.MM.yyyy HH:mm:ss.SSS"
 * Der Zeitpunkt wird entweder über eine Properties-Datei oder programmatisch festgelegt.
 */
@ContextConfiguration(locations = { "/spring/timertask.xml", "/spring/tasks1_2_3.xml" })
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
        when(konfiguration.getAsString(eq("isyfact.task.taskTest1.fixed-rate"), anyString()))
            .thenReturn("0s");
        when(konfiguration.getAsString(eq("isyfact.task.taskTest1.fixed-delay"), anyString()))
            .thenReturn("0s");

        when(konfiguration.getAsString("isyfact.task.taskTest2.benutzer")).thenReturn("TestUser2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.passwort")).thenReturn("TestPasswort2");
        when(konfiguration.getAsString("isyfact.task.taskTest2.ausfuehrung")).thenReturn("FIXED_RATE");

        when(konfiguration.getAsString(eq("isyfact.task.taskTest2.initial-delay"), anyString()))
            .thenReturn("5s");
        when(konfiguration.getAsString(eq("isyfact.task.taskTest2.fixed-rate"), anyString()))
            .thenReturn("2s");
        when(konfiguration.getAsString(eq("isyfact.task.taskTest2.fixed-delay"), anyString()))
            .thenReturn("0s");

        when(konfiguration.getAsString("isyfact.task.taskTest2.zeitpunkt")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest2.zeitpunkt"));

        when(konfiguration.getAsString("isyfact.task.taskTest3.benutzer")).thenReturn("TestUser3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.passwort")).thenReturn("TestPasswort3");
        when(konfiguration.getAsString("isyfact.task.taskTest3.ausfuehrung")).thenReturn("FIXED_DELAY");

        when(konfiguration.getAsString(eq("isyfact.task.taskTest3.initial-delay"), anyString()))
            .thenReturn("5s");
        when(konfiguration.getAsString(eq("isyfact.task.taskTest3.fixed-rate"), anyString()))
            .thenReturn("0s");
        when(konfiguration.getAsString(eq("isyfact.task.taskTest3.fixed-delay"), anyString()))
            .thenReturn("3s");

        when(konfiguration.getAsString("isyfact.task.taskTest3.zeitpunkt")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskTest3.zeitpunkt"));

        taskScheduler.starteKonfigurierteTasks();

        taskScheduler.awaitTerminationInSeconds(60);

        int amount_of_threads = konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads");
        assertEquals(amount_of_threads, 100);

        System.out.println("ScheduledExecuterService will shut down now!");
    }
}