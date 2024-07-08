package de.bund.bva.isyfact.security.oauth2.util;

import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

/**
 * This class contains utility methods for isy-security. It provides access to the claims of the current OAuth 2.0 token.
 */
public class IsySecurityTokenUtil {


    /**
     * The resource bundle that contains the token configuration.
     */
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("config.isy-security-token");

    /**
     * The JWT claim name that contains the login.
     */
    private static final String LOGIN = getConfigPropertyValueAsString("login");

    /**
     * The JWT claim name that contains the userId.
     */
    private static final String USERID = getConfigPropertyValueAsString("userId");

    /**
     * The JWT claim name that contains the bhknz (Behoerdenkennzeichen).
     */
    private static final String BHKNZ = getConfigPropertyValueAsString("bhknz");
    /**
     * The JWT claim name that contains the display name.
     */
    private static final String DISPLAYNAME = getConfigPropertyValueAsString("displayName");

    /**
     * The prefix for all configuration properties.
     */
    private static final String PREFIX = "isy.security.oauth2.claim.";

    /**
     * Returns the value of the configuration property with the given suffix.
     *
     * @param suffix the suffix of the configuration property
     * @return the String value of the configuration property
     */
    private static String getConfigPropertyValueAsString(String suffix) {
        return BUNDLE.getString(PREFIX + suffix);
    }

    /**
     * Returns the login of the current user or an empty Optional if the login attribute is not set.
     *
     * @return the login of the current user
     */
    public static Optional<String> getLogin() {
        String login = (String) getTokenAttribute(LOGIN);
        return Optional.ofNullable(login);
    }

    /**
     * Returns the userId of the current user. If the userId is not set, the Subject identifier (sub) is returned.
     *
     * @return the userId of the current user or the Subject identifier (sub) if the userId is not set
     */
    public static String getUserId() {
        String userId = (String) getTokenAttribute(USERID);
        if (userId == null) {
            return (String) getTokenAttribute(StandardClaimNames.SUB);
        } else {
            return userId;
        }
    }

    /**
     * Returns a bhknz of the current user or an empty Optional if the bhknz attribute is not set.
     *
     * @return the bhknz of the current user
     */
    public static Optional<String> getBhknz() {
        String bhknz = (String) getTokenAttribute(BHKNZ);
        return Optional.ofNullable(bhknz);
    }

    /**
     * Returns the display name of the current user. If the display name is not set, the login is returned.
     *
     * @return the display name of the current user or the login if the display name is not set
     */
    public static Optional<String> getDisplayName() {
        String displayName = (String) getTokenAttribute(DISPLAYNAME);
        if (displayName == null) {
            return getLogin();
        }
        return Optional.of(displayName);
    }

    /**
     * Retrieves an attribute of the access token if the currently authenticated principal is an OAuth 2.0 token.
     *
     * @param key
     *         the key to retrieve the given attribute
     * @return the attribute in the access token for the given key, or {@code null}
     * @throws OAuth2AuthenticationException
     *         if the authenticated principal is not a {@link AbstractOAuth2TokenAuthenticationToken}
     */
    public static Object getTokenAttribute(String key) {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuthentication instanceof AbstractOAuth2TokenAuthenticationToken) {
            return ((AbstractOAuth2TokenAuthenticationToken<?>) currentAuthentication).getTokenAttributes().get(key);
        } else {
            throw new OAuth2AuthenticationException(BearerTokenErrors.invalidToken("Authentication is not an OAuth2 token authentication."));
        }
    }

}
