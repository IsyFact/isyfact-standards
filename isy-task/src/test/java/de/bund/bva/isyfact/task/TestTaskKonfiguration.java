package de.bund.bva.isyfact.task;

import java.time.Duration;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.task.exception.TaskKonfigurationInvalidException;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfigurationVerwalter;
import de.bund.bva.isyfact.task.sicherheit.impl.NoOpAuthenticator;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationParameterException;
import de.bund.bva.pliscommon.konfiguration.common.konstanten.NachrichtenSchluessel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(locations = { "/spring/timertask-test.xml" })
public class TestTaskKonfiguration extends AbstractTaskTest {

    @Autowired
    private TaskKonfigurationVerwalter taskKonfigurationVerwalter;

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void taskIdNull() throws Exception {
        TaskKonfiguration taskKonfiguration = new TaskKonfiguration();

        taskKonfiguration.setTaskId(null);

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void authenticatorNull() throws Exception {
        TaskKonfiguration taskKonfiguration = new TaskKonfiguration();

        taskKonfiguration.setAuthenticator(null);

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void ausfuehrungsplanNull() throws Exception {
        TaskKonfiguration taskKonfiguration = new TaskKonfiguration();

        taskKonfiguration.setAusfuehrungsplan(null);

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void hostnameNull() throws Exception {
        TaskKonfiguration taskKonfiguration = new TaskKonfiguration();

        taskKonfiguration.setHostname(null);

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void ausfuehrungOnceWederExecutionDateTimeNochInitialDelayGesetzt() throws Exception {
        TaskKonfiguration taskKonfiguration = getTestTaskKonfiguration();

        taskKonfiguration.setInitialDelay(null);
        taskKonfiguration.setExecutionDateTime(null);

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void ausfuehrungOnceExecutionDateTimUndInitialDelayGesetzt() throws Exception {
        TaskKonfiguration taskKonfiguration = getTestTaskKonfiguration();

        taskKonfiguration.setInitialDelay(Duration.ofSeconds(10));
        taskKonfiguration.setExecutionDateTime(DateTimeUtil.localDateTimeNow());

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void ausfuehrungFixedRateFixedRateNichtGesetzt() throws Exception {
        TaskKonfiguration taskKonfiguration = getTestTaskKonfiguration();

        taskKonfiguration.setAusfuehrungsplan(TaskKonfiguration.Ausfuehrungsplan.FIXED_RATE);
        taskKonfiguration.setFixedRate(null);

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void ausfuehrungFixedDelayFixedDelayNichtGesetzt() throws Exception {
        TaskKonfiguration taskKonfiguration = getTestTaskKonfiguration();

        taskKonfiguration.setAusfuehrungsplan(TaskKonfiguration.Ausfuehrungsplan.FIXED_DELAY);
        taskKonfiguration.setFixedDelay(null);

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void getTaskKonfigurationPrueftKonfiguration() throws Exception {
        when(konfiguration.getAsString("isyfact.task.test.benutzer")).thenReturn("TestUser1");
        when(konfiguration.getAsString("isyfact.task.test.passwort")).thenReturn("TestPasswort1");
        when(konfiguration.getAsString("isyfact.task.test.bhkz")).thenReturn("BHKZ1");
        when(konfiguration.getAsString("isyfact.task.test.ausfuehrung")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.test.ausfuehrung"));
        when(konfiguration.getAsString("isyfact.task.test.zeitpunkt")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.test.zeitpunkt"));
        when(konfiguration.getAsString(eq("isyfact.task.test.initial-delay"), anyString())).thenReturn("1s");
        when(konfiguration.getAsString("isyfact.task.test.fixed-rate")).thenReturn("3s");
        when(konfiguration.getAsString("isyfact.task.test.fixed-delay")).thenThrow(
            new KonfigurationParameterException(NachrichtenSchluessel.ERR_PARAMETER_LEER,
                "isyfact.task.test.fixed-delay"));

        taskKonfigurationVerwalter.getTaskKonfiguration("test");
    }

    private TaskKonfiguration getTestTaskKonfiguration() {
        TaskKonfiguration taskKonfiguration = new TaskKonfiguration();

        taskKonfiguration.setTaskId("taskId");
        taskKonfiguration.setAuthenticator(new NoOpAuthenticator());
        taskKonfiguration.setHostname("hostname");
        taskKonfiguration.setAusfuehrungsplan(TaskKonfiguration.Ausfuehrungsplan.ONCE);

        return taskKonfiguration;
    }
}
