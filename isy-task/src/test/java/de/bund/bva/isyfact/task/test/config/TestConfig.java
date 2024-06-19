package de.bund.bva.isyfact.task.test.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import de.bund.bva.isyfact.util.spring.MessageSourceHolder;

@Configuration
@EnableAutoConfiguration
@EnableMethodSecurity(securedEnabled = true)
public class TestConfig {

    @Bean
    public MessageSourceHolder messageSourceHolder() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("resources/isy-task/nachrichten/ereignisse",
                "resources/isy-task/nachrichten/hinweise");

        MessageSourceHolder messageSourceHolder = new MessageSourceHolder();
        messageSourceHolder.setMessageSource(messageSource);

        return messageSourceHolder;
    }
}
