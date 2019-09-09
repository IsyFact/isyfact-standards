package de.bund.bva.isyfact.batchrahmen.batch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.core.annotation.AliasFor;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnMissingClass("de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher")
public @interface ExcludeFromBatchContext {
    @AliasFor(annotation = ConditionalOnMissingClass.class, attribute = "value")
    String[] value() default { "de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher" };
}
