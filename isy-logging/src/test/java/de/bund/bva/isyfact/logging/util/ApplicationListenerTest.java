package de.bund.bva.isyfact.logging.util;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.logging.AbstractLogTest;

/**
 * LogApplicationListener test cases.
 */
public class ApplicationListenerTest extends AbstractLogTest {

    /**
     * Tests the creation of log entries when the application context is started and shut down using
     * the LogApplicationContextListener.
     */
    @Test
    public void testApplicationContextLogs() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ApplicationListenerTest.ApplicationListenerTestConfig.class);
        context.start();
        context.publishEvent(new ApplicationEvent(this) {
        });
        context.stop();
        context.close();

        AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
        childContext.setParent(context);
        childContext.register(ApplicationListenerChildConfig.class);
        childContext.refresh();
        pruefeLogdatei("testApplicationContext");
    }

    @Configuration
    static class ApplicationListenerTestConfig {

        @Bean
        LogApplicationListener statusLogger() {
            return new LogApplicationListener("RegXYZ", "REG", "1.1.1");
        }
    }

    @Configuration
    static class ApplicationListenerChildConfig {}
}
