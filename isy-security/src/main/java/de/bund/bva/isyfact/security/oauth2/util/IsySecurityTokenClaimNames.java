package de.bund.bva.isyfact.security.oauth2.util;

import java.util.ResourceBundle;

/**
 * Contains aliases for the claims commonly expected in IsyFact applications that might not necessarily be
 * standard claim names of an OAuth 2.0 / OpenID Connect token.
 * If possible consider accessing these only with the matching methods in {@link IsySecurityTokenUtil}.
 * <p>
 * All the aliased values can be changed in case your environment uses different claim names by
 * creating a file {@code config/isy-security-token.properties} and assigning the claims as follows:
 * <pre>
 * isy.security.oauth2.claim.login = preferred_username
 * isy.security.oauth2.claim.userId = internekennung
 * isy.security.oauth2.claim.bhknz = bhknz
 * isy.security.oauth2.claim.displayName = name
 * </pre>
 *
 * @see IsySecurityTokenUtil
 */
public final class IsySecurityTokenClaimNames {

    /** The resource bundle that contains the token configuration. */
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("config.isy-security-token");

    /** The prefix for all configuration properties. */
    private static final String PREFIX = "isy.security.oauth2.claim.";

    /** The JWT claim name that contains the login. */
    public static final String LOGIN = getConfigPropertyValueAsString("login");

    /** The JWT claim name that contains the user id. */
    public static final String USER_ID = getConfigPropertyValueAsString("userId");

    /** The JWT claim name that contains the BHKNZ (Behoerdenkennzeichen). */
    public static final String BHKNZ = getConfigPropertyValueAsString("bhknz");

    /** The JWT claim name that contains the display name. */
    public static final String DISPLAY_NAME = getConfigPropertyValueAsString("displayName");

    private IsySecurityTokenClaimNames() {
    }

    /**
     * Returns the value of the configuration property with the given suffix.
     *
     * @param suffix
     *         the suffix of the configuration property
     * @return the String value of the configuration property
     */
    private static String getConfigPropertyValueAsString(String suffix) {
        return BUNDLE.getString(PREFIX + suffix);
    }
}
