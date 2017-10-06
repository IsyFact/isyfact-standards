package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.handler.*;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.model.Operation;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.OPERATION;
import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.PRAEFIX;

/**
 * Der OperationHandler ist eine Werkzeugklasse für den Bau von Operation-Instanzen.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class OperationHandlerImpl implements OperationHandler {
	private final static IsyLogger LOG = IsyLoggerFactory.getLogger(OperationHandlerImpl.class);

	/**
	 * Erzeugt die Instanz, die als CallableOperation des Tasks ausgeführt werden soll.
	 *
	 * Zunächst wird der Name der CallableOperation aus dem TaskData geholt.
	 * Über die Reflection API wird eine Instanz der CallableOperation erzeugt und als Rückgabewert zurückgeliefert.
	 *
	 * @return die Instanz der CallableOperation
	 * @throws Exception
	 */
	@Override
	public synchronized Operation getOperation(String id, Konfiguration konfiguration) {
		String operation = konfiguration.getAsString(PRAEFIX + id + OPERATION);

		//TODO: Frage an Bjoern - Wie wird die Exception injiziert?
		// throws CreateOperationInstanceException {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(operation);
		} catch (ClassNotFoundException e) {
			//TODO: Frage an Bjoern - Wie wird die Exception injiziert?
			// throw new CreateOperationInstanceException();
		}
		Constructor<?> ctr = null;
		try {
			ctr = clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			//TODO: Frage an Bjoern - Wie wird die Exception injiziert?
			//throw new CreateOperationInstanceException();
		}
		try {
			return (Operation) ctr.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			//TODO: Frage an Bjoern - Wie wird die Exception injiziert?
			// throw new CreateOperationInstanceException();
		}
		//TODO: Frage an Bjoern - Wie wird die Exception injiziert?
		// return ...
		return null;
	}
}