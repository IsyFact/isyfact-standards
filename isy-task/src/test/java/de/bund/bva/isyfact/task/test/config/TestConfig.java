package de.bund.bva.isyfact.task.test.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableAutoConfiguration
@EnableMethodSecurity(securedEnabled = true)
public class TestConfig {
}
