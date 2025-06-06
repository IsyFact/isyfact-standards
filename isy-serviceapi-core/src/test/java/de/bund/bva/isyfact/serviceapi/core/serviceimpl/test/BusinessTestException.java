package de.bund.bva.isyfact.serviceapi.core.serviceimpl.test;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.FehlertextProvider;

public class BusinessTestException extends BusinessException {
    private static final long serialVersionUID = -3811370257922504594L;

    public BusinessTestException() {
        super("", new FehlertextProvider() {
            @Override
            public String getMessage(String schluessel, String... parameter) {
                return "";
            }
        });
    }

}
