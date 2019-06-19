package de.bund.bva.isyfact.logging.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Diese Annotation kennzeichnet eine Systemgrenze, an der die Bean <code>boundaryLogInterceptor</code> loggt.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Systemgrenze {

}
