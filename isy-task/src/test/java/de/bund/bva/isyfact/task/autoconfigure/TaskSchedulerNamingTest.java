package de.bund.bva.isyfact.task.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.bund.bva.isyfact.task.TaskScheduler;

/**
 * Tests if the double declaration of bean with name "taskScheduler" is fixed. (IFS-744)
 */
public class TaskSchedulerNamingTest {

    /**
     * Enable autoconfiguration conditions evaluation report.
     */
    private final ConditionEvaluationReportLoggingListener initializer = new ConditionEvaluationReportLoggingListener(
            LogLevel.DEBUG);

    /**
     * Sets the contextRunner.
     */
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner(
            AnnotationConfigApplicationContext::new)
            .withInitializer(initializer)
            .withUserConfiguration(TestConfig.class)
            .withConfiguration(AutoConfigurations.of(
                            IsyTaskAutoConfiguration.class,
                            TaskSchedulingAutoConfiguration.class
                    )
            );

    @Test
    public void testCheckApplicationContextTest() {
        contextRunner.run(context -> {
            assertThat(context)
                    .hasSingleBean(TaskScheduler.class)
                    .hasBean("isyTaskScheduler")
                    .hasSingleBean(org.springframework.scheduling.TaskScheduler.class)
                    .hasBean("taskScheduler");

        });
    }

    @Configuration
    @EnableScheduling
    static class TestConfig {
    }
}
