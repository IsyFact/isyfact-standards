package de.bund.bva.isyfact.serviceapi.common;


import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;

/**
 * Class containing helper methods for accessing {@link HttpServletRequest}.
 */
public final class RequestUtil {

    /** Utility class. */
    private RequestUtil() {
    }

    /**
     * Extracts the OAuth 2 bearer token from the request.
     *
     * @param request
     *         the request to extract the token from
     * @return the Base64 encoded token, or {@code null} if no token was found
     */
    public static String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        return extractBearerToken(authHeader);
    }

    /**
     * Extracts the OAuth 2 bearer token from provided header value.
     *
     * @param authHeader
     *         the value of the HTTP Authorization header
     * @return the Base64 encoded token, or {@code null} if no token was found
     */
    public static String extractBearerToken(String authHeader) {
        String tokenString = null;
        if (authHeader != null) {
            String[] split = authHeader.trim().split("\\s+");
            if (split.length == 2 && split[0].equalsIgnoreCase("Bearer")) {
                tokenString = split[1];
            }
        }

        return tokenString;
    }

}
