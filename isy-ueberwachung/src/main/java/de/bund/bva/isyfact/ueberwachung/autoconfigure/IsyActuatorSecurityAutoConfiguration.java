package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import static de.bund.bva.isyfact.security.authentication.RolePrivilegeGrantedAuthoritiesConverter.AUTHORITY_PREFIX;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import de.bund.bva.isyfact.ueberwachung.config.ActuatorSecurityConfigurationProperties;
/* tag::actuatorSecurity[] */

/**
 * Configures HttpBasic authentication for actuator endpoints.
 */
@AutoConfiguration
@EnableWebSecurity
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
public class IsyActuatorSecurityAutoConfiguration {

    /** Enpoint role to identify the actuator Enpoint Admin. */
    public static final String ENDPOINT_ROLE = "ENDPOINT_ADMIN";

    @Bean
    @Order(1)
    @ConditionalOnMissingBean(name = "actuatorSecurityFilterChain")
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests(requests -> requests
                .anyRequest().hasRole(ENDPOINT_ROLE))
            .httpBasic(withDefaults());
        return http.build();
    }

    /**
     * In memory authentication with configured user.
     * Username can be null (left out of configuration) to secure endpoints without configuring a user to access them.
     * If configured, username must contain at least one non-whitespace character.
     *
     * @return {@link UserDetailsService}
     */
    @Bean
    @ConditionalOnProperty("isy.ueberwachung.security.username")
    @ConditionalOnMissingBean(name = "actuatorUserDetailsService")
    public UserDetailsService actuatorUserDetailsService(PasswordEncoder passwordEncoder,
                                                         ActuatorSecurityConfigurationProperties properties) {
        UserDetails actuatorEndpointUser = User
            // Username must contain at least one non-whitespace character
            .withUsername(properties.getUsername())
            .password(passwordEncoder.encode(properties.getPassword()))
            .authorities(AUTHORITY_PREFIX + ENDPOINT_ROLE)
            .build();
        return new InMemoryUserDetailsManager(actuatorEndpointUser);
    }

    /**
     * Password encoder for in memory authentication.
     *
     * @return {@link BCryptPasswordEncoder}
     */
    @Bean
    @ConditionalOnProperty("isy.ueberwachung.security.username")
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration properties for actuator security.
     * <p>
     * The prefix is "isy.ueberwachung.security".
     * </p>
     *
     * @return {@link ActuatorSecurityConfigurationProperties}
     */
    @Bean
    @ConfigurationProperties(prefix = "isy.ueberwachung.security")
    public ActuatorSecurityConfigurationProperties actuatorSecurityConfigurationProperties() {
        return new ActuatorSecurityConfigurationProperties();
    }
}
/* end::actuatorSecurity[] */
