package de.bund.bva.isyfact.logging.impl;

public class PersonenbezogeneDatenMarker extends AbstractIsyDatentypMarker {

    /**
     * Ein Marker für personenbezogene Daten.
     */
    public static final PersonenbezogeneDatenMarker INSTANZ = new PersonenbezogeneDatenMarker();

    private PersonenbezogeneDatenMarker() {
        super("Personenbezogene Daten");
    }
}
