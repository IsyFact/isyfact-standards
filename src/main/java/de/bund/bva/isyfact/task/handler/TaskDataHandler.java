package de.bund.bva.isyfact.task.handler;

import de.bund.bva.isyfact.task.model.TaskData;

/**
 * Der TaskDataHandler ist eine Werkzeugklasse für TaskData-Objekte.
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface TaskDataHandler {

    /**
     *
     * @param id
     * @param username
     * @param password
     * @param executionDateTime
     * @param operationName
     * @param hostName
     * @param days
     * @param hours
     * @param minutes
     * @param seconds
     * @return
     */
    TaskData createTaskData(
            String id,
            String username,
            String password,
            String executionDateTime,
            String operationName,
            String hostName,
            String days,
            String hours,
            String minutes,
            String seconds
    );

    /**
     *
     * @param id
     * @param username
     * @param password
     * @param executionDateTime
     * @param operationName
     * @param hostName
     * @return
     */
    TaskData createTaskData(
            String id,
            String username,
            String password,
            String executionDateTime,
            String operationName,
            String hostName
    );
}