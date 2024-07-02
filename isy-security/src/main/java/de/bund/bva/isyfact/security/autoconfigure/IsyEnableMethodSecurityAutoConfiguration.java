package de.bund.bva.isyfact.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * isy-security AutoConfiguration to enable Method Security for usage with @Secured and @PreAuthorize Annotations.
 *
 * @deprecated since IsyFact 3.1.0, to be removed in IsyFact 4.0.0. Applications are expected to set the annotation if required.
 */
@AutoConfiguration
@EnableMethodSecurity(securedEnabled = true)
@Deprecated
public class IsyEnableMethodSecurityAutoConfiguration {
}
