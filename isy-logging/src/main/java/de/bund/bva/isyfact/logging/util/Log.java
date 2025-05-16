package de.bund.bva.isyfact.logging.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Diese Annotation wird bereitgestellt, um die Konfiguration des LoggingMethodInterceptors zu erleichtern.
 * Sie kann verwendet werden, um den Interceptor ausschließlich auf mit @Log annotierten Methoden anzuwenden.
 * Darüberhinaus besitzt die Annotation keine Semantik.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
public @interface Log {

}
