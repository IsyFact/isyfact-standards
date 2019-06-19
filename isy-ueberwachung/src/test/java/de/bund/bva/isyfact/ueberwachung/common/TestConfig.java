package de.bund.bva.isyfact.ueberwachung.common;

import de.bund.bva.isyfact.ueberwachung.common.ServiceStatistik;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
class TestConfig {

    @Bean
    public ServiceStatistik serviceStatistikMBean(MeterRegistry meterRegistry) {
        return new ServiceStatistik(meterRegistry, Tags.of("testKey", "testValue"));
    }
}
