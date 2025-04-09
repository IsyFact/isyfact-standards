package de.bund.bva.isyfact.task;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import de.bund.bva.isyfact.task.exception.TaskDeactivatedException;
import de.bund.bva.isyfact.task.test.TestTaskRunAssertion;
import de.bund.bva.isyfact.task.test.config.TestConfig;

import io.micrometer.core.instrument.MeterRegistry;

@Import(TestTaskDeactivatedTasks.class)
@SpringBootTest(classes = { TestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "isy.logging.anwendung.name=test",
        "isy.logging.anwendung.typ=test",
        "isy.logging.anwendung.version=test",
        "isy.task.authentication.enabled=false",
        "isy.task.tasks.testTaskDeactivatedTasks-scheduledTaskDeactivated.host=.*",
        "isy.task.tasks.testTaskDeactivatedTasks-scheduledTaskDeactivated.deaktiviert=true",
        "isy.task.tasks.testTaskDeactivatedTasks-scheduledTaskActivated.host=.*",
        "isy.task.tasks.testTaskDeactivatedTasks-scheduledTaskActivated.deaktiviert=false" })
public class TestTaskDeactivation {

    @Autowired
    private MeterRegistry registry;

    @Test
    public void testTaskDeactivated() throws Exception {
        String className = TestTaskDeactivatedTasks.class.getSimpleName();
        String annotatedMethodName = "scheduledTaskDeactivated";

        SECONDS.sleep(1);

        TestTaskRunAssertion.assertTaskFailure(className, annotatedMethodName, registry,
                TaskDeactivatedException.class.getSimpleName());
    }

    @Test
    public void testTaskActivated() throws Exception {
        String className = TestTaskDeactivatedTasks.class.getSimpleName();
        String annotatedMethodName = "scheduledTaskActivated";

        SECONDS.sleep(1);

        TestTaskRunAssertion.assertTaskSuccess(className, annotatedMethodName, registry,
                TaskDeactivatedException.class.getSimpleName());
    }
}
