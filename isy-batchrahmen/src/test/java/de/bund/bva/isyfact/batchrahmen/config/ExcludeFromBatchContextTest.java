package de.bund.bva.isyfact.batchrahmen.config;

import de.bund.bva.isyfact.batchrahmen.batch.annotation.ExcludeFromBatchContext;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher;
import org.junit.Test;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ExcludeFromBatchContextTest {

    private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner().withConfiguration(UserConfigurations.of(AppConfig.class));

    @Test
    public void annotatedBeansNotInContextWhenRunningAsBatch() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("coreBean");
            assertThat(context).doesNotHaveBean("webGuiBean");
        });
    }

    @Test
    public void annotatedBeanInContextWhenNotRunningAsBatch() {
        contextRunner.withClassLoader(new FilteredClassLoader(BatchLauncher.class))
            .run(context -> {
                assertThat(context).hasBean("coreBean");
                assertThat(context).hasBean("webGuiBean");
            });
    }

    @Configuration
    static class AppConfig {

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
