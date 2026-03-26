package de.bund.bva.isyfact.logging.impl;

import java.io.Serial;

public class PersonenbezogeneDatenMarker extends AbstractIsyDatentypMarker {

    @Serial
    private static final long serialVersionUID = 7491072361151423674L;

    /**
     * Ein Marker für personenbezogene Daten.
     */
    public static final PersonenbezogeneDatenMarker INSTANZ = new PersonenbezogeneDatenMarker();

    private PersonenbezogeneDatenMarker() {
        super("Personenbezogene Daten");
    }
}
