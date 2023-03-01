package de.bund.bva.isyfact.security.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import de.bund.bva.isyfact.security.authentication.RolePrivilegeGrantedAuthoritiesConverter;
import de.bund.bva.isyfact.security.config.IsySecurityConfigurationProperties;
import de.bund.bva.isyfact.security.xmlparser.RolePrivilegeMapper;

@Configuration
@EnableConfigurationProperties
@EnableGlobalMethodSecurity(securedEnabled = true)
public class IsySecurityAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "isy.security")
    public IsySecurityConfigurationProperties isySecurityConfigurationProperties() {
        return new IsySecurityConfigurationProperties();
    }

    @Bean
    public RolePrivilegeMapper rolePrivilegeMapper() {
        return new RolePrivilegeMapper(isySecurityConfigurationProperties().getRolePrivilegeMappingFile());
    }

    /**
     * Instead of a the {@link org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter}
     * we configure a custom {@link RolePrivilegeGrantedAuthoritiesConverter} that maps authorities to privileges and searches for them
     * in the configured JWT claim {@link IsySecurityConfigurationProperties#getRolesClaimName()} instead of "scopes" or "scp".
     *
     * @return the authentication converter with custom authority prefix and role to privilege mapping
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        RolePrivilegeGrantedAuthoritiesConverter grantedAuthoritiesConverter = new RolePrivilegeGrantedAuthoritiesConverter(
                rolePrivilegeMapper());
        grantedAuthoritiesConverter.setRolesClaimName(isySecurityConfigurationProperties().getRolesClaimName());

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(RolePrivilegeGrantedAuthoritiesConverter.AUTHORITY_PREFIX);
    }

}
