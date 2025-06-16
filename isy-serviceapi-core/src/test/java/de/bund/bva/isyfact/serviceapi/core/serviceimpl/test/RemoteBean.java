package de.bund.bva.isyfact.serviceapi.core.serviceimpl.test;

import java.lang.reflect.InvocationTargetException;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public interface RemoteBean {

	void eineMethode();

	void eineAndereMethode();

	void eineAndereMethode(Integer zahl);

	void eineAndereMethode(Double zahl);

	void eineAndereMethode(AufrufKontextTo to, Integer zahl);

	void methodeMitZweiToExceptions() throws IllegalStateException, PlisTechnicalToException, PlisBusinessToException;

	void methodeMitToException() throws IllegalStateException, PlisTechnicalToException;

	void methodeMitZweiTechnicalToExceptions() throws IllegalStateException, PlisTechnicalToException, TechnicalTestToException;

	void eineMethodeMitException() throws InvocationTargetException, PlisTechnicalToException;

	void eineMethodeMitBusinessException() throws BaseException, PlisTechnicalToException;

	void eineMethodeMitTechnicalRuntimeException() throws TechnicalRuntimeException, PlisTechnicalToException;
}
