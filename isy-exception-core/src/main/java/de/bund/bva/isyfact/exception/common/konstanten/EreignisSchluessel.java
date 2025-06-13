package de.bund.bva.isyfact.exception.common.konstanten;

/**
 * Diese Klasse enthält Ereignisschlüssel für das Logging.
 */
public class EreignisSchluessel {

    /** Die TransportException implementiert nicht den benoetigten Konstruktor mit den Parametern... */
    public static final String KONSTRUKTOR_NICHT_IMPLEMENTIERT = "EPLEXC00001";

    /**
     * Die TransportException {} konnte nicht erzeugt werden. Sie ist entweder ein Interface oder aber eine
     * abstrakte Klasse. Sie TransportException muss aber eine konkrete Implementierung sein
     */
    public static final String TRANSPORT_EXCEPTION_INTERFACE_ABSTRAKT = "EPLEXC00002";

    /**
     * Die TransportException {} konnte nicht erzeugt werden. Die Parameterwerte ({}, {}, {}) entsprechen
     * nicht den benoetigten Werten: String message, String ausnahmeId, String uniqueId.
     */
    public static final String PARAMETER_FALSCH = "EPLEXC00003";

    /**
     * Der Zugriff auf den Konstruktur der TransportException {} verstoesst gegen die
     * Java-Sicherheitsrichtlinien.
     */
    public static final String KONSTRUKTOR_SICHERHEITSRICHTLINIEN = "EPLEXC00004";

    /**
     * Der Aufruf des Konstruktors der TransportException {} fuehrte innerhalb des aufgerufenen Konstruktors
     * zu einer Exception.
     */
    public static final String KONSTRUKTOR_EXCEPTION = "EPLEXC00005";
}
