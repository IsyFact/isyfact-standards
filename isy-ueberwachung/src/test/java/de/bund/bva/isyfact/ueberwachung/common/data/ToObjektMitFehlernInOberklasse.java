package de.bund.bva.isyfact.ueberwachung.common.data;

public class ToObjektMitFehlernInOberklasse extends AbstractToObjektMitFehlernInOberklasse {

    private int x = 0;

    // Für den Test mit Vererbung & Objekthierarchie
    private ToObjektMitFehlernInOberklasse toObjektMitFehlernInOberklasse;

    public ToObjektMitFehlernInOberklasse getToObjektMitFehlernInOberklasse() {
        return this.toObjektMitFehlernInOberklasse;
    }

    public void setToObjektMitFehlernInOberklasse(
        ToObjektMitFehlernInOberklasse toObjektMitFehlernInOberklasse) {
        this.toObjektMitFehlernInOberklasse = toObjektMitFehlernInOberklasse;
    }

}
