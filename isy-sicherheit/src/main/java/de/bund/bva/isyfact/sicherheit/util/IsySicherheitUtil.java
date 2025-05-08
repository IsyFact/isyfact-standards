package de.bund.bva.isyfact.sicherheit.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
/**
 * This class contains utility methods for isy-sicherheit.
 * @deprecated in favor of {@code spring-web} and {@code spring-security-web}
 */
@Deprecated
public final class IsySicherheitUtil {
    /**
     * Header name for authorization.
     */
    private static final String HEADER_NAME_AUTH = "Authorization";

    /**
     * Prefix name for http basic authorization.
     */
    private static final String BASIC_AUTH_PREFIX = "Basic";

    /**
     * Separator between user identification and password for the http basic authentication.
     */
    private static final String BASIC_AUTH_DELIMITER = ":";

    /**
     * Reads the http header for http basic authentication and determines user identification and password.
     *
     * @param request
     *            the HTTP request
     * @return String array with length two, containing username and password.
     *            Contains zero elements if the header is not set.
     * @deprecated use {@link org.springframework.security.web.authentication.www.BasicAuthenticationConverter} instead
     */
    @Deprecated
    public static String[] parseBasicAuthHeader(HttpServletRequest request) {
        return parseBasisAuthHeaderValue(request.getHeader(HEADER_NAME_AUTH));
    }

    /**
     * Determines the user identification and password from the value of the http header attribute
     * for the http basic authentication.
     *
     * @param headerValue
     *              Value of the header attribute
     * @return String array with length two, containing username and password.
     *              Contains zero elements if the value of the header attribute is empty.
     *
     * @deprecated use {@link org.springframework.security.web.authentication.www.BasicAuthenticationConverter} instead
     */
    @Deprecated
    public static String[] parseBasisAuthHeaderValue(String headerValue) {
        String[] result = new String[2];
        if (headerValue != null && headerValue.trim().length() > 5) {
            // Search "basic" prefix
            String prefix = headerValue.trim().substring(0, 5);
            if (prefix.equalsIgnoreCase(BASIC_AUTH_PREFIX)) {
                // Found basic prefix. The remaining string contains username:password base64 encoded
                headerValue = headerValue.trim();
                headerValue = headerValue.substring(5).trim();
                headerValue = new String(Base64.getDecoder().decode(headerValue), StandardCharsets.ISO_8859_1);
                if (headerValue.contains(BASIC_AUTH_DELIMITER)) {
                    // Everything up until the delimiter constitutes the username
                    result[0] = headerValue.substring(0, headerValue.indexOf(BASIC_AUTH_DELIMITER));
                    // Everything after delimiter constitutes the password
                    result[1] = headerValue.substring(headerValue.indexOf(BASIC_AUTH_DELIMITER) + 1);
                } else {
                    result[0] = headerValue;
                }
            }
        }
        return result;
    }

    /**
     * Creates a http basic authentication header for a login id and password.
     *
     * @param loginId
     *            The login id
     * @param password
     *            The password
     * @return String array with length two, containing header attribute ([0]) and header value ([1]).
     *
     * @deprecated use {@link org.springframework.http.HttpHeaders} instead
     */
    @Deprecated
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
