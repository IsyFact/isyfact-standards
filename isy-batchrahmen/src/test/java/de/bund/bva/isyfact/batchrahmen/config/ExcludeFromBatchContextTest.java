package de.bund.bva.isyfact.batchrahmen.config;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.batchrahmen.batch.annotation.ExcludeFromBatchContext;

/**
 * Test class for the ExcludeFromBatchContext annotation.
 */
public class ExcludeFromBatchContextTest {

    private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner().withConfiguration(UserConfigurations.of(AppConfig.class));

    /**
     * Verifies that no beans are excluded when running without the batch-context property set.
     */
    @Test
    public void annotatedBeansInContextWhenNotRunningAsBatch() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("coreBean");
            assertThat(context).hasBean("webGuiBean");
        });
    }

    /**
     * Verifies that the 'webGuiBean' is excluded when running with the batch-context property set.
     */
    @Test
    public void annotatedBeanNotInContextWhenRunningAsBatch() {
        contextRunner.withPropertyValues("isy.batchrahmen.batch-context", "true")
                     .run(context -> {
                         assertThat(context).hasBean("coreBean");
                         assertThat(context).doesNotHaveBean("webGuiBean");
                     });
    }

    @Test
    public void runBatchWithAnnotatedBean() {

    }

    @Configuration
    public static class AppConfig {

        @Bean
        public String coreBean() {
            return "coreBean";
        }

        @Bean
        @ExcludeFromBatchContext
        public String webGuiBean() {
            return "webGuiBean";
        }
    }
}
