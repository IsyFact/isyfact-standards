package de.bund.bva.isyfact.security.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.bund.bva.isyfact.security.autoconfigure.IsySecurityAutoConfiguration;

/**
 * Test app isy-security. Only uses the autoconfiguration in {@link IsySecurityAutoConfiguration}.
 * All other beans required for tests should be imported manually.
 */
@SpringBootApplication(scanBasePackageClasses = IsySecurityAutoConfiguration.class)
public class IsySecurityTestApplication {

    public static void main(String... args) {
        SpringApplication.run(IsySecurityTestApplication.class, args);
    }

}
