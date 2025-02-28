package de.bund.bva.isyfact.ueberwachung.common.data;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.FehlertextProvider;

/**
 * Implementation of the {@link BusinessException} for testing purposes.
 */
public class TestBusinessException extends BusinessException {

    public TestBusinessException(String message) {
        super("test123", new FehlertextProvider() {
            @Override
            public String getMessage(String schluessel, String... parameter) {
                return message;
            }
        });
    }
}