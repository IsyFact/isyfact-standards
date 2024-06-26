package de.bund.bva.isyfact.security.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.stereotype.Component;

import de.bund.bva.isyfact.security.config.JWTConfigurationProperties;

@Component
public class TenantJwtIssuerValidator implements OAuth2TokenValidator<Jwt> {

    /** The JWT properties. */
    private final JWTConfigurationProperties jwtProperties;

    /** The validators. */
    private final Map<String, JwtIssuerValidator> validators = new ConcurrentHashMap<>();

    public TenantJwtIssuerValidator(JWTConfigurationProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Validate the JWT.
     *
     * @param token
     *         the token to validate
     * @return the validation result
     */
    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        return validators.computeIfAbsent(toIssuer(token), this::fromIssuer)
                .validate(token);
    }

    /**
     * Get the issuer from a JWT.
     *
     * @param jwt
     *         the JWT
     * @return the tenant
     */
    private String toIssuer(Jwt jwt) {
        return jwt.getIssuer().toString();
    }

    /**
     * Create a JwtIssuerValidator for an issuer.
     *
     * @param issuer
     *         the tenant
     * @return the JwtIssuerValidator
     */
    private JwtIssuerValidator fromIssuer(String issuer) {
        return jwtProperties.getTenants().values().stream()
                .filter(tenant -> tenant.getIssuerUri().equals(issuer))
                .findAny()
                .map(JWTConfigurationProperties.JwtServerProperties::getIssuerUri)
                .map(JwtIssuerValidator::new)
                .orElseThrow(() -> new IllegalArgumentException("unknown tenant: " + issuer));
    }
}
