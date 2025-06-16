package de.bund.bva.isyfact.persistence.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Diese Annotation definiert den zu persistierenden Wert für eine Enumausprägung. Sie muss an jede
 * Enum-Konstante gesetzt werden.
 * 
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PersistentValue {

    /**
     * Der zu persistierende Wert für eine Enumausprägung.
     */
    String value();
}
