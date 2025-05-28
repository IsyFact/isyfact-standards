package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import de.bund.bva.isyfact.ueberwachung.config.ActuatorSecurityConfigurationProperties;

/**
 * Configures Http Basic Authentication for actuator endpoints.
 */
@Configuration
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
public class IsyActuatorSecurityAutoConfiguration {

    /** Endpoint role to identify the Actuator Endpoint Admin. */
    public static final String ENDPOINT_ROLE = "ENDPOINT_ADMIN";

    /**
     * Security Properties.
     *
     * @return {@link ActuatorSecurityConfigurationProperties}
     */
    @Bean
    @ConfigurationProperties(prefix = "isy.ueberwachung.security")
    public ActuatorSecurityConfigurationProperties actuatorSecurityConfigurationProperties() {
        return new ActuatorSecurityConfigurationProperties();
    }

    /**
     * Password encoder for in-memory authentication.
     *
     * @return {@link BCryptPasswordEncoder}
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("isy.ueberwachung.security.username")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(1)
    @ConditionalOnProperty("isy.ueberwachung.security.username")
    public static class HttpBasicWebEndpointSecurityConfiguration extends WebSecurityConfigurerAdapter {
        /**
         * Monitoring user configuration.
         */
        private final ActuatorSecurityConfigurationProperties properties;

        /**
         * Password encoder implementation.
         */
        private final PasswordEncoder passwordEncoder;

        public HttpBasicWebEndpointSecurityConfiguration(ActuatorSecurityConfigurationProperties properties, PasswordEncoder passwordEncoder) {
            this.properties = properties;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                .withUser(properties.getUsername())
                .password(passwordEncoder.encode(properties.getPassword()))
                .roles(ENDPOINT_ROLE);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeHttpRequests(requests -> requests
                    .antMatchers("/actuator/health").permitAll()
                    .anyRequest().hasRole(ENDPOINT_ROLE))
                .httpBasic();
        }
    }

}
