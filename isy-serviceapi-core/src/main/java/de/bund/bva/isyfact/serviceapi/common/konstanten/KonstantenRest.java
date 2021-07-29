package de.bund.bva.isyfact.serviceapi.common.konstanten;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class KonstantenRest {

    private KonstantenRest() {
    }

    /** Name des (aufrufenden) Systems. */
    public static final String KEY_SYSTEMNAME = "isy-aufrufendes-system";

    /** Name des aufgerufenen Nachbarsystems. */
    public static final String KEY_SYSTEMNAME_REMOTE = "isy-aufgerufenes-system";

    /** Unix-Zeitpunkt des Empfangs der Nachricht. */
    public static final String KEY_EMPFANGSZEITPUNKT = "isy-empfangszeitpunkt";

    /** Unix-Zeitpunkt des Versands der Nachricht. */
    public static final String KEY_SENDEZEITPUNKT = "isy-sendezeitpunkt";

    /** Zwischengespeicherte Log-Nachricht (bei Konfiguration Loggen Daten bei Exception). */
    public static final String KEY_STORED_LOG_MESSAGE = "isy-stored-log-message";

    /** Zwischengespeicherter Logger (bei Konfiguration Loggen Daten bei Exception). */
    public static final String KEY_STORED_LOGGER = "isy-stored-logger";

    /**
     * Positivliste der logbaren Header-Elemente. Aus datenschutzrechtlichen Gruenden werden nicht
     * grundsaetzlich alle vorhandenen Header-Elemente geloggt.
     */
    public static final Set<String> LOGGABLE_HEADER = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList("Accept", "accept-encoding", "cache-control", "connection",
            "Content-Length", "content-type", "host", "user-agent", KEY_SYSTEMNAME, KEY_SYSTEMNAME_REMOTE)));
}
