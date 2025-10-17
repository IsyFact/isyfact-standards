package de.bund.bva.isyfact.logging.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.STRING;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import de.bund.bva.isyfact.logging.http.HttpHeaderNestedDiagnosticContextFilter;
import de.bund.bva.isyfact.logging.test.config.LeereTestConfig;
import de.bund.bva.isyfact.logging.util.MdcHelper;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                LeereTestConfig.class,
                MdcFilterAutoConfigurationTest.TestConfig.class
        },
        properties = {
                "spring.main.banner-mode = off",
                "isy.logging.anwendung.name = test",
                "isy.logging.anwendung.typ = test",
                "isy.logging.anwendung.version = test"
        })
@Import(MdcFilterAutoConfiguration.class)
public class MdcFilterAutoConfigurationTest {

    @Autowired
    private FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> mdcFilter;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testFilterWurdeRichtigErstellt() {
        assertThat(mdcFilter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
        assertThat(mdcFilter.getUrlPatterns()).containsExactly("/*");
    }

    @Test
    public void mdcFilterAppliesFirst() {
        final ObjectAssert<ResponseEntity<String>> responseAssert = assertThat(testRestTemplate.getForEntity("/", String.class));

        responseAssert.extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
        responseAssert.extracting(ResponseEntity::getBody, STRING).isNotBlank();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        FilterRegistrationBean<OncePerRequestFilter> testFilter() {
            final FilterRegistrationBean<OncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
            registrationBean.addUrlPatterns("/*");
            registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
            registrationBean.setFilter(new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
                    final String korrelationsId = MdcHelper.liesKorrelationsId();
                    if (korrelationsId == null) {
                        response.setStatus(500);
                    } else {
                        response.setStatus(200);
                        response.getWriter().println(korrelationsId);
                    }
                    response.flushBuffer();
                }
            });
            return registrationBean;
        }

        @Bean
        ServletRegistrationBean<HttpServlet> testServlet() {
            ServletRegistrationBean<HttpServlet> servletRegistrationBean = new ServletRegistrationBean<>();
            servletRegistrationBean.setUrlMappings(Collections.singleton("/*"));
            servletRegistrationBean.setServlet(new HttpServlet() {
                @Override
                protected void doGet(HttpServletRequest request, HttpServletResponse response) {
                    response.setStatus(200);
                }
            });
            return servletRegistrationBean;
        }

    }
}
