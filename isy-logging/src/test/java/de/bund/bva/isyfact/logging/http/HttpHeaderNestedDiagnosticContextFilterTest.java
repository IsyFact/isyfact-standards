package de.bund.bva.isyfact.logging.http;

import javax.servlet.http.HttpServletRequest;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpHeaderNestedDiagnosticContextFilterTest {

    @Mock
    private HttpServletRequest servletRequest;

    @Test
    public void beforeRequest() {
        HttpHeaderNestedDiagnosticContextFilter filter = new HttpHeaderNestedDiagnosticContextFilter();
        filter.setCorrelationIdHttpHeaderName("korrelationsid");

        when(servletRequest.getHeader("korrelationsid")).thenReturn("testId1");

        MdcHelper.pushKorrelationsId("testId0");

        filter.beforeRequest(servletRequest, "message");

        assertEquals("testId1", MdcHelper.entferneKorrelationsId());
        assertEquals("testId0", MdcHelper.entferneKorrelationsId());
    }

    @Test
    public void beforeRequestKeineIdImHeader() {
        HttpHeaderNestedDiagnosticContextFilter filter = new HttpHeaderNestedDiagnosticContextFilter();

        MdcHelper.pushKorrelationsId("testId0");

        filter.beforeRequest(servletRequest, "message");

        assertNotNull(MdcHelper.entferneKorrelationsId());
        assertEquals("testId0", MdcHelper.entferneKorrelationsId());
    }

    @Test
    public void afterRequestEntferntAlleKorrelationsIds() {
        HttpHeaderNestedDiagnosticContextFilter filter = new HttpHeaderNestedDiagnosticContextFilter();

        MdcHelper.pushKorrelationsId("testId0");
        MdcHelper.pushKorrelationsId("testId1");

        filter.afterRequest(null, "message");

        assertNull(MdcHelper.liesKorrelationsId());
    }
}
