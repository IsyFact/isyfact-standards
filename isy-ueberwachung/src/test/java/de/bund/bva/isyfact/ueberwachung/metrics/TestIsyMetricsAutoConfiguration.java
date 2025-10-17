package de.bund.bva.isyfact.ueberwachung.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyMetricsAutoConfiguration;
import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyLoadbalancerAutoConfiguration;
import de.bund.bva.isyfact.ueberwachung.metrics.impl.DefaultServiceStatistik;

public class TestIsyMetricsAutoConfiguration {
    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(TestConfig_basicServiceStatistikImpl.class)
                    .withConfiguration(AutoConfigurations.of(IsyLoadbalancerAutoConfiguration.class))
                    .withConfiguration(AutoConfigurations.of(IsyMetricsAutoConfiguration.class));

    private final ApplicationContextRunner contextRunner_withCustomServiceStatistik =
            new ApplicationContextRunner()
                    .withUserConfiguration(TestConfig_basicServiceStatistikImpl.class)
                    .withUserConfiguration(TestConfig_customServiceStatisticImpl.class)
                    .withConfiguration(AutoConfigurations.of(IsyLoadbalancerAutoConfiguration.class))
                    .withConfiguration(AutoConfigurations.of(IsyMetricsAutoConfiguration.class));

    @Test
    public void context_hasServiceStatistikImpl(){
        contextRunner.run(context ->
            assertThat(context)
                    .hasSingleBean(ServiceStatistik.class)
                    .hasSingleBean(DefaultServiceStatistik.class));
    }

    @Test
    public void context_hasBothCustomAnBasicServiceStatistikImpl(){
        contextRunner_withCustomServiceStatistik.run(context ->
            assertThat(context)
                    .hasSingleBean(DefaultServiceStatistik.class)
                    .hasBean("customServiceStatistik")
                    .getBeans(ServiceStatistik.class).hasSize(2));
    }

    @TestConfiguration
    public static class TestConfig_basicServiceStatistikImpl {
        @Bean
        public ServiceStatistik serviceStatistik() {
            return new DefaultServiceStatistik("testKey", "testValue");
        }
    }

    @TestConfiguration
    public static class TestConfig_customServiceStatisticImpl {
        @Bean
        public ServiceStatistik customServiceStatistik() {
            return Mockito.mock(ServiceStatistik.class);
        }
    }
}
