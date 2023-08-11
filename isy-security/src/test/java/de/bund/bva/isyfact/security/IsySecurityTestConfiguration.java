package de.bund.bva.isyfact.security;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * Test configuration for isy-security. Only scans for auto configurations.
 * <p>
 * Tests that require no additional configuration only need to define {@code @SpringBootTest}.
 * Other tests need to import this as well as other required beans manually.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class IsySecurityTestConfiguration {

}
