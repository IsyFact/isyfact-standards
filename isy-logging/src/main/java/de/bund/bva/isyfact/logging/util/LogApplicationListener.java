package de.bund.bva.isyfact.logging.util;

import java.util.Objects;

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
public class LogApplicationListener implements ApplicationListener<ApplicationEvent>, ApplicationContextAware {

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
    private final String systemname;

    /**
     * Art des Systems.
     */
    private final String systemart;

    /**
     * Version des Systems.
     */
    private final String systemversion;

    /**
     * Applikationskontext des Loggers
     */
    private ApplicationContext applicationContext;

    /**
     * Erzeugt einen neuen LogApplicationListener.
     *
     * @param systemname    Wert des Attributs 'systemname'.
     * @param systemart     Wert des Attributs 'systemart'.
     * @param systemversion Wert des Attributs 'systemversion'.
     */
    public LogApplicationListener(String systemname, String systemart, String systemversion) {
        this.systemname = systemname;
        this.systemart = systemart;
        this.systemversion = systemversion;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // Behandele nur Events des Kontext, in dem der Logger registriert ist, keine Kind-Kontexte
        if (!(event instanceof ApplicationContextEvent)
                || !Objects.equals(applicationContext, ((ApplicationContextEvent) event).getApplicationContext())) {
            return;
        }

        if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
            LOGGER.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO02001.name(),
                    Ereignisschluessel.EISYLO02001.getNachricht(), systemname, systemart,
                    event.getClass().getSimpleName());
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

        } else {
            LOGGER.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO02002.name(),
                    Ereignisschluessel.EISYLO02002.getNachricht(), systemname, systemart,
                    event.getClass().getSimpleName());
        }

    }
}
