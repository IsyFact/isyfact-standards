package de.bund.bva.isyfact.security.core;

import java.net.URL;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;

import de.bund.bva.isyfact.security.config.JWTConfigurationProperties;

public class TenantJwsKeySelector implements JWTClaimsSetAwareJWSKeySelector<SecurityContext> {

    /**
     * The key selectors.
     */
    private final Map<String, JWSKeySelector<SecurityContext>> selectors = new ConcurrentHashMap<>();

    /**
     * The JWT properties.
     */
    private final JWTConfigurationProperties jwtProperties;

    public TenantJwsKeySelector(JWTConfigurationProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Select the JWS keys for a JWT.
     *
     * @param jwsHeader       the JWS header
     * @param jwtClaimsSet    the JWT claims set
     * @param securityContext the security context
     * @return the JWS keys
     * @throws KeySourceException if the keys cannot be selected
     */
    @Override
    public List<? extends Key> selectKeys(JWSHeader jwsHeader, JWTClaimsSet jwtClaimsSet, SecurityContext securityContext)
            throws KeySourceException {
        return this.selectors.computeIfAbsent(toIssuer(jwtClaimsSet), this::fromIssuer)
                .selectJWSKeys(jwsHeader, securityContext);
    }

    private String toIssuer(JWTClaimsSet claimSet) {
        return claimSet.getIssuer();
    }

    /**
     * Create a JWSKeySelector for a tenant.
     *
     * @param issuer the tenant
     * @return the JWSKeySelector
     */
    private JWSKeySelector<SecurityContext> fromIssuer(String issuer) {
        return jwtProperties.getTenants().values().stream()
                .filter(tenant -> tenant.getIssuerUri().equals(issuer))
                .findAny()
                .map(tenant -> fromUri(tenant.getJwkSetUri()))
                .orElseThrow(() -> new IllegalArgumentException("unknown tenant: " + issuer));
    }

    /**
     * Create a JWSKeySelector from a JWKSet URI.
     *
     * @param uri the JWKSet URI
     * @return the JWSKeySelector
     */
    private JWSKeySelector<SecurityContext> fromUri(String uri) {
        try {
            return JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(new URL(uri));
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
