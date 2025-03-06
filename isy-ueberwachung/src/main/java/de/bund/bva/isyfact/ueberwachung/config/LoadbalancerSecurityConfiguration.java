package de.bund.bva.isyfact.ueberwachung.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
public class LoadbalancerSecurityConfiguration {

    /**
     * Path where Loadbalancer is publicly available.
     */
    public static final String LOADBALANCER_SERVLET_PATH = "/Loadbalancer";

    /**
     * Filter to always allow access to "/Loadbalancer" Endpoint.
     * <p>The {@code @Order(99)} annotation applies the SecurityFilterChain before Beans without
     * an @Order annotation.
     *
     * @param http {@link HttpSecurity}
     *
     * @return {@link SecurityFilterChain}
     * @throws Exception if an error occurred when building the Object
     */
    @Bean
    @Order(99)
    SecurityFilterChain loadbalancerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new AntPathRequestMatcher("**"))
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(LOADBALANCER_SERVLET_PATH)
                                .permitAll()
        );
        return http.build();
    }
}
