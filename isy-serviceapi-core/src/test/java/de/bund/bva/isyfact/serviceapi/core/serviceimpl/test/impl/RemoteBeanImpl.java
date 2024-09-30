package de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.BusinessTestException;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.TechnicalRuntimeTestException;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

import java.lang.reflect.InvocationTargetException;

public class RemoteBeanImpl implements RemoteBean {

	@Override
	public void eineMethode() {
		// noop
	}

	@Override
	public void eineAndereMethode() {
		// noop
	}

	@Override
	public void methodeMitZweiToExceptions() throws IllegalStateException {
		// noop
	}

	@Override
	public void methodeMitToException() throws IllegalStateException {
		// noop
	}

	@Override
	public void methodeMitZweiTechnicalToExceptions() throws IllegalStateException{
		// noop
	}

	@Override
	public void eineAndereMethode(AufrufKontextTo to, Integer zahl) {
		// noop
	}

	public void nichtImInterfaceDeklariert() {
		// noop
	}

	@Override
	public void eineAndereMethode(Integer zahl) {
		// noop
	}

	@Override
	public void eineAndereMethode(Double zahl) {
		// noop
	}

	@Override
	public void eineMethodeMitException() throws InvocationTargetException {
		IllegalArgumentException e = new IllegalArgumentException("eine simulierte Exception.");
		throw new InvocationTargetException(e);
	}

	@Override
	public void eineMethodeMitBusinessException() throws BaseException {
		throw new BusinessTestException();
	}

	@Override
	public void eineMethodeMitTechnicalRuntimeException() throws TechnicalRuntimeException {
		throw new TechnicalRuntimeTestException("", null, null);
	}

}
