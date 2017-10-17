package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationParameterException;
import de.bund.bva.pliscommon.konfiguration.common.konstanten.NachrichtenSchluessel;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@ContextConfiguration(locations = { "/spring/timertask-test.xml", "/spring/task_exception.xml" })
public class TestCompletionWatchdog extends AbstractTaskTest {

    @Test
    public void taskMitExceptionWirdNeuGestartet() throws Exception {
        when(konfiguration.getAsString("isyfact.task.taskMitException.benutzer")).thenReturn("TestUser1");
        when(konfiguration.getAsString("isyfact.task.taskMitException.passwort")).thenReturn("TestPasswort1");
        when(konfiguration.getAsString("isyfact.task.taskMitException.ausfuehrung")).thenReturn("FIXED_RATE");

        when(konfiguration.getAsString(eq("isyfact.task.taskMitException.initial-delay"), anyString()))
            .thenReturn("3s");
        when(konfiguration.getAsString(eq("isyfact.task.taskMitException.fixed-rate"), anyString()))
            .thenReturn("5s");
        when(konfiguration.getAsString(eq("isyfact.task.taskMitException.fixed-delay"), anyString()))
            .thenReturn("0s");

        when(konfiguration.getAsString("isyfact.task.taskMitException.zeitpunkt")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.taskMitException.zeitpunkt"));

        when(konfiguration.getAsInteger(eq(KonfigurationSchluessel.WATCHDOG_RESTART_INTERVAL), anyInt()))
            .thenReturn(3);

        taskScheduler.starteKonfigurierteTasks();

        taskScheduler.warteAufTerminierung(10);

        assertTrue(Boolean.valueOf(getMBeanAttribute("TaskMitException", "LetzteAusfuehrungErfolgreich")));
    }
}
