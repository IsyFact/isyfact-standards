package de.bund.bva.isyfact.serviceapi.core.serviceimpl.test;

import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;

public class TechnicalTestToException extends PlisTechnicalToException {
	private static final long serialVersionUID = -1453296643631781592L;

	public TechnicalTestToException(String message, String ausnahmeId, String uniqueId) {
		super(message, ausnahmeId, uniqueId);
	}

}
