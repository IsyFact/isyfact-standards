package de.bund.bva.isyfact.security.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AdditionalCredentialsTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String TEST_BHKNZ = "900600";
    private static final String TEST_OU = "Foo-OU";

    @Test
    public void testWithUsernamePassword() {
        AdditionalCredentials credentials = AdditionalCredentials.createWithUsernamePassword(TEST_USERNAME, TEST_PASSWORD);

        assertEquals(TEST_USERNAME, credentials.getUsername());
        assertEquals(TEST_PASSWORD, credentials.getPassword());

        assertNull(credentials.getBhknz());

        assertTrue(credentials.hasUsernamePassword());
        assertFalse(credentials.hasBhknz());
        assertFalse(credentials.hasBhknzOu());
    }

    @Test
    public void testWithUsernamePasswordBhknzOu() {
        AdditionalCredentials credentials = AdditionalCredentials.createWithUsernamePasswordBhknzOu(
                TEST_USERNAME, TEST_PASSWORD, TEST_BHKNZ, TEST_OU);

        assertEquals(TEST_USERNAME, credentials.getUsername());
        assertEquals(TEST_PASSWORD, credentials.getPassword());
        assertEquals(TEST_BHKNZ, credentials.getBhknz());
        assertEquals(TEST_OU, credentials.getCertificateOu());

        assertTrue(credentials.hasUsernamePassword());
        assertTrue(credentials.hasBhknz());
        assertTrue(credentials.hasBhknzOu());
    }

    @Test
    public void testWithBhknz() {
        AdditionalCredentials credentials = AdditionalCredentials.createWithBhknz(TEST_BHKNZ);

        assertEquals(TEST_BHKNZ, credentials.getBhknz());

        assertNull(credentials.getUsername());
        assertNull(credentials.getPassword());

        assertFalse(credentials.hasUsernamePassword());
        assertTrue(credentials.hasBhknz());
        assertFalse(credentials.hasBhknzOu());
    }

    @Test
    public void testWithUsernamePasswordNullUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> AdditionalCredentials.createWithUsernamePassword(null, TEST_PASSWORD));
    }

    @Test
    public void testWithUsernamePasswordNullPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> AdditionalCredentials.createWithUsernamePassword(TEST_USERNAME, null));
    }

    @Test
    public void testWithUsernamePasswordBhknzOuNullUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> AdditionalCredentials.createWithUsernamePasswordBhknzOu(null, TEST_PASSWORD, TEST_BHKNZ, TEST_OU));
    }

    @Test
    public void testWithUsernamePasswordBhknzOuNullPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> AdditionalCredentials.createWithUsernamePasswordBhknzOu(TEST_USERNAME, null, TEST_BHKNZ, TEST_OU));
    }

    @Test
    public void testWithUsernamePasswordBhknzOuNullBhknz() {
        assertThrows(IllegalArgumentException.class,
                () -> AdditionalCredentials.createWithUsernamePasswordBhknzOu(TEST_USERNAME, TEST_PASSWORD, null, TEST_OU));
    }

    @Test
    public void testWithUsernamePasswordBhknzOuNullOu() {
        assertThrows(IllegalArgumentException.class,
                () -> AdditionalCredentials.createWithUsernamePasswordBhknzOu(TEST_USERNAME, TEST_PASSWORD, TEST_BHKNZ, null));
    }

    @Test
    public void testWithBhknzNullBhknz() {
        assertThrows(IllegalArgumentException.class,
                () -> AdditionalCredentials.createWithBhknz(null));
    }

    @Test
    public void testHasUsernamePasswordBothNull() {
        AdditionalCredentials credentials = AdditionalCredentials.createWithBhknz(TEST_BHKNZ);

        assertFalse(credentials.hasUsernamePassword());
    }

    @Test
    public void testHasBhknzNullBhknz() {
        AdditionalCredentials credentials = AdditionalCredentials.createWithUsernamePassword(TEST_USERNAME, TEST_PASSWORD);

        assertFalse(credentials.hasBhknz());
        assertFalse(credentials.hasBhknzOu());
    }
}
