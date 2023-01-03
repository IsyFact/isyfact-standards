package de.bund.bva.isyfact.task.demo;

import de.bund.bva.isyfact.task.TestHostHandlerTasks;
import de.bund.bva.isyfact.task.TestTaskDeactivatedTasks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.bund.bva.isyfact.task.TestTaskAuthenticationTasks;
import de.bund.bva.isyfact.task.test.config.TestConfig;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
@Import(TestConfig.class)
public class SchedulerDemoConfiguration {

    @Bean
    TestTaskAuthenticationTasks testTasks() {
        return new TestTaskAuthenticationTasks();
    }

    @Bean
    TestHostHandlerTasks testHostHandlerTasks() {
        return new TestHostHandlerTasks();
    }

    @Bean
    TestTaskDeactivatedTasks testDeactivatedTasks() {
        return new TestTaskDeactivatedTasks();
    }

}
