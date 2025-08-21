package de.bund.bva.isyfact.batchrahmen;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
public class OverrideSecurityConfig {
    @Bean
    InMemoryClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties) {
        var mapper = new OAuth2ClientPropertiesMapper(properties);
        List<ClientRegistration> registrations = new ArrayList<>(mapper.asClientRegistrations().values());
        return new InMemoryClientRegistrationRepository(registrations);
    }
}
