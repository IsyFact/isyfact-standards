package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/* tag::actuatorSecurity[] */

/**
 * Configures HttpBasic authentication for actuator endpoints.
 * actuator/health needs no authentification
 */
@AutoConfiguration
@EnableWebSecurity
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
public class IsyActuatorSecurityAutoConfiguration {

    /** Endpoint role to identify the actuator Endpoint Admin. */
    public static final String ENDPOINT_ROLE = "ENDPOINT_ADMIN";
    public static final String URL = "jwk-set-uri";


    @Bean
    @Order(10)
    @ConditionalOnMissingBean(name = "actuatorSecurityFilterChain")
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
                        .anyRequest().hasRole(ENDPOINT_ROLE));

        http.sessionManagement(sessionConfig ->
                sessionConfig
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.oauth2ResourceServer((rs) ->
                rs.jwt((jwt) -> jwt.decoder(jwtDecoder()))
        );

        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "jwtDecoder")
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(URL).build();
    }
}
/* end::actuatorSecurity[] */
