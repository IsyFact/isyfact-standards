package de.bund.bva.isyfact.ueberwachung.common.impl;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
class TestConfig {

    @Bean
    public DefaultServiceStatistik serviceStatistikMBean() {
        return new DefaultServiceStatistik("testKey", "testValue");
    }
}
