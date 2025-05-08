package de.bund.bva.isyfact.persistence.usertype;

import de.bund.bva.isyfact.persistence.annotation.EnumId;

/**
 * Test-Enum mit natürlichen Schlüssel.
 * 
 */
public enum Vermerkstyp {
    /** . */
    NACHRICHT_EMPFANGEN("TYP001"),
    /** . */
    NACHRICHT_GESENDET("TYP002"),
    /** . */
    VERARBEITUNG_GESTARTET("TYP003");

    /**
     * Der Schlüssel.
     */
    private final String id;

    private Vermerkstyp(String id) {
        this.id = id;
    }

    /**
     * Liefert den Schlüssel.
     * 
     * @return der Schlüssel
     */
    @EnumId
    public String getId() {
        return id;
    }

}
