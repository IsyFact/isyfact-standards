package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/* tag::actuatorSecurity[] */

/**
 * Configures OAuth2 authentication for actuator endpoints.
 * actuator/health needs no authentication
 */
@AutoConfiguration
@EnableWebSecurity
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
public class IsyActuatorSecurityAutoConfiguration {

    /**
     * OpenID connect certificate‑endpoint to validate token against.
     */
    @Value("${isy.ueberwachung.security.jwk-set-uri:}")
    private String jwkSetUri;

    @Bean
    @Order(10)
    @ConditionalOnMissingBean(name = "actuatorSecurityFilterChain")
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sessionConfig ->
                        sessionConfig
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (jwkSetUri != null && !jwkSetUri.isEmpty()) {
            // secure actuator endpoints with JWT token, except health
            http.oauth2ResourceServer(oauth -> oauth
                    .jwt(jwt -> jwt
                            .jwkSetUri(jwkSetUri)
                    )
            );
        } else {
            http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        }

        return http.build();
    }

    /**
     * Delivers 401 every request, if no decoder is defined.
     */
    @Bean
    @ConditionalOnMissingBean(name = "jwtDecoder")
    public JwtDecoder jwtDecoder() {
        String secret = UUID.randomUUID().toString();
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")
        ).build();
    }

}
/* end::actuatorSecurity[] */
