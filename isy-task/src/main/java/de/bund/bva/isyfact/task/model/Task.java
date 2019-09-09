package de.bund.bva.isyfact.task.model;

/**
 * Eine Task enthält die Anweisungen, die erledigt werden sollen und Methoden zur Steuerung und Überwachung
 * eines Tasks.
 */
public interface Task {

    /**
     * Enthält die eigentliche Operation, die der Task ausführen soll.
     */
    void execute();

    /**
     * Prüft, ob der Task manuell deaktiviert ist.
     */
    boolean isDeaktiviert();

    /**
     * Aktiviert den Task manuell.
     */
    void aktivieren();

    /**
     * Deaktiviert den Task manuell.
     */
    void deaktivieren();

    /**
     * Zeichnet eine erfolgreiche Ausführung des Tasks auf.
     */
    void zeichneErfolgreicheAusfuehrungAuf();

    /**
     * Zeichnet eine fehlgeschlagene Ausführung des Tasks auf.
     *
     * @param fehler
     *            aufgetretene Ausnahme
     */
    void zeichneFehlgeschlageneAusfuehrungAuf(Exception fehler);

}
