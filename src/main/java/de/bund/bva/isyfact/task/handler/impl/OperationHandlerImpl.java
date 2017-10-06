package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.handler.*;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.isyfact.task.model.Operation;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.springframework.context.ApplicationContext;

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
	public synchronized Operation getOperation(String id, ApplicationContext applicationContext) {
		return applicationContext.getBean(id, Operation.class);
	}
}