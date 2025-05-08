package de.bund.bva.isyfact.logging.util;



import de.bund.bva.isyfact.logging.AbstractLogTest;

import org.junit.Test;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Testfälle des LogApplicationListeners.
 */
public class ApplicationListenerTest extends AbstractLogTest {

    /**
     * Testet die Erstellung von Logeinträgen beim Hoch- und Herunterfahren des Application-Context mit Hilfe
     * des LogApplicationContextListener.
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
