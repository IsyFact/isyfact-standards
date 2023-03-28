package de.bund.bva.isyfact.security.test;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderStub;

/**
 * Class to generate RSA key pairs. It is use by the {@link EmbeddedOidcProviderStub} to generate key pairs if they are not
 * explicitly provided. It can be called via the {@link #main(String[])} method to create key pairs for a static configuration.
 */
public class RsaKeyGenerator {

    private final KeyPair keyPair;

    public RsaKeyGenerator() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to generate Keypair", e);
        }
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public static String encodePublicKey(PublicKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey decodePublicKey(String encodedKey) {
        final byte[] bytes = Base64.getDecoder().decode(encodedKey);
        final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decode key", e);
        }
    }

    public static String encodePrivateKey(PrivateKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PrivateKey decodePrivateKey(String encodedKey) {
        final byte[] bytes = Base64.getDecoder().decode(encodedKey);
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decode key", e);
        }
    }

    public static KeyPair decodeKeyPair(String encodedPublicKey, String encodedPrivateKey) {
        return new KeyPair(decodePublicKey(encodedPublicKey), decodePrivateKey(encodedPrivateKey));
    }

    /**
     * Star method to create a public/private RSA key pair.
     *
     * @param args
     *         command line arguments (ignored)
     */
    public static void main(String[] args) {
        final RsaKeyGenerator gen = new RsaKeyGenerator();
        System.out.println("Public key:" + encodePublicKey(gen.getPublicKey()));
        System.out.println("Private key:" + encodePrivateKey(gen.getPrivateKey()));
    }

}
