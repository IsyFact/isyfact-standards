package de.bund.bva.isyfact.serviceapi.core.serviceimpl.test;

import java.util.Arrays;

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

public class TechnicalRuntimeTestException extends TechnicalRuntimeException {
	private static final long serialVersionUID = 4944917830826917167L;

	public TechnicalRuntimeTestException(String ausnahmeId, Throwable throwable, String[] parameter) {
		super(ausnahmeId, throwable, new FehlertextProvider() {
			@Override
			public String getMessage(String schluessel, String... parameter) {				
				return schluessel + ": " + Arrays.toString(parameter);
			}
		}, parameter);
	}

}
