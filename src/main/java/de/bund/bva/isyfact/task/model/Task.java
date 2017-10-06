package de.bund.bva.isyfact.task.model;

import de.bund.bva.isyfact.task.handler.AusfuehrungsplanHandler.Ausfuehrungsplan;
import de.bund.bva.isyfact.task.security.SecurityAuthenticator;

import java.time.LocalDateTime;

/**
 * Ein Objekt des Typs Task entspricht einer auszuführenden Aufgabe.
 * Jede Aufgabe ruft fachliche Operationen auf.
 * Das bedeutet, dass sich alle aufgerufenen fachlichen Operationen eindeutig einer bestimmten Ausführung einer Aufgabe zuordnen lassen.
 * Ein TaskData wird von der konkreten Implementierung eines auszuführenden Tasks abgeleitet.
 * Jeder auszuführende Task setzt seine id, seinen Namen und den Zeitpunkt der Ausführung.
 * Die Aufgaben, die erledigt werden sollen, werden in einer call- bzw. run-Methode
 * einer CallableOperation oder RepeatableOperation implementiert.
 * 
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface Task {

	String getId();

	void setId(String id);

	String getKorrelationsId();

	void setKorrelationsId(String korrelationsId);

	SecurityAuthenticator getSecurityAuthenticator();

	void setSecurityAuthenticator(SecurityAuthenticator securityAuthenticator);

	Operation getOperation();

	void setOperation(Operation operation);

	LocalDateTime getExecutionEndDateTime();

	void setExecutionEndDateTime(LocalDateTime executionEndDateTime);

	Ausfuehrungsplan getAusfuehrungsplan();

	void setAusfuehrungsplan(Ausfuehrungsplan ausfuehrungsplan);
}
