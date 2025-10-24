package de.bund.bva.isyfact.logging.impl;

import java.io.Serial;

import de.bund.bva.isyfact.logging.IsyDatentypMarker;

/**
 * Abstrakte Implementierung eines Datentyp-Markers, das den Namen des Felds im Logeintrag auf {@link
 * MarkerSchluessel#DATENTYP} festlegt.
 */
public abstract class AbstractIsyDatentypMarker extends IsyMarkerImpl implements IsyDatentypMarker {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Konstruktor für Datentyp-Marker.
     *
     * @param typ Datentyp des Logeintrags
     */
    public AbstractIsyDatentypMarker(String typ) {
        super(MarkerSchluessel.DATENTYP, typ);
    }

}
