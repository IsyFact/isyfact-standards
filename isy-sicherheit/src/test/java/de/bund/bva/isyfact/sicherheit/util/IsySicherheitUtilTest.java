package de.bund.bva.isyfact.sicherheit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Tests for class IsySicherheitUtil.
 */
public class IsySicherheitUtilTest {

    @Test
    public void basicAuthUtilTest() {
        // Base 64 encoded "LoginId:Password"
        String base64EncodedSample1 = "Basic TG9naW5JZDpQYXNzd29yZA==";
        String[] loginCredentials = IsySicherheitUtil.parseBasisAuthHeaderValue(base64EncodedSample1);
        assertEquals("LoginId", loginCredentials[0]);
        assertEquals("Password", loginCredentials[1]);

        // Base 64 encoded "LoginId-ÄÖÜ:Password-äöü"
        String base64EncodedSample2 = "Basic TG9naW5JZC3E1tw6UGFzc3dvcmQt5Pb8";
        loginCredentials = IsySicherheitUtil.parseBasisAuthHeaderValue(base64EncodedSample2);
        assertEquals("LoginId-ÄÖÜ", loginCredentials[0]);
        assertEquals("Password-äöü", loginCredentials[1]);

        // "Basic" must is not case sensitive.
        loginCredentials = IsySicherheitUtil.parseBasisAuthHeaderValue("BASIC TG9naW5JZDpQYXNzd29yZA==");
        assertEquals("LoginId", loginCredentials[0]);
        assertEquals("Password", loginCredentials[1]);

        // Header value must start with "Basic" (case insensitive)
        loginCredentials =
            IsySicherheitUtil.parseBasisAuthHeaderValue("NOBASIC TG9naW5JZC3E1tw6UGFzc3dvcmQt5Pb8");
        assertNull(loginCredentials[0]);
        assertNull(loginCredentials[1]);

        // Insert of null results in no login credentials
        loginCredentials = IsySicherheitUtil.parseBasisAuthHeaderValue(null);
        assertNull(loginCredentials[0]);
        assertNull(loginCredentials[1]);

        // Create Basic authentication header with normal characters
        String[] authHeader = IsySicherheitUtil.createBasicAuthHeader("LoginId", "Password");
        assertEquals("Authorization", authHeader[0]);
        assertEquals(base64EncodedSample1, authHeader[1]);

        // Create Basic authentication header with special characters
        authHeader = IsySicherheitUtil.createBasicAuthHeader("LoginId-ÄÖÜ", "Password-äöü");
        assertEquals("Authorization", authHeader[0]);
        assertEquals(base64EncodedSample2, authHeader[1]);

        // Get the credentials from created header gives the input parameters.
        loginCredentials =
            IsySicherheitUtil.parseBasisAuthHeaderValue(IsySicherheitUtil.createBasicAuthHeader(
                "LoginId-ÄÖÜ", "Password-äöü")[1]);
        assertEquals("LoginId-ÄÖÜ", loginCredentials[0]);
        assertEquals("Password-äöü", loginCredentials[1]);
    }

}
