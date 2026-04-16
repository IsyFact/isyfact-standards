package de.bund.bva.isyfact.logging.impl;

import java.io.Serial;

/**
 * Datentyp-Marker für Technikdaten.
 */
public class TechnikdatenMarker extends AbstractIsyDatentypMarker {

    @Serial
    private static final long serialVersionUID = 7491072361151423674L;

    /**
     * Erzeugt einen Marker für Technikdaten.
     */
    public TechnikdatenMarker() {
        super("Technikdaten");
    }

}
