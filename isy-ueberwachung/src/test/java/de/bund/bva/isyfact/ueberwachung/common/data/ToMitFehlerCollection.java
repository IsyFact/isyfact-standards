package de.bund.bva.isyfact.ueberwachung.common.data;

import java.util.ArrayList;
import java.util.List;

public class ToMitFehlerCollection {

    private List<Fehler> fehlerliste = new ArrayList<Fehler>();

    public List<Fehler> getFehlerliste() {
        return this.fehlerliste;
    }

    public void setFehlerliste(List<Fehler> fehler) {
        this.fehlerliste = fehler;
    }

}
