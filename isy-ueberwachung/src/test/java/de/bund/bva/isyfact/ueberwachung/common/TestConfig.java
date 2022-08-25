package de.bund.bva.isyfact.ueberwachung.common;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
class TestConfig {

    @Bean
    public ServiceStatistik serviceStatistikMBean() {
        return new ServiceStatistik("testKey", "testValue");
    }
}
