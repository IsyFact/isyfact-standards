package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;

import java.util.HashMap;

/**
 * Liste der moeglichen Status fuer einen Batch.
 * 
 *
 */
public enum BatchStatusTyp {
    /**
     *  Der Status NEU. 
     */
    NEU("neu"),
    /**
     *  Der Status LAUEFT. 
     */
    LAEUFT("laeuft"),

    /**
     *  Der Status ABGEBROCHEN. 
     */
    ABGEBROCHEN("abgebrochen"),

    /**
     * Der Status BEENDET.
     */
    BEENDET("beendet");

    /**
     * Diese HashMap speichert den Zusammenhang zwischen Status-Namen und dazugehörigem Status.
     */
    private static HashMap<String, BatchStatusTyp> codeMap;

    /**
     * Hier wird der Name des Status gespeichert.
     */
    private final String name;

    /**
     * Konstruktor für ein Status mit Namen.
     * 
     * @param name
     *            Name den das Status haben soll
     */
    private BatchStatusTyp(String name) {
        this.name = name;
    }

    /**
     * String-Repräsentation des Status.
     * 
     * @return Der Name des Status
     */
    public String getName() {
        return name;
    }

    /**
     * Hiermit wird die Hashmap mit den Statusnamen und den zugehörigen Status gefüllt.
     */
    static {
        BatchStatusTyp[] status = BatchStatusTyp.values();
        codeMap = new HashMap<>();
        for (int i = 0; i < status.length; i++) {
            codeMap.put(status[i].name, status[i]);
        }
    }

    /**
     * Erzeugt einen Anfragestatus mit einem gesetzten Wert.
     * 
     * @param name
     *            Name des Status
     * @return Ein Anfragestatus mit dem gesetzten Wert
     */
    public static BatchStatusTyp fromCode(String name) {
        return codeMap.get(name);
    }
}
