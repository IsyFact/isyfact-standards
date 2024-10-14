package de.bund.bva.isyfact.sicherheit.common.exception;

import org.junit.jupiter.api.Test;

import java.util.MissingResourceException;

import static org.junit.jupiter.api.Assertions.*;

class SicherheitFehlertextProviderTest {

    @Test
    void testGetMessageWithoutParameters() {
        // Given
        String schluessel = "SIC2003";
        String expectedMessage = "Das in der Anwendung zu konfigurierende Mapping von Rollen zu Rechten enthält nicht das erforderliche Recht: {0}.";

        // When
        String actualMessage = SicherheitFehlertextProvider.getMessage(schluessel);

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetMessageWithParameters() {
        // Given
        String schluessel = "SIC1000";
        String[] params = {"param1"};
        String expectedMessage = "Der übergebene Parameter \"param1\" ist null oder leer.";

        // When
        String actualMessage = new SicherheitFehlertextProvider().getMessage(schluessel, params);

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetMessageWithMissingKey() {
        // Given
        String schluessel = "non.existent.key";

        // When
        Exception exception = assertThrows(MissingResourceException.class, () -> {
            SicherheitFehlertextProvider.getMessage(schluessel);
        });

        // Then
        String expectedMessage = "Can't find resource for bundle java.util.PropertyResourceBundle, key non.existent.key";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testGetMessageWithNullKey() {
        // Given

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            SicherheitFehlertextProvider.getMessage(null);
        });

        // Then
        String expectedMessage = "Key is null";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetMessageWithParametersAndNullKey() {
        // Given
        String[] params = {"param1"};
        SicherheitFehlertextProvider provider = new SicherheitFehlertextProvider();

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            provider.getMessage(null, params);
        });

        // Then
        String expectedMessage = "Key is null";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}

