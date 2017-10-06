package de.bund.bva.isyfact.task.model;

/**
 * Eine Operation enthält die Anweisungen, die erledigt werden sollen.
 * Wenn die Anweisungen erfolgreich durchlaufen wurden, gibt hasBeenExecutedSuccessfully true zurück.
 * Sollte der Durchlauf unterbrochen werden, wird die ErrorMessage notiert.
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public interface Operation {

    void execute();

}
