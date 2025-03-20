package de.bund.bva.isyfact.batchrahmen.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

/**
 * !!! Workaround !!!
 * <p>
 * Since {@code WebApplicationType.NONE} is set in the BatchLauncher, the
 * {@link org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration}
 * is not loaded due to the annotation
 * {@code @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)}.
 * <p>
 * Therefore, the omitted beans must be provided manually. To provide a default, the Beans in this class
 * are copied from the package private Spring classes:
 * <ul>
 * <li>OAuth2ClientRegistrationRepositoryConfiguration</li>
 * <li>OAuth2WebSecurityConfiguration</li>
 * </ul>
 * and OAuth2ClientProperties are enabled.
 */
@Configuration
@EnableConfigurationProperties(OAuth2ClientProperties.class)
@ConditionalOnClass({ClientRegistration.class, EnableWebSecurity.class})
@Conditional(ClientsConfiguredCondition.class)
public class BatchSecurityConfiguration {

    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    InMemoryClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties) {
        var mapper = new OAuth2ClientPropertiesMapper(properties);
        List<ClientRegistration> registrations = new ArrayList<>(mapper.asClientRegistrations().values());
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    @ConditionalOnMissingBean
    OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }
}
