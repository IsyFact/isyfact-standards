package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.handler.*;
import de.bund.bva.isyfact.task.model.Operation;
import de.bund.bva.isyfact.task.model.TaskData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
	 * @param taskData
	 * @return die Instanz der CallableOperation
	 * @throws Exception
	 */
	@Override
	public synchronized Operation createOperationInstance(TaskData taskData) {

			// TODO Reparieren
			// throws CreateOperationInstanceException {
		String operationName = taskData.getOperationName();
		Class<?> clazz = null;
		try {
			clazz = Class.forName(operationName);
		} catch (ClassNotFoundException e) {
			// TODO Reparieren
			// throw new CreateOperationInstanceException();
		}
		Constructor<?> ctr = null;
		try {
			ctr = clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			// TODO Reparieren
			//throw new CreateOperationInstanceException();
		}
		try {
			return (Operation) ctr.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			// TODO Reparieren
			// throw new CreateOperationInstanceException();
		}
		// Hier kommt man nur im Fehlerfall an.
		// TODO Reparieren
		return null;
	}
}