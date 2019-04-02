package de.bund.bva.isyfact.task;

import java.net.InetAddress;
import java.time.format.DateTimeFormatter;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.test.config.TestConfig;
import de.bund.bva.isyfact.task.test.config.TestTaskGesichertConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = { TestConfig.class, TestTaskGesichertConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE,
properties = {
    "isy.logging.anwendung.name=test",
    "isy.logging.anwendung.typ=test",
    "isy.logging.anwendung.version=test",
    "logging.level.root=info",
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
    "isy.task.watchdog.restart-interval=5s"
})
public class TestGesichertTask extends AbstractTaskTest {

    @Autowired
    private IsyTaskConfigurationProperties configurationProperties;

    @Before
    public void setup() throws Exception {
        configurationProperties.getDefault().setHost(InetAddress.getLocalHost().getHostName());
        String executionDateTime = DateTimeUtil.localDateTimeNow().plusSeconds(1)
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS"));
        configurationProperties.getTasks().get("gesichertTask1").setZeitpunkt(executionDateTime);
        configurationProperties.getTasks().get("gesichertTask2").setZeitpunkt(executionDateTime);
    }

    @Test
    public void testGesicherterTask() throws Exception {
        taskScheduler.starteKonfigurierteTasks();

        SECONDS.sleep(3);

        taskScheduler.shutdownMitTimeout(1);

        SECONDS.sleep(5);
        assertTrue(Boolean.valueOf(getMBeanAttribute("GesichertTask1", "LetzteAusfuehrungErfolgreich")));
        String letzterFehlerNachricht = getMBeanAttribute("GesichertTask2", "LetzterFehlerNachricht");
        assertTrue(letzterFehlerNachricht.startsWith("#SIC2051 Die Autorisierung ist fehlgeschlagen. Das für diese Aktion erforderliche Recht ist nicht vorhanden. Recht1 "));
    }
}
