package de.bund.bva.isyfact.batchrahmen;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.batchrahmen.batch.annotation.ExcludeFromBatchContext;

/**
 * Test configuration containing a bean annotated with @ExcludeFromBatchContext which throws a runtime exception
 * when the bean is not excluded.
 */
@Configuration
public class AnnotatedBatchrahmenTestConfig {

    @Bean
    @ExcludeFromBatchContext
    public Object exceptionBean() {
        throw new RuntimeException("The annotation @ExcludeFromBatchContext did not work. This bean should have been excluded.");
    }

}
