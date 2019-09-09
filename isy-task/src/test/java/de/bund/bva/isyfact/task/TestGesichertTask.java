package de.bund.bva.isyfact.task;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.isyfact.task.test.config.TestConfig;
import de.bund.bva.isyfact.task.test.config.TestTaskGesichertConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = { TestConfig.class,
    TestTaskGesichertConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test",
        "isy.logging.anwendung.version=test",
        "isy.task.authentication.enabled=true",
        "isy.task.tasks.gesichertTask1.benutzer=TestUser1",
        "isy.task.tasks.gesichertTask1.passwort=TestPasswort1",
        "isy.task.tasks.gesichertTask1.bhkz=BHKZ1",
        "isy.task.tasks.gesichertTask1.ausfuehrung=ONCE",
        "isy.task.tasks.gesichertTask1.initial-delay=0s",
        "isy.task.tasks.gesichertTask2.benutzer=TestUser2",
        "isy.task.tasks.gesichertTask2.passwort=TestPasswort2",
        "isy.task.tasks.gesichertTask2.bhkz=BHKZ1",
        "isy.task.tasks.gesichertTask2.ausfuehrung=ONCE",
        "isy.task.tasks.gesichertTask2.initial-delay=0s",
        "isy.task.watchdog.restart-interval=1s" })
public class TestGesichertTask extends AbstractTaskTest {

    @Autowired
    private IsyTaskConfigurationProperties configurationProperties;

    @Autowired
    @Qualifier("taskMonitor1")
    private TaskMonitor taskMonitor1;

    @Autowired
    @Qualifier("taskMonitor2")
    private TaskMonitor taskMonitor2;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS");

    @Before
    public void setup() throws Exception {
        configurationProperties.getDefault().setHost(InetAddress.getLocalHost().getHostName());
        LocalDateTime time1 = DateTimeUtil.localDateTimeNow().plusSeconds(1);
        LocalDateTime time2 = time1.plusSeconds(1);
        configurationProperties.getTasks().get("gesichertTask1").setZeitpunkt(time1.format(formatter));
        configurationProperties.getTasks().get("gesichertTask2").setZeitpunkt(time2.format(formatter));
    }

    @Test
    public void testGesicherterTask() throws Exception {
        taskScheduler.starteKonfigurierteTasks();

        SECONDS.sleep(1);

        taskScheduler.shutdownMitTimeout(2);

        assertTrue(taskMonitor1.isLetzteAusfuehrungErfolgreich());
        assertFalse(taskMonitor2.isLetzteAusfuehrungErfolgreich());
        assertTrue(taskMonitor2.getLetzterFehlerNachricht().startsWith(
            "#SIC2051 Die Autorisierung ist fehlgeschlagen. Das für diese Aktion erforderliche Recht ist nicht vorhanden. Recht1 "));
    }
}
