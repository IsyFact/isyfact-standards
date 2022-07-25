package de.bund.bva.isyfact.aufrufkontext.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.filter.OncePerRequestFilter;

import de.bund.bva.isyfact.aufrufkontext.http.HttpHeaderNestedDiagnosticContextFilter;
import de.bund.bva.isyfact.aufrufkontext.test.config.LeereTestConfig;
import de.bund.bva.isyfact.logging.util.MdcHelper;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {LeereTestConfig.class, MdcFilterAutoConfigurationServletTest.CustomFilterTestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "isy.logging.anwendung.name=test",
                "isy.logging.anwendung.typ=test",
                "isy.logging.anwendung.version=test" })
public class MdcFilterAutoConfigurationServletTest {

    @Autowired
    private FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> httpHeaderNestedDiagnosticContextFilter;

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    public void testFilterWurdeRichtigErstellt() {
        FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> mdcFilter =
                this.httpHeaderNestedDiagnosticContextFilter;

        assertThat(mdcFilter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
        assertThat(mdcFilter.getUrlPatterns()).containsExactly("/*");
    }

    @Test
    public void requestReachesCustomFiltersAndServlet() {
        ResponseEntity<HttpServletResponse> response = testRestTemplate.exchange(
                "/",
                HttpMethod.GET,
                TestData.createHttpTestEntity(),
                HttpServletResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get(TestData.FILTER_HEADER))
                .containsExactly(TestData.FILTER_RESPONSE);
        assertThat(response.getHeaders().get(TestData.SERVLET_HEADER))
                .containsExactly(TestData.SERVLET_RESPONSE);
    }

    @Test
    public void mdcFilterAppliesFirst() {
        ResponseEntity<HttpServletResponse> response = testRestTemplate.exchange(
                "/",
                HttpMethod.GET,
                TestData.createHttpTestEntity(),
                HttpServletResponse.class);

        assertThat(response.getHeaders().get(TestData.CHECK_MDC_HEADER))
                .containsExactly(TestData.CHECK_MDC_SUCCESS);
    }


    public static class TestData {
        public static HttpEntity<String> createHttpTestEntity(){
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setAccept(Collections.singletonList(MediaType.ALL));
            headers.set(TestData.FILTER_HEADER, "");
            headers.set(TestData.SERVLET_HEADER, "");
            headers.set(TestData.CHECK_MDC_HEADER, "");
            return new HttpEntity<>(headers);
        }

        public static final String SERVLET_HEADER = "ServerResponseHeader";
        public static final String SERVLET_RESPONSE = "ResponseFromServer";
        public static final String FILTER_HEADER = "FilterResponseHeader";
        public static final String FILTER_RESPONSE = "ResponseFromFilter";
        public static final String CHECK_MDC_HEADER = "CheckingMdcKorrelationsId";
        public static final String CHECK_MDC_SUCCESS = "CheckMdcSuccess";
        public static final String CHECK_MDC_FAIL = "CheckMdcFail";
    }

    @TestConfiguration
    static class CustomFilterTestConfig {
        @Bean
        public FilterRegistrationBean<HasKorrelationsIdTestFilter> registerFilterBean() {
            FilterRegistrationBean<HasKorrelationsIdTestFilter> registrationBean = new FilterRegistrationBean<>();
            registrationBean.setFilter(new HasKorrelationsIdTestFilter());
            registrationBean.addUrlPatterns("/*");
            registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
            return registrationBean;
        }

        @Bean
        ServletRegistrationBean<TestServlet>  servletRegistrationBean(){
            ServletRegistrationBean<TestServlet> servletRegistrationBean = new ServletRegistrationBean<> ();
            servletRegistrationBean.setServlet(new TestServlet());
            servletRegistrationBean.setUrlMappings(Collections.singleton("/*"));
            return servletRegistrationBean;
        }

       static private class TestServlet extends HttpServlet {
           @Override
           protected void doGet(HttpServletRequest request, HttpServletResponse response) {
               response.setStatus(200);
               response.addHeader(TestData.SERVLET_HEADER, TestData.SERVLET_RESPONSE);
           }
       }

       static private class HasKorrelationsIdTestFilter extends OncePerRequestFilter {
           @Override
           protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
               String KorrelationsID = MdcHelper.liesKorrelationsId();
               boolean KorrelationsIdMissing = (KorrelationsID == null || KorrelationsID.isEmpty());
               response.setStatus(200);
               response.addHeader(TestData.FILTER_HEADER, TestData.FILTER_RESPONSE);
               response.addHeader(
                       TestData.CHECK_MDC_HEADER,
                       KorrelationsIdMissing ? TestData.CHECK_MDC_FAIL : TestData.CHECK_MDC_SUCCESS
               );
               filterChain.doFilter(request, response);
           }
       }
    }
}
