package de.bund.bva.isyfact.logging.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Diese Annotation kennzeichnet eine Komponentengrenze, an der die Bean <code>componentLogInterceptor</code>
 * loggt.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Komponentengrenze {

}
