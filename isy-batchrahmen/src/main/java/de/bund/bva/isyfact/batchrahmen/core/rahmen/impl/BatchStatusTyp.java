/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.batchrahmen.core.rahmen.impl;

import java.util.HashMap;

/**
 * Liste der moeglichen Status fuer einen Batch.
 * 
 *
 */
public enum BatchStatusTyp {
    /**
     * Der Status NEU.
     */
    NEU("neu"),
    /**
     * Der Status LAUEFT.
     */
    LAEUFT("laeuft"),

    /**
     * Der Status ABGEBROCHEN.
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
        codeMap = new HashMap<String, BatchStatusTyp>();
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
        return (BatchStatusTyp) codeMap.get(name);
    }

}
