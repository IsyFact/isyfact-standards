package de.bund.bva.isyfact.logging.util;

/*
 * #%L
 * isy-logging
 * %%
 *
 * %%
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * #L%
 */

import org.junit.Test;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.logging.AbstractLogTest;

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
