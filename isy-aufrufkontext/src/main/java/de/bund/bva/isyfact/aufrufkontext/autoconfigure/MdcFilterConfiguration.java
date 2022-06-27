package de.bund.bva.isyfact.aufrufkontext.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import de.bund.bva.isyfact.aufrufkontext.http.HttpHeaderNestedDiagnosticContextFilter;

/**
 * Da der MDC-Filter ein Servlet-Filter ist darf er auch nur in Servlet-Webanwendungen erstellt werden.
 * Somit wird der Filter in Tests etc. wo er nicht relevant ist auch nicht erstellt.
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MdcFilterConfiguration {

    /**
     * Automatisches setzen der Korrelations-ID.
     */
    @Bean
    FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> httpHeaderNestedDiagnosticContextFilter() {
        FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> registrationBean =
                new FilterRegistrationBean<>();
        registrationBean.setFilter(new HttpHeaderNestedDiagnosticContextFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

}
