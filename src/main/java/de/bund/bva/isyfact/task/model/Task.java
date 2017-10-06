package de.bund.bva.isyfact.task.model;

/**
 * Eine Task enthält die Anweisungen, die erledigt werden sollen.
 * Wenn die Anweisungen erfolgreich durchlaufen wurden, gibt hasBeenExecutedSuccessfully true zurück.
 * Sollte der Durchlauf unterbrochen werden, wird die ErrorMessage notiert.
 *
 * @author Alexander Salvanos, msg systems ag
 */
public interface Task {

    void execute();

    /**
     * Zeichnet eine erfolgreiche Ausführung des Tasks auf.
     */
    void zeichneErfolgreicheAusfuehrungAuf();

    /**
     * Zeichnet eine fehlgeschlagene Ausführung des Tasks auf.
     *
     * @param fehler
     *     aufgetretene Ausnahme
     */
    void zeichneFehlgeschlageneAusfuehrungAuf(Exception fehler);

}
