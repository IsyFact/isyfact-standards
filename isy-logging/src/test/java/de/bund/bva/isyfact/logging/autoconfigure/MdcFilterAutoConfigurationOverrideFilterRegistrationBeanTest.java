package de.bund.bva.isyfact.logging.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import de.bund.bva.isyfact.logging.http.HttpHeaderNestedDiagnosticContextFilter;
import de.bund.bva.isyfact.logging.test.config.LeereTestConfig;

@SpringBootTest(
        classes = {
                LeereTestConfig.class,
                MdcFilterAutoConfigurationOverrideFilterRegistrationBeanTest.OverrideTestConfig.class
        },
        properties = {
                "spring.main.banner-mode = off",
                "isy.logging.anwendung.name = test",
                "isy.logging.anwendung.typ = test",
                "isy.logging.anwendung.version = test"
        })
public class MdcFilterAutoConfigurationOverrideFilterRegistrationBeanTest {

    @Autowired
    private FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> mdcFilter;

    @Test
    public void testThatBeanCanBeOverridden() {
        assertThat(mdcFilter.getUrlPatterns()).containsExactly("/test-override/*");
    }

    @TestConfiguration
    static class OverrideTestConfig {
        @Bean
        FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> httpHeaderNestedDiagnosticContextFilter() {
            HttpHeaderNestedDiagnosticContextFilter overriddenFilter = new HttpHeaderNestedDiagnosticContextFilter();

            FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> registrationBean =
                    new FilterRegistrationBean<>();
            registrationBean.setFilter(overriddenFilter);
            registrationBean.addUrlPatterns("/test-override/*");
            return registrationBean;
        }
    }

}
