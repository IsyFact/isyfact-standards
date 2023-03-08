package de.bund.bva.isyfact.security.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import de.bund.bva.isyfact.security.autoconfigure.IsySecurityAutoConfiguration;

@SpringBootApplication
@Import(IsySecurityAutoConfiguration.class)
public class IsySpringBootApplication {

    public static void main(String... args) {
        SpringApplication.run(IsySpringBootApplication.class, args);
    }

}
