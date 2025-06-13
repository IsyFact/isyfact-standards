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

    @Test
    public void testWithUsernamePassword() {
        AdditionalCredentials credentials = AdditionalCredentials.createWithUsernamePassword(TEST_USERNAME, TEST_PASSWORD);

        assertEquals(TEST_USERNAME, credentials.getUsername());
        assertEquals(TEST_PASSWORD, credentials.getPassword());

        assertNull(credentials.getBhknz());

        assertTrue(credentials.hasUsernamePassword());
        assertFalse(credentials.hasBhknz());
    }

    @Test
    public void testWithUsernamePasswordBhknz() {
        AdditionalCredentials credentials = AdditionalCredentials.createWithUsernamePasswordBhknz(
                TEST_USERNAME, TEST_PASSWORD, TEST_BHKNZ);

        assertEquals(TEST_USERNAME, credentials.getUsername());
        assertEquals(TEST_PASSWORD, credentials.getPassword());
        assertEquals(TEST_BHKNZ, credentials.getBhknz());

        assertTrue(credentials.hasUsernamePassword());
        assertTrue(credentials.hasBhknz());
    }

    @Test
    public void testWithBhknz() {
        AdditionalCredentials credentials = AdditionalCredentials.createWithBhknz(TEST_BHKNZ);

        assertEquals(TEST_BHKNZ, credentials.getBhknz());

        assertNull(credentials.getUsername());
        assertNull(credentials.getPassword());

        assertFalse(credentials.hasUsernamePassword());
        assertTrue(credentials.hasBhknz());
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
    public void testWithUsernamePasswordBhknzNullUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> AdditionalCredentials.createWithUsernamePasswordBhknz(null, TEST_PASSWORD, TEST_BHKNZ));
    }

    @Test
    public void testWithUsernamePasswordBhknzNullPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> AdditionalCredentials.createWithUsernamePasswordBhknz(TEST_USERNAME, null, TEST_BHKNZ));
    }

    @Test
    public void testWithUsernamePasswordBhknzNullBhknz() {
        assertThrows(IllegalArgumentException.class,
                () -> AdditionalCredentials.createWithUsernamePasswordBhknz(TEST_USERNAME, TEST_PASSWORD, null));
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
    }
}
