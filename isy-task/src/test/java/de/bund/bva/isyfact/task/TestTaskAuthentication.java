package de.bund.bva.isyfact.task;

import static java.util.concurrent.TimeUnit.SECONDS;

import de.bund.bva.isyfact.task.test.TestTaskRunAssertion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.sicherheit.common.exception.AutorisierungFehlgeschlagenException;
import de.bund.bva.isyfact.task.test.config.TestConfig;

import io.micrometer.core.instrument.MeterRegistry;

@RunWith(SpringRunner.class)
@Import(TestTaskAuthenticationTasks.class)
@SpringBootTest(classes = { TestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test", "isy.logging.anwendung.version=test",
    "isy.task.authentication.enabled=true",
    "spring.task.scheduling.pool.size=2",
    "isy.task.tasks.testTaskAuthenticationTasks-scheduledTaskSecured.host=.*",
    "isy.task.tasks.testTaskAuthenticationTasks-scheduledTaskSecured.benutzer=TestUser1",
    "isy.task.tasks.testTaskAuthenticationTasks-scheduledTaskSecured.passwort=TestPasswort1",
    "isy.task.tasks.testTaskAuthenticationTasks-scheduledTaskSecured.bhkz=BHKZ1",
    "isy.task.tasks.testTaskAuthenticationTasks-scheduledTaskSecuredInsufficientRights.host=.*",
    "isy.task.tasks.testTaskAuthenticationTasks-scheduledTaskSecuredInsufficientRights.benutzer=TestUser2",
    "isy.task.tasks.testTaskAuthenticationTasks-scheduledTaskSecuredInsufficientRights.passwort=TestPasswort2",
    "isy.task.tasks.testTaskAuthenticationTasks-scheduledTaskSecuredInsufficientRights.bhkz=BHKZ1"})
public class TestTaskAuthentication {

    @Autowired
    private MeterRegistry registry;

    @Test
    public void testTaskSecured() throws Exception {
        String className = TestTaskAuthenticationTasks.class.getSimpleName();
        String annotatedMethodName = "scheduledTaskSecured";

        SECONDS.sleep(5);

        TestTaskRunAssertion.assertTaskSuccess(className, annotatedMethodName, registry,
            AutorisierungFehlgeschlagenException.class.getSimpleName());
    }

    @Test
    public void testTaskSecuredInsufficientRights() throws Exception {
        String className = TestTaskAuthenticationTasks.class.getSimpleName();
        String annotatedMethodName = "scheduledTaskSecuredInsufficientRights";

        SECONDS.sleep(5);

        TestTaskRunAssertion.assertTaskFailure(className, annotatedMethodName, registry,
            AutorisierungFehlgeschlagenException.class.getSimpleName());
    }
}
