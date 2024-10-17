package de.bund.bva.isyfact.security.oauth2.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

/**
 * This class contains utility methods for working with tokens in isy-security.
 * It provides easy access to the claims defined in {@link IsySecurityTokenClaimNames}
 * and all claims of the current OAuth 2.0 token.
 */
public final class IsySecurityTokenUtil {

    /**
     * Utility class should not be instantiated.
     */
    private IsySecurityTokenUtil() {
    }

    /**
     * Returns the login of the current user or an empty Optional if the login attribute is not set.
     *
     * @return the login of the current user
     */
    public static Optional<String> getLogin() {
        String login = (String) getTokenAttribute(IsySecurityTokenClaimNames.LOGIN);
        return Optional.ofNullable(login);
    }

    /**
     * Returns the userId of the current user. If the userId is not set, the Subject identifier (sub) is returned.
     *
     * @return the userId of the current user or the Subject identifier (sub) if the userId is not set
     */
    public static String getUserId() {
        String userId = (String) getTokenAttribute(IsySecurityTokenClaimNames.USER_ID);
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
        String bhknz = (String) getTokenAttribute(IsySecurityTokenClaimNames.BHKNZ);
        return Optional.ofNullable(bhknz);
    }

    /**
     * Returns the display name of the current user. If the display name is not set, the login is returned.
     *
     * @return the display name of the current user or the login if the display name is not set
     */
    public static Optional<String> getDisplayName() {
        String displayName = (String) getTokenAttribute(IsySecurityTokenClaimNames.DISPLAY_NAME);
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
        return getAuthenticationToken().getTokenAttributes().get(key);
    }

    /**
     * Checks if the {@link OAuth2Token} from the current SecurityContext has expired
     * with a time offset of {@code expirationTimeOffset}.
     *
     * @param expirationTimeOffset
     *         the time frame before expiry during which the token is considered as already expired
     * @return {@code true} if the token has expired or the expiresAt property of the token is not set,
     * else {@code false}
     * @throws OAuth2AuthenticationException
     *         if the authenticated principal is not a {@link AbstractOAuth2TokenAuthenticationToken}
     */
    public static boolean hasTokenExpired(Duration expirationTimeOffset) {
        return Optional.ofNullable(getAuthenticationToken().getToken().getExpiresAt())
                .map(expiresAt -> Instant.now().isAfter(expiresAt.minus(expirationTimeOffset)))
                .orElse(true);
    }

    /**
     * Retrieves the oauth2 authentication token from the SecurityContext if the currently authenticated principal
     * is an OAuth 2.0 token.
     *
     * @return the {@link AbstractOAuth2TokenAuthenticationToken} from the SecurityContext
     * @throws OAuth2AuthenticationException
     *         if the authenticated principal is not a {@link AbstractOAuth2TokenAuthenticationToken}
     */
    public static AbstractOAuth2TokenAuthenticationToken<?> getAuthenticationToken() {
        if (hasOAuth2Token()) {
            return ((AbstractOAuth2TokenAuthenticationToken<?>) SecurityContextHolder.getContext().getAuthentication());
        } else {
            throw new OAuth2AuthenticationException(BearerTokenErrors.invalidToken("Authentication is not an OAuth2 token authentication."));
        }
    }

    /**
     * Checks if the {@link SecurityContextHolder} holds an {@link AbstractOAuth2TokenAuthenticationToken} object.
     *
     * @return {@code true} if the current authentication in the SecurityContext is an instance of
     * {@link AbstractOAuth2TokenAuthenticationToken}, else {@code false}
     */
    public static boolean hasOAuth2Token() {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        return currentAuthentication instanceof AbstractOAuth2TokenAuthenticationToken;
    }
}
