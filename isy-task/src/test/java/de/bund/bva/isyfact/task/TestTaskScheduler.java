package de.bund.bva.isyfact.task;

import java.net.InetAddress;
import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.test.config.TestConfig;
import de.bund.bva.isyfact.task.test.config.TestTasksConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = { TestConfig.class, TestTasksConfig.class },
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = { "isy.logging.anwendung.name=test",
        "isy.logging.anwendung.typ=test",
        "isy.logging.anwendung.version=test",
        "logging.level.root=info",
        "isy.task.tasks.testTask1.benutzer=TestUser1",
        "isy.task.tasks.testTask1.passwort=TestPasswort1",
        "isy.task.tasks.testTask1.ausfuehrung=ONCE",
        "isy.task.tasks.testTask1.initial-delay=0s",

        "isy.task.tasks.testTask2.ausfuehrung=FIXED_RATE",
        "isy.task.tasks.testTask2.initial-delay=5s",
        "isy.task.tasks.testTask2.fixed-rate=2s",

        "isy.task.tasks.testTask3.benutzer=TestUser3",
        "isy.task.tasks.testTask3.passwort=TestPasswort3",
        "isy.task.tasks.testTask3.ausfuehrung=FIXED_DELAY",
        "isy.task.tasks.testTask3.initial-delay=5s",
        "isy.task.tasks.testTask3.fixed-delay=3s",

        "isy.task.tasks.testTaskOnceInitialDelay.benutzer=TestUser1",
        "isy.task.tasks.testTaskOnceInitialDelay.passwort=TestPasswort1",
        "isy.task.tasks.testTaskOnceInitialDelay.ausfuehrung=ONCE",
        "isy.task.tasks.testTaskOnceInitialDelay.initial-delay=10s" })
public class TestTaskScheduler extends AbstractTaskTest {

    @Autowired
    private IsyTaskConfigurationProperties configurationProperties;

    @Before
    public void setup() throws Exception {
        configurationProperties.getDefault().setHost(InetAddress.getLocalHost().getHostName());
        String executionDateTime = DateTimeUtil.localDateTimeNow().plusSeconds(5)
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS"));
        configurationProperties.getTasks().get("testTask1").setZeitpunkt(executionDateTime);
    }

    @Test
    public void testSchedule() throws Exception {
        taskScheduler.starteKonfigurierteTasks();

        assertTrue(isTaskRunning("testTask1"));
        assertTrue(isTaskRunning("testTask2"));
        assertTrue(isTaskRunning("testTask3"));
        assertTrue(isTaskRunning("testTaskOnceInitialDelay"));

        SECONDS.sleep(20);
        taskScheduler.shutdownMitTimeout(1);

        SECONDS.sleep(1);

        assertFalse(isTaskRunning("testTask2"));
        assertFalse(isTaskRunning("testTask3"));
        assertFalse(isTaskRunning("testTask1"));
        assertFalse(isTaskRunning("testTaskOnceInitialDelay"));
    }
}
