package de.bund.bva.isyfact.task.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.bund.bva.isyfact.task.TestHostHandlerTasks;
import de.bund.bva.isyfact.task.TestTaskAuthenticationTasks;
import de.bund.bva.isyfact.task.TestTaskDeactivatedTasks;
import de.bund.bva.isyfact.task.test.config.TestConfig;

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
