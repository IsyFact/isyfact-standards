/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
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
