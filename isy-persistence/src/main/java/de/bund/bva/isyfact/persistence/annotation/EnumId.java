package de.bund.bva.isyfact.persistence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Diese Annotation kennzeichnet ein Schlüsselfeld eines Enums. Sie muss im Enum an die get-Methode gesetzt
 * werden, die den Schlüssel liefert.
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumId {
}
