package de.bund.bva.isyfact.serviceapi.core.serviceimpl.test;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.FehlertextProvider;

public class SomeOtherBusinessException extends BaseException {
    public SomeOtherBusinessException(FehlertextProvider fehlertextProvider) {
        super("SOME_OTHER_BUS_EX", fehlertextProvider, "Parameter1", "Parameter2");
    }
}