package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.isyfact.task.test.config.TestConfig;
import de.bund.bva.isyfact.task.test.config.TestTaskHostnameConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = { TestConfig.class,
    TestTaskHostnameConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test", "isy.logging.anwendung.version=test",
    "isy.task.authentication.enabled=true",
    "isy.task.tasks.hostnameTask1.host=thisisthewronghostname",
    "isy.task.tasks.hostnameTask1.benutzer=TestUser1",
    "isy.task.tasks.hostnameTask1.passwort=TestPasswort1",
    "isy.task.tasks.hostnameTask1.bhkz=BHKZ1",
    "isy.task.tasks.hostnameTask1.ausfuehrung=ONCE",
    "isy.task.tasks.hostnameTask1.initial-delay=0s",
    "isy.task.tasks.hostnameTask2.host=.*",
    "isy.task.tasks.hostnameTask2.benutzer=TestUser1",
    "isy.task.tasks.hostnameTask2.passwort=TestPasswort1",
    "isy.task.tasks.hostnameTask2.bhkz=BHKZ1",
    "isy.task.tasks.hostnameTask2.ausfuehrung=ONCE",
    "isy.task.tasks.hostnameTask2.initial-delay=0s",
    "isy.task.watchdog.restart-interval=1s" })
public class TestHostnamenTask extends AbstractTaskTest {

    @Autowired
    @Qualifier("taskMonitor1")
    private TaskMonitor taskMonitor1;

    @Autowired
    @Qualifier("taskMonitor2")
    private TaskMonitor taskMonitor2;

    @Test
    public void testHostnameTask() throws Exception {
        taskScheduler.starteKonfigurierteTasks();

        SECONDS.sleep(1);

        taskScheduler.shutdownMitTimeout(3);

        assertFalse(taskMonitor1.isLetzteAusfuehrungErfolgreich());
        assertTrue(taskMonitor2.isLetzteAusfuehrungErfolgreich());
    }
}
