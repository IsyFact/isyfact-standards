package de.bund.bva.isyfact.logging.impl;

/**
 * Auflistung aller Standard-Logeinträge und deren Nachrichten, die in IsyFact-Logging genutzt werden.
 */
public enum EreignisschluesselRest {

    /** REST-Logging */
    /** Eingehender Request mit fachlichen Daten im Body. */
    EISYLO03001("Eingehender Request-Body: {}"),
    /** Ausgehender Requeset mit fachlichen Daten im Body */
    EISYLO03002("Ausgehender Request-Body: {}"),
    /** Eingehende Response mit fachlichen Daten im Body. */
    EISYLO03003("Eingehender Response-Body: {}"),
    /** Ausgehende Response mit fachlichen Daten im Body. */
    EISYLO03004("Ausgehender Response-Body: {}"),
    /** Eingehender Request (mit technischen Informationen). */
    EISYLO03005("Eingehender Request: {}."),
    /** Ausgehender Request (mit technischen Informationen). */
    EISYLO03006("Ausgehender Request: {}."),
    /** Eingehende Response (mit technischen Informationen). */
    EISYLO03007("Eingehende Response: {}."),
    /** Ausgehende Response (mit technischen Informationen). */
    EISYLO03008("Ausgehende Response: {}."),
    /** Dauer des Aufrufes eines anderen Systems. */
    EISYLO03009("Der Aufruf von {} dauerte {} ms."),
    /** Dauer der Verarbeitung eines Request eines anderen Systems. */
    EISYLO03010("Die Verarbeitung fuer {} dauerte {} ms."),
    /** Dauer der Verarbeitung eines Request eines anderen Systems. */
    EISYLO03011("Eingehender Request von Benutzer '{}' (Login='{}',InterneKennung='{}') mit Behoerde '{}'.");

    /**
     * Die Nachricht des Logschlüssels.
     */
    private final String nachricht;

    /**
     * Konstruktor der Klasse.
     *
     * @param nachricht
     *            die zum Logschlüssel gehörende Nachricht.
     */
    private EreignisschluesselRest(String nachricht) {
        this.nachricht = nachricht;
    }

    /**
     * Liefert den Wert des Attributs 'nachricht'.
     *
     * @return Wert des Attributs.
     */
    public String getNachricht() {
        return this.nachricht;
    }
}
