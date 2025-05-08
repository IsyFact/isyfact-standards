package de.bund.bva.isyfact.sicherheit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Zum Sichern von Methoden-Aufrufen von Spring-Beans.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Deprecated
public @interface Gesichert {

    /** Eine Liste mit Rechten, welche vorhanden sein müssen. */
    String[] value() default {};

}
