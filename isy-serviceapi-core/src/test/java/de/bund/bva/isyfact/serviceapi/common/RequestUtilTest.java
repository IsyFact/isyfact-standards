package de.bund.bva.isyfact.serviceapi.common;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

/**
 * Tests for various extractions of the bearer token via {@link RequestUtil}.
 */
public class RequestUtilTest {

    /** Test value for any tokens (Bearer, Basic). */
    private static final String TEST_TOKEN = "testToken12345";

    /** Mocked request to set headers in. */
    private HttpServletRequest requestMock;

    @Before
    public void setUp() {
        requestMock = mock(HttpServletRequest.class);
    }

    @Test
    public void testBearerTokenExtracted() {
        mockRequestAuthorizationHeader("Bearer " + TEST_TOKEN);

        String extractedBearerToken = RequestUtil.extractBearerToken(requestMock);
        assertEquals(TEST_TOKEN, extractedBearerToken);
    }

    @Test
    public void testBearerTokenExtractedRemovingWhitespace() {
        mockRequestAuthorizationHeader("    Bearer    " + TEST_TOKEN + "     ");

        String extractedBearerToken = RequestUtil.extractBearerToken(requestMock);
        assertEquals(TEST_TOKEN, extractedBearerToken);
    }

    @Test
    public void testBearerTokenExtractedWithDifferentCapitalization() {
        mockRequestAuthorizationHeader("bEAreR " + TEST_TOKEN);

        String extractedBearerToken = RequestUtil.extractBearerToken(requestMock);
        assertEquals(TEST_TOKEN, extractedBearerToken);
    }

    @Test
    public void testBearerTokenNotExtractedWithInvalidScheme() {
        mockRequestAuthorizationHeader("Basic " + TEST_TOKEN);

        String extractedBearerToken = RequestUtil.extractBearerToken(requestMock);
        assertNull(extractedBearerToken);
    }

    @Test
    public void testBearerTokenNotExtractedWithoutScheme() {
        mockRequestAuthorizationHeader(TEST_TOKEN);

        String extractedBearerToken = RequestUtil.extractBearerToken(requestMock);
        assertNull(extractedBearerToken);
    }

    @Test
    public void testBearerTokenNotExtractedWhenHeaderNotSet() {
        mockRequestAuthorizationHeader(null);

        String extractedBearerToken = RequestUtil.extractBearerToken(requestMock);
        assertNull(extractedBearerToken);
    }

    private void mockRequestAuthorizationHeader(String headerValue) {
        when(requestMock.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(headerValue);
    }

}
