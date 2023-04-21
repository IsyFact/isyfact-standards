package de.bund.bva.isyfact.security.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import de.bund.bva.isyfact.security.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.Berechtigungsmanager;
import de.bund.bva.isyfact.security.DefaultBerechtigungsmanager;
import de.bund.bva.isyfact.security.Security;
import de.bund.bva.isyfact.security.authentication.RolePrivilegeGrantedAuthoritiesConverter;
import de.bund.bva.isyfact.security.config.IsySecurityConfigurationProperties;
import de.bund.bva.isyfact.security.core.DefaultSecurity;
import de.bund.bva.isyfact.security.xmlparser.RolePrivilegesMapper;

/**
 * Main AutoConfiguration class providing beans and configurations that are always required when using isy-security.
 */
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
    public RolePrivilegesMapper rolePrivilegesMapper() {
        return new RolePrivilegesMapper(isySecurityConfigurationProperties().getRolePrivilegeMappingFile());
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
        RolePrivilegeGrantedAuthoritiesConverter grantedAuthoritiesConverter =
                new RolePrivilegeGrantedAuthoritiesConverter(rolePrivilegesMapper());
        grantedAuthoritiesConverter.setRolesClaimName(isySecurityConfigurationProperties().getRolesClaimName());

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(RolePrivilegeGrantedAuthoritiesConverter.AUTHORITY_PREFIX);
    }

    @Bean
    public Berechtigungsmanager berechtigungsmanager() {
        return new DefaultBerechtigungsmanager(isySecurityConfigurationProperties().getRolesClaimName());
    }

    @Bean
    @ConditionalOnMissingBean
    Security security(
            RolePrivilegesMapper rolePrivilegesMapper,
            Berechtigungsmanager berechtigungsmanager,
            @Nullable Authentifizierungsmanager authentifizierungsmanager
    ) {
        return new DefaultSecurity(
                rolePrivilegesMapper,
                berechtigungsmanager,
                authentifizierungsmanager
        );
    }
}
