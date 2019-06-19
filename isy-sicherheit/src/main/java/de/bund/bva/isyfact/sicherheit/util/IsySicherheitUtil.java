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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
/**
 * Diese Klasse enthält Utility-Methoden für die isy-sicherheit.
 */
public final class IsySicherheitUtil {
    /**
     * Header-Name für Authorisierung.
     */
    private static final String HEADER_NAME_AUTH = "Authorization";

    /**
     * Prefix-Name für HTTP-Basic-Authentifizierung .
     */
    private static final String BASIC_AUTH_PREFIX = "Basic";

    /**
     * Trennzeichen zwischen Benutzerkennung und Kennwort bei der HTTP-BASIC-Authentifizierung.
     */
    private static final String BASIC_AUTH_DELIMITER = ":";

    /**
     * Liest den HTTP-Header für die HTTP-Basic-Basic-Authentifizierung aus und ermittelt die Benutzerkennung
     * und das Kennwort.
     *
     * @param request
     *            der HTTP Request
     * @return String array der Länge zwei mit dem Benutzernamen und Passwort, enthält null Werte, wenn der
     *         Header nicht gesetzt ist.
     */
    public static String[] parseBasicAuthHeader(HttpServletRequest request) {
        return parseBasisAuthHeaderValue(request.getHeader(HEADER_NAME_AUTH));
    }

    /**
     * Ermittelt die Benutzerkennung und das Kennwort aus dem Wert des HTTP-Headers-Attributs für die
     * HTTP-Basic-Authentifizierung aus.
     *
     * @param headerValue
     *            Wert des Header-Attributs.
     * @return String array der Länge zwei mit dem Benutzernamen und Passwort, enthält null Werte, wenn der
     *         Wert des Header-Attributs leer ist.
     */
    public static String[] parseBasisAuthHeaderValue(String headerValue) {
        String[] result = new String[2];
        if (headerValue != null && headerValue.trim().length() > 5) {
            // Suche "Basic" Präfix
            String prefix = headerValue.trim().substring(0, 5);
            if (prefix.equalsIgnoreCase(BASIC_AUTH_PREFIX)) {
                // "Basic" Präfix gefunden. Der restliche String enthält username:password base64 kodiert
                headerValue = headerValue.trim();
                headerValue = headerValue.substring(5).trim();
                headerValue = new String(Base64.getDecoder().decode(headerValue), StandardCharsets.ISO_8859_1);
                if (headerValue.contains(BASIC_AUTH_DELIMITER)) {
                    // Bis zum Delimiter ist alles Benutzername
                    result[0] = headerValue.substring(0, headerValue.indexOf(BASIC_AUTH_DELIMITER));
                    // Alles nach Delimiter ist Passwort
                    result[1] = headerValue.substring(headerValue.indexOf(BASIC_AUTH_DELIMITER) + 1);
                } else {
                    result[0] = headerValue;
                }
            }
        }
        return result;
    }

    /**
     * Erzeugt einen HTTP-Basic-Authentication-Header für eine Login-ID und ein Kennwort.
     *
     * @param loginId
     *            Die Login-ID.
     * @param password
     *            Das Kennwort.
     * @return Stirng[] mit dem Namen des HeaderAttributs ([0]) und dem Header-Wert ([1]).
     */
    public static String[] createBasicAuthHeader(String loginId, String password) {
        String[] result = new String[2];
        result[0] = HEADER_NAME_AUTH;

        String tmpLoginId = loginId;
        if (tmpLoginId == null) {
            tmpLoginId = "";
        }

        String tmpPassword = password;
        if (tmpPassword == null) {
            tmpPassword = "";
        }

        String loginPassword = tmpLoginId + BASIC_AUTH_DELIMITER + tmpPassword;
        String loginPasswordEncoded = Base64.getEncoder().encodeToString(loginPassword.getBytes(StandardCharsets.ISO_8859_1));

        result[1] = BASIC_AUTH_PREFIX + " " + loginPasswordEncoded;

        return result;
    }

}
