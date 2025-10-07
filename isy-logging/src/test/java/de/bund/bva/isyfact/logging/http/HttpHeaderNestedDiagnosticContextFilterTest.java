package de.bund.bva.isyfact.logging.http;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.bund.bva.isyfact.logging.util.MdcHelper;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
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
