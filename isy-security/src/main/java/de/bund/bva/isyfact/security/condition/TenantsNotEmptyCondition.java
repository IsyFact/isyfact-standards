package de.bund.bva.isyfact.security.condition;

import java.util.regex.Pattern;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

public class TenantsNotEmptyCondition implements Condition {

    /**
     * Pattern to match tenant issuer URI properties.
     */
    private static final Pattern TENANT_ISSUER_URI_PATTERN = Pattern.compile("isy\\.security\\.oauth2\\.resourceserver\\.jwt\\.tenants\\..+\\.issuer-uri");

    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        ConfigurableEnvironment env = (ConfigurableEnvironment) context.getEnvironment();
        for (PropertySource<?> propertySource : env.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                if (isTenantIssuerUriPresent((EnumerablePropertySource<?>) propertySource)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the tenant issuer URI is present in the given property source.
     *
     * @param propertySource the property source to check
     * @return true if the tenant issuer URI is present, false otherwise
     */
    private boolean isTenantIssuerUriPresent(EnumerablePropertySource<?> propertySource) {
        for (String propertyName : propertySource.getPropertyNames()) {
            if (TENANT_ISSUER_URI_PATTERN.matcher(propertyName).matches()) {
                return true;
            }
        }
        return false;
    }
}
