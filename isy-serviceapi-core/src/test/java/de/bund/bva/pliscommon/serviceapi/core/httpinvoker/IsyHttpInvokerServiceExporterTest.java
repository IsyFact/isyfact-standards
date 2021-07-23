package de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.aufrufkontext.stub.AufrufKontextVerwalterStub;

/**
 * Plain unit tests for {@link IsyHttpInvokerServiceExporter}.
 */
public class IsyHttpInvokerServiceExporterTest {

    /** Test value for any tokens (Bearer, Basic). */
    private static final String TEST_TOKEN = "testToken12345";

    /** AufrufKontextVerwalter to read the set bearer token. */
    private AufrufKontextVerwalter<?> aufrufKontextVerwalterStub;

    /** The ServiceExporter that handles the requests. */
    private IsyHttpInvokerServiceExporter serviceExporter;

    /** Mocked request to set headers in. */
    private HttpServletRequest requestMock;

    /** Mocked response. */
    private HttpServletResponse responseMock;

    @Before
    public void setUp() {
        aufrufKontextVerwalterStub = new AufrufKontextVerwalterStub<>();
        aufrufKontextVerwalterStub.setBearerToken(null);
        serviceExporter = new IsyHttpInvokerServiceExporter(aufrufKontextVerwalterStub);
        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);
    }

    @Test
    public void testBearerTokenExtracted() {
        mockRequestAuthorizationHeader("Bearer " + TEST_TOKEN);

        try {
            serviceExporter.handleRequest(requestMock, responseMock);
            fail("Exception expected since the invoker can't connect to a remote.");
        } catch (Exception e) {
            assertEquals(TEST_TOKEN, aufrufKontextVerwalterStub.getBearerToken());
        }
    }

    @Test
    public void testBearerTokenExtractedWithDifferentCapitalization() {
        mockRequestAuthorizationHeader("bEAreR " + TEST_TOKEN);

        try {
            serviceExporter.handleRequest(requestMock, responseMock);
            fail("Exception expected since the invoker can't connect to a remote.");
        } catch (Exception e) {
            assertEquals(TEST_TOKEN, aufrufKontextVerwalterStub.getBearerToken());
        }
    }

    @Test
    public void testBearerTokenNotExtractedWithInvalidScheme() {
        mockRequestAuthorizationHeader("Basic " + TEST_TOKEN);

        try {
            serviceExporter.handleRequest(requestMock, responseMock);
            fail("Exception expected since the invoker can't connect to a remote.");
        } catch (Exception e) {
            assertNull(aufrufKontextVerwalterStub.getBearerToken());
        }
    }

    @Test
    public void testBearerTokenNotExtractedWhenHeaderNotSet() {
        mockRequestAuthorizationHeader(null);

        try {
            serviceExporter.handleRequest(requestMock, responseMock);
            fail("Exception expected since the invoker can't connect to a remote.");
        } catch (Exception e) {
            assertNull(aufrufKontextVerwalterStub.getBearerToken());
        }
    }

    private void mockRequestAuthorizationHeader(String headerValue) {
        when(requestMock.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(headerValue);
    }

}
