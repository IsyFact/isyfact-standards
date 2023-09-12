package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung;

import static de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung.Zeichengruppe.E1;
import static de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung.Zeichengruppe.E_GRIECH;
import static de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung.Zeichengruppe.E_KYRILL;
import static de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung.Zeichengruppe.N1;
import static de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung.Zeichengruppe.N2;
import static de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung.Zeichengruppe.N3;
import static de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung.Zeichengruppe.N4;
import static de.bund.bva.isyfact.sonderzeichen.dinnorm91379.validierung.Zeichengruppe.N_LATEIN;

/**
 * Class representing a data type as defined by DIN Norm 91379.
 */
public enum Datentyp {

    /**
     * Datentyp A: für Namen natürlicher Personen.
     * <p>
     * Data Type A: for names of natural persons.
     */
    A(N_LATEIN, N1),
    /**
     * Datentyp B: für sonstige Namen, wie z.B. Straßennamen mit Hausnummer.
     * <p>
     * Data Type B: for miscellaneous names, like street names with house number.
     */
    B(N_LATEIN, N1, N2),
    /**
     * Datentyp C: für alle normative Schriftzeichen.
     * <p>
     * Data Type C: for all normative characters.
     */
    C(N_LATEIN, N1, N2, N3, N4),
    /**
     * Datentyp D: für Namen juristischer Personen.
     * <p>
     * Data Type D: for names of legal persons.
     */
    D(N_LATEIN, N1, N2, N3, E1, E_GRIECH),
    /**
     * Datentyp E: für alle normativen und erweiterten Schriftzeichen.
     * <p>
     * Data Type E: for all normative and extended characters.
     */
    E(N_LATEIN, N1, N2, N3, N4, E1, E_GRIECH, E_KYRILL);

    /**
     * The character groups making up this data type.
     */
    private final Zeichengruppe[] zeichengruppen;

    Datentyp(Zeichengruppe... zeichengruppen) {
        this.zeichengruppen = zeichengruppen;
    }

    /**
     * Returns the character groups that make up this data type.
     *
     * @return array containing the character groups
     */
    public Zeichengruppe[] getZeichengruppen() {
        return zeichengruppen;
    }

}
