package de.bund.bva.isyfact.logging.impl;

import java.io.Serial;

/**
 * Datentyp-Marker für Fachdaten.
 */
public class FachdatenMarker extends AbstractIsyDatentypMarker {

    @Serial
    private static final long serialVersionUID = 3562948107808272999L;

    /**
     * Erzeugt einen Marker für Fachdaten.
     */
    public FachdatenMarker() {
        super("Fachdaten");
    }

}
