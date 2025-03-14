package de.bund.bva.isyfact.task;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.test.TestTaskRunAssertion;
import de.bund.bva.isyfact.task.test.config.TestConfig;

import io.micrometer.core.instrument.MeterRegistry;

@Import(TestHostHandlerTasks.class)
@SpringBootTest(classes = { TestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test", "isy.logging.anwendung.version=test",
        "isy.task.tasks.testHostHandlerTasks-scheduledTaskWithCorrectHostname.host=.*",
        "isy.task.tasks.testHostHandlerTasks-scheduledTaskWithWrongHostname.host=thisisthewronghostname" })
public class TestHostHandler {

    @Autowired
    private MeterRegistry registry;

    @Test
    public void testTaskWithCorrectHostname() throws Exception {
        String className = TestHostHandlerTasks.class.getSimpleName();
        String annotatedMethodName = "scheduledTaskWithCorrectHostname";

        SECONDS.sleep(1);

        TestTaskRunAssertion.assertTaskSuccess(className, annotatedMethodName, registry,
                HostNotApplicableException.class.getSimpleName());
    }

    @Test
    public void testTaskWithWrongHostname() throws Exception {
        String className = TestHostHandlerTasks.class.getSimpleName();
        String annotatedMethodName = "scheduledTaskWithWrongHostname";

        SECONDS.sleep(1);

        TestTaskRunAssertion.assertTaskFailure(className, annotatedMethodName, registry,
                HostNotApplicableException.class.getSimpleName());
    }
}
