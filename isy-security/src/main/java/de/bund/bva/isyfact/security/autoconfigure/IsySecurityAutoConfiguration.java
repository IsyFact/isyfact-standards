package de.bund.bva.isyfact.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import de.bund.bva.isyfact.security.authentication.RolePrivilegeGrantedAuthoritiesConverter;
import de.bund.bva.isyfact.security.config.IsySecurityConfigurationProperties;
import de.bund.bva.isyfact.security.core.Berechtigungsmanager;
import de.bund.bva.isyfact.security.core.IsyOAuth2Berechtigungsmanager;
import de.bund.bva.isyfact.security.core.IsyOAuth2Security;
import de.bund.bva.isyfact.security.core.Security;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
import de.bund.bva.isyfact.security.xmlparser.RolePrivilegesMapper;

/**
 * Main autoconfiguration for isy-security that creates all beans required for role privileges mapping and JWT conversion.
 */
@ConditionalOnClass(EnableWebSecurity.class)
@AutoConfiguration
@EnableConfigurationProperties
public class IsySecurityAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "isy.security")
    public IsySecurityConfigurationProperties isySecurityProperties() {
        return new IsySecurityConfigurationProperties();
    }

    @Bean
    public RolePrivilegesMapper rolePrivilegesMapper(IsySecurityConfigurationProperties isySecurityProperties) {
        return new RolePrivilegesMapper(isySecurityProperties.getRolePrivilegesMappingFile());
    }

    /**
     * Instead of a the {@link org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter}
     * we configure a custom {@link RolePrivilegeGrantedAuthoritiesConverter} that maps authorities to privileges and searches for them
     * in the configured JWT claim {@link IsySecurityConfigurationProperties#getRolesClaimName()} instead of "scopes" or "scp".
     *
     * @return the authentication converter with custom authority prefix and role to privilege mapping
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(RolePrivilegesMapper rolePrivilegesMapper,
                                                                 IsySecurityConfigurationProperties isySecurityProperties) {
        RolePrivilegeGrantedAuthoritiesConverter grantedAuthoritiesConverter =
                new RolePrivilegeGrantedAuthoritiesConverter(rolePrivilegesMapper);
        grantedAuthoritiesConverter.setRolesClaimName(isySecurityProperties.getRolesClaimName());

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(RolePrivilegeGrantedAuthoritiesConverter.AUTHORITY_PREFIX);
    }

    @Bean
    public Berechtigungsmanager berechtigungsmanager(IsySecurityConfigurationProperties isySecurityProperties) {
        return new IsyOAuth2Berechtigungsmanager(isySecurityProperties.getRolesClaimName());
    }

    @Bean
    @ConditionalOnMissingBean
    public Security security(RolePrivilegesMapper rolePrivilegesMapper, Berechtigungsmanager berechtigungsmanager,
                             @Nullable Authentifizierungsmanager authentifizierungsmanager
    ) {
        return new IsyOAuth2Security(rolePrivilegesMapper, berechtigungsmanager, authentifizierungsmanager);
    }

}
