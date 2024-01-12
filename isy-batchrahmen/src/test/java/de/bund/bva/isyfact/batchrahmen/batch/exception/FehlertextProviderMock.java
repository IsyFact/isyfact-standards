package de.bund.bva.isyfact.batchrahmen.batch.exception;

import de.bund.bva.isyfact.exception.FehlertextProvider;

public class FehlertextProviderMock implements FehlertextProvider {
    @Override
    public String getMessage(String schluessel, String... parameter) {
        return "someMessage";
    }
}
