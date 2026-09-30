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

    /** The certificate OU to send as part of the authentication request. */
    @Nullable
    private final String certificateOu;

    private AdditionalCredentials(@Nullable String username, @Nullable String password, @Nullable String bhknz, @Nullable String certificateOu) {
        this.username = username;
        this.password = password;
        this.bhknz = bhknz;
        this.certificateOu = certificateOu;
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
        return new AdditionalCredentials(username, password, null, null);
    }

    /**
     * Creates a builder-object for AdditionalCredentials with username, password, BHKNZ and certificate OU.
     *
     * @param username the username of the resource owner
     * @param password the password of the resource owner
     * @param bhknz the BHKNZ to be sent as part of the authentication request
     * @param certificateOu the certificate OU to be sent as part of the authentication request
     * @return a new AdditionalCredentials instance
     */
    public static AdditionalCredentials createWithUsernamePasswordBhknzOu(String username, String password, String bhknz, String certificateOu) {
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(password, "password cannot be null");
        Assert.notNull(bhknz, "bhknz cannot be null");
        Assert.notNull(certificateOu, "certificateOu cannot be null");
        return new AdditionalCredentials(username, password, bhknz, certificateOu);
    }

    /**
     * Creates a builder-object for AdditionalCredentials only with BHKNZ.
     *
     * @param bhknz the BHKNZ to be sent as part of the authentication request
     * @return a new AdditionalCredentials instance
     */
    public static AdditionalCredentials createWithBhknz(String bhknz) {
        Assert.notNull(bhknz, "bhknz cannot be null");
        return new AdditionalCredentials(null, null, bhknz, null);
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

    @Nullable
    public String getCertificateOu() {
        return certificateOu;
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
     * @return {@code true} if BHKNZ is set, otherwise {@code false}
     * @deprecated Checking only for the BHKNZ is deprecated. Use {@link #hasBhknzOu()} instead.
     */
    @Deprecated
    public boolean hasBhknz() {
        return bhknz != null;
    }

    /**
     * Checks whether the BHKNZ and certificate OU are set.
     *
     * @return {@code true} if BHKNZ and certificate OU are set, otherwise {@code false}
     */
    public boolean hasBhknzOu() {
        return bhknz != null && certificateOu != null;
    }
}
