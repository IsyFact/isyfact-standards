package de.bund.bva.isyfact.task;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.endsWith;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/timertask.xml", "/spring/gesichertTask.xml" })
@DirtiesContext
public class TestGesichertTask {
    @Autowired
    private Konfiguration konfiguration;

    @Autowired
    private TaskScheduler taskScheduler;

    @Before
    public void setUp() throws Exception {
        when(konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN,
            KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN)).thenReturn("dd.MM.yyyy HH:mm:ss.SSS");
        when(konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads")).thenReturn(100);
        when(konfiguration.getAsString(endsWith("host")))
            .thenReturn(InetAddress.getLocalHost().getHostName());
    }

    @Test
    public void testGesicherterTaskAuthentifizierungErfolgreich() throws Exception {
        when(konfiguration.getAsString("isyfact.task.gesichertTask.benutzer")).thenReturn("TestUser1");
        when(konfiguration.getAsString("isyfact.task.gesichertTask.passwort")).thenReturn("TestPasswort");
        when(konfiguration.getAsString("isyfact.task.gesichertTask.ausfuehrung")).thenReturn("ONCE");
        String dateTimePattern = konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN,
            KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
        String executionDateTime1 = LocalDateTime.now().plusSeconds(1).format(dateTimeFormatter);
        when(konfiguration.getAsString("isyfact.task.gesichertTask.zeitpunkt")).thenReturn(executionDateTime1);

        taskScheduler.starteKonfigurierteTasks();

        taskScheduler.awaitTerminationInSeconds(10);

        assertTrue(Boolean.valueOf(getMBeanAttribute("LetzteAusfuehrungErfolgreich")));
    }

    @Test
    public void testGesicherterTaskAuthentifizierungFehlgeschlagen() throws Exception {
        when(konfiguration.getAsString("isyfact.task.gesichertTask.benutzer")).thenReturn("TestUser2");
        when(konfiguration.getAsString("isyfact.task.gesichertTask.passwort")).thenReturn("TestPasswort");
        when(konfiguration.getAsString("isyfact.task.gesichertTask.ausfuehrung")).thenReturn("ONCE");
        String dateTimePattern = konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN,
            KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
        String executionDateTime1 = LocalDateTime.now().plusSeconds(1).format(dateTimeFormatter);
        when(konfiguration.getAsString("isyfact.task.gesichertTask.zeitpunkt")).thenReturn(executionDateTime1);

        taskScheduler.starteKonfigurierteTasks();

        taskScheduler.awaitTerminationInSeconds(10);

        String letzterFehlerNachricht = getMBeanAttribute("LetzterFehlerNachricht");

        assertTrue(letzterFehlerNachricht.startsWith("#SIC2051 Die Autorisierung ist fehlgeschlagen. Das für diese Aktion erforderliche Recht ist nicht vorhanden. Recht1 "));
    }

    private String getMBeanAttribute(String attributeName) throws Exception {
        ArrayList<MBeanServer> mBeanServerList = MBeanServerFactory.findMBeanServer(null);
        MBeanServer mBeanServer = mBeanServerList.get(0);

        Hashtable<String, String> table = new Hashtable<>();
        table.put("type", "TaskMonitor");
        table.put("name", "\"GesichertTask\"");
        ObjectName testObjectName = new ObjectName("de.bund.bva.isyfact.taskRunner", table);
        return mBeanServer.getAttribute(testObjectName, attributeName).toString();
    }
}
