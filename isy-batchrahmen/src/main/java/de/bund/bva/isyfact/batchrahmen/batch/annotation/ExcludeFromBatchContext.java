package de.bund.bva.isyfact.batchrahmen.batch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Annotation for marking beans which are to be excluded from the batch context.
 */
@Target({ ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnProperty(value = "isy.batchrahmen.batch-context", havingValue = "false", matchIfMissing = true)
public @interface ExcludeFromBatchContext {

}
