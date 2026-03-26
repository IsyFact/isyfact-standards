package de.bund.bva.isyfact.logging.impl;

import java.io.Serial;

public class DsgvoArtikel9DatenMarker extends AbstractIsyDatentypMarker {

    @Serial
    private static final long serialVersionUID = -2303119245845331141L;

    /**
     * A marker for data pursuant to Article 9 of the GDPR (DSGVO)
     */
    public static final DsgvoArtikel9DatenMarker INSTANZ = new DsgvoArtikel9DatenMarker();

    private DsgvoArtikel9DatenMarker() {
        super("Daten gem. Artikel 9 DSGVO");
    }
}
