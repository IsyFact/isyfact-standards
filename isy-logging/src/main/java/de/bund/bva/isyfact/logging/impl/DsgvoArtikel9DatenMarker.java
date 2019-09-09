package de.bund.bva.isyfact.logging.impl;

public class DsgvoArtikel9DatenMarker extends AbstractIsyDatentypMarker {

    /**
     * Ein Marker für Daten gem. Artikel 9 DSGVO
     */
    public static final DsgvoArtikel9DatenMarker INSTANZ = new DsgvoArtikel9DatenMarker();

    private DsgvoArtikel9DatenMarker() {
        super("Daten gem. Artikel 9 DSGVO");
    }
}
