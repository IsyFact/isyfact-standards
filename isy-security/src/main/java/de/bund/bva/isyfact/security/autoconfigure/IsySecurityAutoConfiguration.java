package de.bund.bva.isyfact.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;

import de.bund.bva.isyfact.security.authentication.RolePrivilegeGrantedAuthoritiesConverter;
import de.bund.bva.isyfact.security.condition.TenantsNotEmptyCondition;
import de.bund.bva.isyfact.security.config.IsySecurityConfigurationProperties;
import de.bund.bva.isyfact.security.config.JWTConfigurationProperties;
import de.bund.bva.isyfact.security.core.Berechtigungsmanager;
import de.bund.bva.isyfact.security.core.IsyOAuth2Berechtigungsmanager;
import de.bund.bva.isyfact.security.core.IsyOAuth2Security;
import de.bund.bva.isyfact.security.core.Security;
import de.bund.bva.isyfact.security.core.TenantJwsKeySelector;
import de.bund.bva.isyfact.security.core.TenantJwtIssuerValidator;
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

    /**
     * Configuration for multi-tenancy support. This configuration is applied when the property
     * "spring.security.oauth2.resourceserver.jwt.issuer-uri" is either set to "false" or not specified
     * in the application properties, and tenant-specific configurations are provided in the application properties.
     */
    @Configuration
    @ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.issuer-uri", havingValue = "false", matchIfMissing = true)
    @Conditional(TenantsNotEmptyCondition.class)
    public static class MultiTenancyConfiguredDependentBeans {

        @Bean
        @ConfigurationProperties(prefix = "isy.security.oauth2.resourceserver.jwt")
        public JWTConfigurationProperties jwtConfigurationProperties() {
            return new JWTConfigurationProperties();
        }

        @Bean
        public TenantJwsKeySelector tenantJwsKeySelector(JWTConfigurationProperties jwtConfigurationProperties) {
            return new TenantJwsKeySelector(jwtConfigurationProperties);
        }

        @Bean
        public TenantJwtIssuerValidator tenantJwtIssuerValidator(JWTConfigurationProperties jwtConfigurationProperties) {
            return new TenantJwtIssuerValidator(jwtConfigurationProperties);
        }

        @Bean
        JWTProcessor<SecurityContext> jwtProcessor(TenantJwsKeySelector keySelector) {
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWTClaimsSetAwareJWSKeySelector(keySelector);
            return jwtProcessor;
        }

        @Bean
        JwtDecoder jwtDecoder(JWTProcessor<SecurityContext> jwtProcessor, OAuth2TokenValidator<Jwt> jwtValidator) {
            NimbusJwtDecoder decoder = new NimbusJwtDecoder(jwtProcessor);
            OAuth2TokenValidator<Jwt> validator =
                    new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefault(), jwtValidator);
            decoder.setJwtValidator(validator);
            return decoder;
        }
    }
}
