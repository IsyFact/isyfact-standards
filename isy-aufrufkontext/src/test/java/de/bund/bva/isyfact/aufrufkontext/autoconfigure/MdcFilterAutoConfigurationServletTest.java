package de.bund.bva.isyfact.aufrufkontext.autoconfigure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.aufrufkontext.http.HttpHeaderNestedDiagnosticContextFilter;
import de.bund.bva.isyfact.aufrufkontext.test.config.LeereTestConfig;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeereTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "isy.logging.anwendung.name=test",
                "isy.logging.anwendung.typ=test",
                "isy.logging.anwendung.version=test" })
public class MdcFilterAutoConfigurationServletTest {

    @Autowired
    private FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> httpHeaderNestedDiagnosticContextFilter;

    @Test
    public void testFilterWurdeRichtigErstellt() {
        FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter> mdcFilter =
                this.httpHeaderNestedDiagnosticContextFilter;

        assertEquals(Ordered.HIGHEST_PRECEDENCE, mdcFilter.getOrder());
        assertEquals(1, mdcFilter.getUrlPatterns().size());
        assertTrue(mdcFilter.getUrlPatterns().contains("/*"));
    }

}
