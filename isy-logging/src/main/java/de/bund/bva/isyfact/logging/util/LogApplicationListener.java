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

import java.util.Objects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;

import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.IsyLoggerStandard;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.impl.Ereignisschluessel;

/**
 * Spring-ApplicationListener zum Loggen von Änderungen des Systemzustands.
 */
public class LogApplicationListener implements ApplicationListener<ApplicationEvent>,
        InitializingBean, ApplicationContextAware {

    /**
     * Systemproperty aus der die JAVA-Version gelesen wird.
     */
    private static final String SYSTEM_PROPERTY_JAVA_VERSION = "java.version";

    /**
     * Systemproperty aus der die Zeitzone gelesen wird.
     */
    private static final String SYSTEM_PROPERTY_ZEITZONE = "user.timezone";

    /**
     * Systemproperty aus der die Dateikodierung gelesen wird.
     */
    private static final String SYSTEM_PROPERTY_DATEIKODIERUNG = "file.encoding";

    /**
     * Logger der Klasse.
     */
    private static final IsyLoggerStandard LOGGER = IsyLoggerFactory.getLogger(LogApplicationListener.class);

    /**
     * Name des Systems.
     */
    private String systemname;

    /**
     * Art des Systems.
     */
    private String systemart;

    /**
     * Version des Systems.
     */
    private String systemversion;

    /**
     * Applicationcontext, in dem der Logger registriert ist.
     */
    private ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     *
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // Behandele nur Events des eigenen Kontextes, nicht Kind-Kontexte
        if (!(event instanceof ApplicationContextEvent) ||
                !Objects.equals(((ApplicationContextEvent) event).getApplicationContext(), applicationContext)) {
            return;
        }

        if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
            // ContextRefreshedEvent loggen
            LOGGER.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO02001.name(),
                    Ereignisschluessel.EISYLO02001.getNachricht(), systemname, systemart,
                    event.getClass().getSimpleName());
            // Systemversion loggen
            LOGGER.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO02003.name(),
                    Ereignisschluessel.EISYLO02003.getNachricht(), systemversion);

            // Java-Version loggen
            LOGGER.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO02004.name(),
                    Ereignisschluessel.EISYLO02004.getNachricht(), SYSTEM_PROPERTY_JAVA_VERSION,
                    System.getProperty(SYSTEM_PROPERTY_JAVA_VERSION));

            // Zeitzone loggen
            LOGGER.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO02004.name(),
                    Ereignisschluessel.EISYLO02004.getNachricht(), SYSTEM_PROPERTY_ZEITZONE,
                    System.getProperty(SYSTEM_PROPERTY_ZEITZONE));

            // Encoding loggen
            LOGGER.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO02004.name(),
                    Ereignisschluessel.EISYLO02004.getNachricht(), SYSTEM_PROPERTY_DATEIKODIERUNG,
                    System.getProperty(SYSTEM_PROPERTY_DATEIKODIERUNG));

            // Max Heap-Size loggen
            LOGGER.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO02004.name(),
                    Ereignisschluessel.EISYLO02004.getNachricht(), "maxMemory",
                    Runtime.getRuntime().maxMemory());

        } else { // Context Closed
            LOGGER.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO02002.name(),
                    Ereignisschluessel.EISYLO02002.getNachricht(), systemname, systemart,
                    event.getClass().getSimpleName());
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (systemart == null) {
            throw new IllegalArgumentException("Property 'systemart' muss gesetzt werden");
        } else if (systemname == null) {
            throw new IllegalArgumentException("Property 'systemname' muss gesetzt werden");
        } else if (systemversion == null) {
            throw new IllegalArgumentException("Property 'systemversion' muss gesetzt werden");
        }
    }

    /**
     * Setzt den Wert des Attributs 'systemname'.
     *
     * @param systemname Neuer Wert des Attributs.
     */
    public void setSystemname(String systemname) {
        this.systemname = systemname;
    }

    /**
     * Setzt den Wert des Attributs 'systemart'.
     *
     * @param systemart Neuer Wert des Attributs.
     */
    public void setSystemart(String systemart) {
        this.systemart = systemart;
    }

    /**
     * Setzt den Wert des Attributs 'systemversion'.
     *
     * @param systemversion Neuer Wert des Attributs.
     */
    public void setSystemversion(String systemversion) {
        this.systemversion = systemversion;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
