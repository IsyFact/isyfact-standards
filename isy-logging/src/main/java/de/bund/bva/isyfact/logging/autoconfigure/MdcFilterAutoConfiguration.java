package de.bund.bva.isyfact.logging.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import de.bund.bva.isyfact.logging.http.HttpHeaderNestedDiagnosticContextFilter;

/**
 * The MDC filter is a servlet filter and therefore may only be created in servlet web applications.
 * Consequently, where it isn't relevant, e.g. in tests, the filter is not created.
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MdcFilterAutoConfiguration {

    /**
     * Automatically sets the Correlation-ID
     */
    @Bean
    @ConditionalOnMissingBean(name = "httpHeaderNestedDiagnosticContextFilter")
    FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> httpHeaderNestedDiagnosticContextFilter() {
        FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> registrationBean =
                new FilterRegistrationBean<>();
        registrationBean.setFilter(new HttpHeaderNestedDiagnosticContextFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

}
