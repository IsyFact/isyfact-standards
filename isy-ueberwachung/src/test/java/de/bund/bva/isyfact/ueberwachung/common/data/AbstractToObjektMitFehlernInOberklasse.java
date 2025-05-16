package de.bund.bva.isyfact.ueberwachung.common.data;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractToObjektMitFehlernInOberklasse {

    private List<Fehler> fehlerliste = new ArrayList<Fehler>();

    private Fehler fehler;

    public List<Fehler> getFehlerliste() {
        return this.fehlerliste;
    }

    public void setFehlerliste(List<Fehler> fehler) {
        this.fehlerliste = fehler;
    }

    public Fehler getFehler() {
        return this.fehler;
    }

    public void setFehler(Fehler fehler) {
        this.fehler = fehler;
    }

}
