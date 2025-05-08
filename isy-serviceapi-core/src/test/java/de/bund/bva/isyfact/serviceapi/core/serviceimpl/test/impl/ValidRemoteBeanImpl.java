package de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl;

import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.ValidRemoteBean;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;

public class ValidRemoteBeanImpl implements ValidRemoteBean {

	@Override
	public void eineMethode() throws PlisTechnicalToException {

	}

	@Override
	public void eineAndereMethode() throws PlisTechnicalToException {

	}

	@Override
	public void methodeMitException() throws TechnicalException, PlisTechnicalToException {

	}

	@Override
	public void methodeMitParametern(Integer zahl, String zeichenkette) throws PlisTechnicalToException {

	}
}
