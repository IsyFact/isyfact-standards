package de.bund.bva.isyfact.logging;

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

import java.util.EventObject;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.bund.bva.isyfact.logging.util.LogApplicationListener;

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
        final String configLocation = "classpath:spring/applicationListener-test.xml";
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(configLocation);
        context.getBean("statusLogger");
        context.start();
        context.publishEvent(new ApplicationEvent(this) {
            /** UID. */
            private static final long serialVersionUID = 1L;
        });
        ClassPathXmlApplicationContext context2 = new ClassPathXmlApplicationContext(
                new String[]{configLocation}, context);
        context2.close();
        context.stop();
        context.close();
        pruefeLogdatei("testApplicationContext");
    }


    /**
     * Testet die verschiedenen Fehler, die bei der Initialisierung auftreten können.
     *
     * @throws Exception
     *             wenn beim Test eine Exception auftritt.
     */
    @Test
    public void testInitialisierungFehlerhaft() throws Exception {

        LogApplicationListener applicationListener = new LogApplicationListener();

        // systemart wurde nicht gesetzt
        try {
            applicationListener.afterPropertiesSet();
            Assert.fail("Es wurde keine IllegalArgumentException geworfen.");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Property 'systemart' muss gesetzt werden", iae.getMessage());
        }
        applicationListener.setSystemart("systemart");

        // systemname wurde nicht gesetzt
        try {
            applicationListener.afterPropertiesSet();
            Assert.fail("Es wurde keine IllegalArgumentException geworfen.");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Property 'systemname' muss gesetzt werden", iae.getMessage());
        }
        applicationListener.setSystemname("systemname");

        // systemversion wurde nicht gesetzt
        try {
            applicationListener.afterPropertiesSet();
            Assert.fail("Es wurde keine IllegalArgumentException geworfen.");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Property 'systemversion' muss gesetzt werden", iae.getMessage());
        }

        applicationListener.setSystemversion("systemversion");
        // Alle Parameter wurden gesetzt
        applicationListener.afterPropertiesSet();

    }

}
