package de.bund.bva.isyfact.security.oauth2.client.authentication.token;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;

/**
 * AuthenticationToken holding parameters required for creating a Client to use with Resource Owner Password Credentials Flow authentication.
 */
public class PasswordClientRegistrationAuthenticationToken extends AbstractClientRegistrationAuthenticationToken {

    /** The resource owner's username. */
    private final String username;

    /** The resource owner's password. */
    private final String password;

    /**
     * The certificate OU to send as part of the authentication request (optional).
     * If the certificaet OU is set the {@link #bhknz} must also be set.
     */
    @Nullable
    private final String certificateOu;

    public PasswordClientRegistrationAuthenticationToken(ClientRegistration clientRegistration, String username, String password, @Nullable String bhknz, @Nullable String certificateOu) {
        super(username, clientRegistration, bhknz);
        Assert.isTrue(!(bhknz != null ^ certificateOu != null), "if bhknz is set the certifiateOu must also be set");
        this.username = username;
        this.password = password;
        this.certificateOu = certificateOu;
        setAuthenticated(false);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Nullable
    public String getCertificateOu() {
        return certificateOu;
    }

    /**
     * Generates a cache key that includes the following fields.
     * <ul>
     *     <li>principal</li>
     *     <li>bhknz</li>
     *     <li>certificateOu</li>
     *     <li>issuerLocation</li>
     *     <li>clientId</li>
     *     <li>clientSecret</li>
     *     <li>authorizationGrantType</li>
     *     <li>username</li>
     *     <li>password</li>
     * </ul>
     *
     * @return the generated cache key as hash code or null
     */
    @Override
    public byte[] generateCacheKey(String hashAlgorithm, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);

            digest.update(super.generateCacheKey(hashAlgorithm, salt));

            List<String> additionalValues = Arrays.asList(
                String.valueOf(getUsername()),
                String.valueOf(getPassword()),
                String.valueOf(getCertificateOu())
            );
            byte[] additionalBytes = SerializationUtils.serialize(additionalValues);
            digest.update(additionalBytes);

            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(hashAlgorithm + " nicht verfügbar.", e);
        }
    }
}
