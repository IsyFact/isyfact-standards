package de.bund.bva.isyfact.task;

import java.net.InetAddress;

import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.isyfact.task.test.config.TestConfig;
import de.bund.bva.isyfact.task.test.config.TestTaskExceptionConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = {TestConfig.class, TestTaskExceptionConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"isy.logging.anwendung.name=test",
                  "isy.logging.anwendung.typ=test",
                  "isy.logging.anwendung.version=test",
                  "logging.level.root=info",
                  "isy.task.tasks.taskMitException.benutzer=TestUser1",
                  "isy.task.tasks.taskMitException.passwort=TestPasswort1",
                  "isy.task.tasks.taskMitException.bhkz=BHKZ1",
                  "isy.task.tasks.taskMitException.ausfuehrung=FIXED_RATE",
                  "isy.task.tasks.taskMitException.initial-delay=1s",
                  "isy.task.tasks.taskMitException.fixed-rate=3s",
                  "isy.task.watchdog.restart-interval=1s"})
public class TestCompletionWatchdog extends AbstractTaskTest {

    @Autowired
    private IsyTaskConfigurationProperties configurationProperties;

    @Autowired
    private TaskMonitor taskMonitor;

    @Before
    public void setup() throws Exception {
        configurationProperties.getDefault().setHost(InetAddress.getLocalHost().getHostName());
    }

    @Test
    public void taskMitExceptionWirdNeuGestartet() throws Exception {
        taskScheduler.starteKonfigurierteTasks();

        SECONDS.sleep(10);

        taskScheduler.shutdownMitTimeout(10);

        assertTrue(taskMonitor.isLetzteAusfuehrungErfolgreich());
    }
}
