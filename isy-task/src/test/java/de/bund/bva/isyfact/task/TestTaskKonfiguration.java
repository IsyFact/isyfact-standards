package de.bund.bva.isyfact.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.task.exception.TaskKonfigurationInvalidException;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfigurationVerwalter;
import de.bund.bva.isyfact.task.sicherheit.impl.NoOpAuthenticator;
import de.bund.bva.isyfact.task.test.config.TestConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"isy.logging.anwendung.name=test",
                  "isy.logging.anwendung.typ=test",
                  "isy.logging.anwendung.version=test",
                  "isy.task.tasks.test.benutzer=Test1",
                  "isy.task.tasks.test.passwort=Test1",
                  "isy.task.tasks.test.bhkz=Test1"})
public class TestTaskKonfiguration {

    @Autowired
    private TaskKonfigurationVerwalter taskKonfigurationVerwalter;

    @Test
    public void erfolgreicherDurchlauf() {
        TaskKonfiguration taskKonfiguration = getTestTaskKonfiguration();
        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void taskIdNull() throws Exception {
        TaskKonfiguration taskKonfiguration = getTestTaskKonfiguration();

        taskKonfiguration.setTaskId(null);

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void authenticatorNull() throws Exception {
        TaskKonfiguration taskKonfiguration = getTestTaskKonfiguration();

        taskKonfiguration.setAuthenticator(null);

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void hostnameNull() throws Exception {
        TaskKonfiguration taskKonfiguration = getTestTaskKonfiguration();

        taskKonfiguration.setHostname(null);

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test(expected = TaskKonfigurationInvalidException.class)
    public void hostnameInvalideRegEx() throws Exception {
        TaskKonfiguration taskKonfiguration = getTestTaskKonfiguration();

        taskKonfiguration.setHostname("((\\");

        taskKonfigurationVerwalter.pruefeTaskKonfiguration(taskKonfiguration);
    }

    @Test
    public void hostnameValideRegEx() {
        TaskKonfiguration taskKonfiguration = getTestTaskKonfiguration();

        taskKonfiguration.setHostname("host.*");

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

        return taskKonfiguration;
    }
}
