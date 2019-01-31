package de.bund.bva.isyfact.task;

import java.time.Duration;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.model.Task;
import de.bund.bva.isyfact.task.model.TaskRunner;
import de.bund.bva.isyfact.task.model.impl.TaskRunnerImpl;
import de.bund.bva.isyfact.task.sicherheit.impl.NoOpAuthenticator;
import de.bund.bva.isyfact.task.test.config.TestConfig;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"isy.logging.anwendung.name=test",
        "isy.logging.anwendung.typ=test",
        "isy.logging.anwendung.version=test"})
public class TestTaskSchedulerManuell extends AbstractTaskTest {

    @Test
    public void testScheduleManuell() throws Exception {
        boolean[] taskDone = { false };

        class ManuellerTask extends TestTask1 {

            private final IsyLogger LOG = IsyLoggerFactory.getLogger(ManuellerTask.class);

            @Override
            public void execute() {
                for (int i = 0; i < 3; i++) {
                    try {
                        SECONDS.sleep(1);
                        LOG.info(LogKategorie.JOURNAL, "manuellerTask", "{} running Manueller Task",
                            DateTimeUtil.localDateTimeNow());
                    } catch (InterruptedException e) {
                        LOG.debug("Thread unterbrochen");
                        return;
                    }
                }
                taskDone[0] = true;
            }
        }
        ;

        Task manuellerTask = new ManuellerTask();

        TaskKonfiguration taskKonfiguration = new TaskKonfiguration();

        taskKonfiguration.setTaskId("manuellerTask");
        taskKonfiguration.setAuthenticator(new NoOpAuthenticator());
        taskKonfiguration.setHostname("localhost");
        taskKonfiguration.setAusfuehrungsplan(TaskKonfiguration.Ausfuehrungsplan.ONCE);
        taskKonfiguration.setInitialDelay(Duration.ofSeconds(1));

        TaskRunner taskRunner = new TaskRunnerImpl(manuellerTask, taskKonfiguration);

        taskScheduler.addTask(taskRunner);

        taskScheduler.start();

        assertTrue(isTaskRunning("manuellerTask"));

        taskScheduler.shutdownMitTimeout(7);

        assertTrue(taskDone[0]);

        SECONDS.sleep(1);

        assertFalse(isTaskRunning("manuellerTask"));
    }
}