package de.bund.bva.isyfact.task;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.AuthenticationException;

import de.bund.bva.isyfact.task.test.config.TestConfig;
import de.bund.bva.isyfact.task.util.TaskCounterBuilder;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Import(ProgrammaticallyScheduledTask.class)
@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test",
        "isy.logging.anwendung.version=test",
        "isy.task.tasks.programmaticallyScheduledTask-run.host=.*" })
public class TestTaskScheduledProgrammatically {

    @Autowired
    private MeterRegistry registry;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private ProgrammaticallyScheduledTask task;

    @Test
    public void testScheduleManuell() throws Exception {
        String className = ProgrammaticallyScheduledTask.class.getSimpleName();
        String annotatedMethodName = "run";

        taskScheduler.schedule(task, Instant.now());

        SECONDS.sleep(1);

        Counter successCounter = TaskCounterBuilder.successCounter(className, annotatedMethodName, registry);
        Counter failureCounter = TaskCounterBuilder.failureCounter(className, annotatedMethodName,
                AuthenticationException.class.getSimpleName(), registry);

        assertEquals(1, successCounter.count(), 0.0);
        assertEquals(0, failureCounter.count(), 0.0);
    }
}
