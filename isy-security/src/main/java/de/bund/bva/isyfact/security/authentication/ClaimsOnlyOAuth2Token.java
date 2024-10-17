package de.bund.bva.isyfact.security.authentication;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

import de.bund.bva.isyfact.security.oauth2.util.IsySecurityTokenClaimNames;
import de.bund.bva.isyfact.security.oauth2.util.IsySecurityTokenUtil;

/**
 * An implementation of an {@link AbstractOAuth2Token} that contains only the claims of a JSON Web Token (JWT)
 * and no actual token value that could be used as for example a bearer token.
 * <p>
 * This token is only intended for internal use within an application where it is assured that the application
 * will either not communicate with external systems or perform a proper authentication which overwrites this
 * token before doing so.
 */
public class ClaimsOnlyOAuth2Token extends AbstractOAuth2Token implements ClaimAccessor {

    /** Map of the token claims. */
    private final Map<String, Object> claims;

    public ClaimsOnlyOAuth2Token(Map<String, Object> claims) {
        super("dummy value that should never be returned");
        Assert.notEmpty(claims, "claims cannot be empty");
        this.claims = Collections.unmodifiableMap(new LinkedHashMap<>(claims));
    }

    /**
     * Returns the Subject {@code (sub)} claim which identifies the principal that is the
     * subject of the JWT.
     *
     * @return the Subject identifier
     */
    public String getSubject() {
        return getClaimAsString(StandardClaimNames.SUB);
    }

    @Nullable
    public String getLogin() {
        return getClaimAsString(IsySecurityTokenClaimNames.LOGIN);
    }

    @Nullable
    public String getUserId() {
        return getClaimAsString(IsySecurityTokenClaimNames.USER_ID);
    }

    @Nullable
    public String getBhknz() {
        return getClaimAsString(IsySecurityTokenClaimNames.BHKNZ);
    }

    @Nullable
    public String getDisplayName() {
        return getClaimAsString(IsySecurityTokenClaimNames.DISPLAY_NAME);
    }

    @Override
    public Map<String, Object> getClaims() {
        return claims;
    }

    @Override
    public String getTokenValue() {
        throw new RuntimeException("This is an unauthenticated token that does not have a token value. "
                + "It is only to be used in, for example an unauthenticated batch or task, for storing user data "
                + "in claims for logging purposes. If you see this error it means the application likely tried "
                + "to pass the token as part of a request to another application and you should instead make sure "
                + "to perform a proper authentication.");
    }

    public ClaimsOnlyOAuth2AuthenticationToken asAuthentication() {
        return new ClaimsOnlyOAuth2AuthenticationToken(this);
    }

    /**
     * Return a {@link Builder} with the given subject set.
     *
     * @param subject
     *         the value to use for the Subject {@code (sub)} claim
     * @return a {@link Builder}
     */
    public static Builder withSubject(String subject) {
        return new Builder(subject);
    }

    public static final class Builder {

        /** The actual JWT builder. */
        private final Map<String, Object> claims = new LinkedHashMap<>();

        private Builder(String subject) {
            claims.put(StandardClaimNames.SUB, subject);
            // Treat this as a token that has all claims easily accessible via IsySecurityTokenUtil already set,
            // similar to an empty AufrufKontext where the initial values are empty strings.
            // Do this here instead of in build() so the keys can be overwritten with null (if desired).
            claims.put(IsySecurityTokenClaimNames.LOGIN, "");
            claims.put(IsySecurityTokenClaimNames.USER_ID, "");
            claims.put(IsySecurityTokenClaimNames.BHKNZ, "");
            claims.put(IsySecurityTokenClaimNames.DISPLAY_NAME, "");
        }

        /**
         * Use this login in the resulting {@link Jwt}.
         * It will be set in a claim that can be fetched by {@link IsySecurityTokenUtil#getLogin()}.
         *
         * @param login
         *         the login to use
         * @return the {@link Builder} for further configurations
         */
        public Builder login(String login) {
            claims.put(IsySecurityTokenClaimNames.LOGIN, login);
            return this;
        }

        /**
         * Use this user ID in the resulting {@link Jwt}.
         * It will be set in a claim that can be fetched by {@link IsySecurityTokenUtil#getUserId()}.
         *
         * @param userId
         *         the user ID to use
         * @return the {@link Builder} for further configurations
         */
        public Builder userId(String userId) {
            claims.put(IsySecurityTokenClaimNames.USER_ID, userId);
            return this;
        }

        /**
         * Use this BHKNZ in the resulting {@link Jwt}.
         * It will be set in a claim that can be fetched by {@link IsySecurityTokenUtil#getBhknz()}.
         *
         * @param bhknz
         *         the BHKNZ to use
         * @return the {@link Builder} for further configurations
         */
        public Builder bhknz(String bhknz) {
            claims.put(IsySecurityTokenClaimNames.BHKNZ, bhknz);
            return this;
        }

        /**
         * Use this display name in the resulting {@link Jwt}.
         * It will be set in a claim that can be fetched by {@link IsySecurityTokenUtil#getDisplayName()}.
         *
         * @param displayName
         *         the display name to use
         * @return the {@link Builder} for further configurations
         */
        public Builder displayName(String displayName) {
            claims.put(IsySecurityTokenClaimNames.DISPLAY_NAME, displayName);
            return this;
        }

        /**
         * Use this claim in the resulting {@link Jwt}.
         *
         * @param name
         *         the claim name
         * @param value
         *         the claim value
         * @return the {@link Builder} for further configurations
         */
        public Builder claim(String name, Object value) {
            claims.put(name, value);
            return this;
        }

        public ClaimsOnlyOAuth2Token build() {
            return new ClaimsOnlyOAuth2Token(claims);
        }
    }
}
