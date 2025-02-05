package de.bund.bva.isyfact.ueberwachung.common.data;

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalException;

/**
 * Implementation of the {@link TechnicalException} for testing purposes.
 */
public class TestTechnicalException extends TechnicalException {

    public TestTechnicalException(String message) {
        super("test123", new FehlertextProvider() {
            @Override
            public String getMessage(String schluessel, String... parameter) {
                return message;
            }
        });
    }
}