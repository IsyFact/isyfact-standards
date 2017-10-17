package de.bund.bva.isyfact.task;

import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@ContextConfiguration(locations = { "/spring/timertask.xml", "/spring/gesichertTask.xml" })
public class TestGesichertTask extends AbstractTaskTest {
    @Test
    public void testGesicherterTaskAuthentifizierungErfolgreich() throws Exception {
        when(konfiguration.getAsString("isyfact.task.gesichertTask.benutzer")).thenReturn("TestUser1");
        when(konfiguration.getAsString("isyfact.task.gesichertTask.passwort")).thenReturn("TestPasswort");
        when(konfiguration.getAsString("isyfact.task.gesichertTask.ausfuehrung")).thenReturn("ONCE");
        String dateTimePattern = konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN,
            KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
        String executionDateTime1 = DateTimeUtil.localDateTimeNow().plusSeconds(1).format(dateTimeFormatter);
        when(konfiguration.getAsString("isyfact.task.gesichertTask.zeitpunkt")).thenReturn(executionDateTime1);

        when(konfiguration.getAsString(eq("isyfact.task.gesichertTask.initial-delay"), anyString()))
            .thenReturn("0s");
        when(konfiguration.getAsString(eq("isyfact.task.gesichertTask.fixed-rate"), anyString()))
            .thenReturn("0s");
        when(konfiguration.getAsString(eq("isyfact.task.gesichertTask.fixed-delay"), anyString()))
            .thenReturn("0s");

        taskScheduler.starteKonfigurierteTasks();

        taskScheduler.warteAufTerminierung(10);

        assertTrue(Boolean.valueOf(getMBeanAttribute("GesichertTask", "LetzteAusfuehrungErfolgreich")));
    }

    @Test
    public void testGesicherterTaskAuthentifizierungFehlgeschlagen() throws Exception {
        when(konfiguration.getAsString("isyfact.task.gesichertTask.benutzer")).thenReturn("TestUser2");
        when(konfiguration.getAsString("isyfact.task.gesichertTask.passwort")).thenReturn("TestPasswort");
        when(konfiguration.getAsString("isyfact.task.gesichertTask.ausfuehrung")).thenReturn("ONCE");
        String dateTimePattern = konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN,
            KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
        String executionDateTime1 = DateTimeUtil.localDateTimeNow().plusSeconds(1).format(dateTimeFormatter);
        when(konfiguration.getAsString("isyfact.task.gesichertTask.zeitpunkt")).thenReturn(executionDateTime1);

        when(konfiguration.getAsInteger(eq(KonfigurationSchluessel.WATCHDOG_RESTART_INTERVAL), anyInt()))
            .thenReturn(3);

        taskScheduler.starteKonfigurierteTasks();

        taskScheduler.warteAufTerminierung(10);

        String letzterFehlerNachricht = getMBeanAttribute("GesichertTask", "LetzterFehlerNachricht");

        assertTrue(letzterFehlerNachricht.startsWith("#SIC2051 Die Autorisierung ist fehlgeschlagen. Das für diese Aktion erforderliche Recht ist nicht vorhanden. Recht1 "));
    }
}
