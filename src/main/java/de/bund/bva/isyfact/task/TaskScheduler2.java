package de.bund.bva.isyfact.task;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import de.bund.bva.isyfact.task.model.Task;

/**
 * Der TaskScheduler bietet die Möglichkeit, dass Aufgaben (Tasks) zu bestimmten Zeitpunkten ausgeführt werden können.
 *
 * TODO: Der TaskScheduler sollte meines Erachtens das folgende tun:
 * * Beim Start aus isy-konfiguration eine Liste von Task-Konfigurationen auslesen.
 * * Mit dieser Information baut er die Tasks auf und plant deren Ausführung (Aufruf des ScheduledExecutorService).
 * * Die Futures dieser Ausführungen sammelt er in einer Liste.
 * * Er startet einen weiteren Thread, der alle Futures regelmäßig auf Completion prüft.
 * * Wenn ein Task aufgrund eines Fehlers abbricht, wird das entsprechende Future completed.
 * * Der TaskScheduler loggt dies (WARN) und startet den Task neu.
 * * Wenn die Anwendung herunterfährt, muss der ScheduledExecutorServicea alle laufenden Tasks canceln.
 *
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface TaskScheduler2 {

	// TODO Exception darf später nicht geworfen werden, bzw. muss eingegrenzt werden.
	void fuehreKonfigurierteTasksAus() throws Exception;

	/**
	 * Plant einen Task.
	 *
	 *
	 * @param task
	 * @return a ScheduledFuture<String>
	 */
	ScheduledFuture<?> schedule(Task task) throws NoSuchMethodException, Exception;

	/**
	 * Plant einen TaskData.
	 *
	 *
	 * @param task
	 * @return a ScheduledFuture<String>
	 */
	ScheduledFuture<?> scheduleAtFixedRate(Task task) throws NoSuchMethodException, Exception;

	/**
	 * Plant einen TaskData.
	 *
	 *
	 * @param task
	 * @return a ScheduledFuture<String>
	 */
	ScheduledFuture<?> scheduleWithFixedDelay(Task task) throws NoSuchMethodException, Exception;

}
