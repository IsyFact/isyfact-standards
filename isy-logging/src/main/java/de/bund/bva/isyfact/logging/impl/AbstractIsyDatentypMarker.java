package de.bund.bva.isyfact.logging.impl;

import de.bund.bva.isyfact.logging.IsyDatentypMarker;

public abstract class AbstractIsyDatentypMarker extends IsyMarkerImpl implements IsyDatentypMarker {

    /**
     * Eindeutige UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Erzeugt einen Datentyp-Marker.
     *
     * @param typ Datentyp des Logeintrags
     */
    public AbstractIsyDatentypMarker(String typ) {
        super(MarkerSchluessel.DATENTYP, typ);
    }

}
