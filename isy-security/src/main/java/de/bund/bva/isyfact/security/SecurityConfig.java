package de.bund.bva.isyfact.security;

import de.bund.bva.isyfact.security.xmlparser.RolePrivilegeMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private String roleMappingFilePath;

    /**
     * Sicherheitskonfiguration der Endpunkte
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/rest/open").permitAll()
                .antMatchers("/rest/oauth").authenticated()
                .antMatchers("/rest/oauth_user").authenticated()
                .antMatchers("/rest/oauth_admin").hasAuthority("PRIV_admin_privilege0")
                .antMatchers("/rest/batch").permitAll()
                .antMatchers("/rest/extra_claim").authenticated()
                .antMatchers("/rest/get_roles").authenticated()
                .antMatchers("/rest/get_privileges").authenticated()
                .antMatchers("/rest/has_privilege").authenticated()
                .antMatchers("/rest/check_privilege").authenticated()
                .antMatchers("/rest/redirect").authenticated()
                .antMatchers("/rest/propagate_token").authenticated()
                .antMatchers("/**").denyAll()
                .and().oauth2ResourceServer().jwt();
        http.addFilterBefore(logFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    public RolePrivilegeMapper rolePrivilegeMapper() {
        return new RolePrivilegeMapper(roleMappingFilePath);
    }

    /**
     * Statt dem {@link org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter}
     * wird hier der neue {@link RolePrivilegeGrantedAuthoritiesConverter} registriert.
     * Außerdem werden die Rollen aus dem Claim "roles" statt aus "scopes" oder "scp" geladen.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        RolePrivilegeGrantedAuthoritiesConverter grantedAuthoritiesConverter = new RolePrivilegeGrantedAuthoritiesConverter(rolePrivilegeMapper()) { };
        grantedAuthoritiesConverter.setRolesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter
                = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }

    @Bean
    public AccessDecisionAdapter adapter() {
        return new AccessDecisionAdapter(new PrivilegeVoter());
    }
}

