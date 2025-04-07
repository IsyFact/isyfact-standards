package de.bund.bva.isyfact.security.config;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Builder class for additional authentication data that can be used for authentication with an OAuth 2.0 client.
 * Can create combinations of bhknz, username + password and username + password + bhknz.
 */
public final class AdditionalCredentials {

    /** The resource owner's username. */
    @Nullable
    private final String username;

    /** The resource owner's password. */
    @Nullable
    private final String password;

    /** The BHKNZ to send as part of the authentication request. */
    @Nullable
    private final String bhknz;

    private AdditionalCredentials(@Nullable String username, @Nullable String password, @Nullable String bhknz) {
        this.username = username;
        this.password = password;
        this.bhknz = bhknz;
    }

    /**
     * Creates a builder-object for AdditionalCredentials with username and password.
     *
     * @param username the username of the resource owner
     * @param password the password of the resource owner
     * @return a new AdditionalCredentials instance
     */
    public static AdditionalCredentials createWithUsernamePassword(String username, String password) {
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(password, "password cannot be null");
        return new AdditionalCredentials(username, password, null);
    }

    /**
     * Creates a builder-object for AdditionalCredentials with username, password and BHKNZ.
     *
     * @param username the username of the resource owner
     * @param password the password of the resource owner
     * @param bhknz the BHKNZ to be sent as part of the authentication request
     * @return a new AdditionalCredentials instance
     */
    public static AdditionalCredentials createWithUsernamePasswordBhknz(String username, String password, String bhknz) {
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(password, "password cannot be null");
        Assert.notNull(bhknz, "bhknz cannot be null");
        return new AdditionalCredentials(username, password, bhknz);
    }

    /**
     * Creates a builder-object for AdditionalCredentials only with BHKNZ.
     *
     * @param bhknz the BHKNZ to be sent as part of the authentication request
     * @return a new AdditionalCredentials instance
     */
    public static AdditionalCredentials createWithBhknz(String bhknz) {
        Assert.notNull(bhknz, "bhknz cannot be null");
        return new AdditionalCredentials(null, null, bhknz);
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    @Nullable
    public String getBhknz() {
        return bhknz;
    }

    /**
     * Checks whether the username and password are set.
     *
     * @return true if username and password are set, otherwise false
     */
    public boolean hasUsernamePassword() {
        return username != null && password != null;
    }

    /**
     * Checks whether the BHKNZ is set.
     *
     * @return true if BHKNZ is set, otherwise false
     */
    public boolean hasBhknz() {
        return bhknz != null;
    }
}
