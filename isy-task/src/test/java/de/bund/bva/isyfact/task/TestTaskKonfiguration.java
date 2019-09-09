package de.bund.bva.isyfact.task;

import java.time.Duration;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.task.exception.TaskKonfigurationInvalidException;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfigurationVerwalter;
import de.bund.bva.isyfact.task.sicherheit.impl.NoOpAuthenticator;
import de.bund.bva.isyfact.task.test.config.TestConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = { "isy.logging.anwendung.name=test",
        "isy.logging.anwendung.typ=test",
        "isy.logging.anwendung.version=test",
        "isy.task.tasks.test.benutzer=Test1",
        "isy.task.tasks.test.passwort=Test1",
        "isy.task.tasks.test.bhkz=Test1",
        "isy.task.tasks.test.initial-delay=1s",
        "isy.task.tasks.test.fixed-rate=3s" })
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
