package de.bund.bva.isyfact.serviceapi.core.aop.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereit;

/**
 * Dummy Service, um die Ausführungsreihenfolge der Advisoren anhand der annotierten Methoden zu testen.
 */
public class LoggingKontextAdvisorService {

    @StelltLoggingKontextBereit
    @UnorderedDummyAnnotation
    public void stelltLoggingKontextBereitBevorUnordered() {

    }

    @UnorderedDummyAnnotation
    @StelltLoggingKontextBereit
    public void stelltLoggingKontextBereitNachUnordered() {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    public @interface UnorderedDummyAnnotation {}
}
