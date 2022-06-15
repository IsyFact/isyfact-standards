package de.bund.bva.isyfact.ueberwachung.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Order(99) // apply before IsyKeycloakWebSecurityConfigurerAdapter
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
public class LoadbalancerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * Path where Loadbalancer is publicly available.
     */
    public static final String LOADBALANCER_SERVLET_PATH = "/Loadbalancer";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers(LOADBALANCER_SERVLET_PATH)
                .and()
                .authorizeRequests().anyRequest().permitAll();
    }
}
