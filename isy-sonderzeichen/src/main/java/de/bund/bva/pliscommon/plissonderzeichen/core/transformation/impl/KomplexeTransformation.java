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
package de.bund.bva.pliscommon.plissonderzeichen.core.transformation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bund.bva.pliscommon.plissonderzeichen.core.transformation.Transformator;

/**
 * Diese Klasse bildet Transformationen ab, bei denen mehr als ein Zeichen transformiert wird oder bei denen
 * besondere Bedingungen erfüllt sein müssen.
 * 
 */
public class KomplexeTransformation {

    /**
     * Diese Klasse beschreibt eine mögliche Ersetzung mit den dafür eventuell notwendigen Regeln.
     */
    private class Ersetzung {

        /**
         * Die Nummern der Regeln, bei deren Erfüllung die Ersetzung angewendet werden kann.
         * 
         * Wenn mehrere Regeln angegeben sind, so wird die Ersetzung verwendet, wenn mindestens eine davon
         * erfüllt ist. Ist keine Regel angegeben, so kann die Ersetzung immer verwendet werden.
         */
        public int[] regeln = new int[0];

        /** Der String, durch den die ursprünglichen Zeichen ersetzt werden. */
        public String ersatz;
    }

    /** Der Transformator, dem die Transformation zugeordnet ist. */
    private Transformator transformator;

    /** Enthält alle möglichen Ersetzung, die mit einem bestimmten Zeichen beginnen. */
    private Map ersetzungen = new HashMap();

    /** Die Länge der längsten zu ersetzenden Zeichenkombination. */
    private int maxKeyLaenge = 0;

    /** Anzahl Zeichen, die bei der letzten ermittelten Zeichenersetzung ersetzt worden wären. */
    private int laengeLetzteErsetzung = 0;

    public KomplexeTransformation(Transformator transformator) {
        this.transformator = transformator;
    }

    /**
     * Fügt der Liste der Ersetzungen eine neue Ersetzung ohne Regeln hinzu.
     * 
     * @param ersetzenVon
     *            Die zu ersetzende Zeichenkette.
     * @param ersetzenNach
     *            Zeichenkette, durch die ersetzt wird.
     */
    public void addErsetzung(String ersetzenVon, String ersetzenNach) {
        Ersetzung ersetzung = new Ersetzung();
        ersetzung.ersatz = ersetzenNach;
        addErsetzung(ersetzenVon, ersetzung);
    }

    /**
     * Fügt der Liste der Ersetzungen eine neue Ersetzung mit Regeln hinzu.
     * 
     * @param ersetzenVon
     *            Die zu ersetzende Zeichenkette.
     * @param ersetzenNach
     *            Zeichenkette, durch die ersetzt wird.
     * @param regeln
     *            Liste der Regeln, die für die neue Ersetzung zu prüfen sind.
     */
    public void addErsetzung(String ersetzenVon, String ersetzenNach, String[] regeln) {
        Ersetzung ersetzung = new Ersetzung();
        ersetzung.ersatz = ersetzenNach;
        ersetzung.regeln = new int[regeln.length];
        for (int i = 0; i < regeln.length; i++) {
            ersetzung.regeln[i] = Integer.parseInt(regeln[i]);
        }
        addErsetzung(ersetzenVon, ersetzung);
    }

    /**
     * Liefert eine Zeichenersetzung für eine Position innerhalb einer Zeichenkette.
     * 
     * Es wird immer die Zeichenersetzung ermittelt, durch die möglichst viele Zeichen des Ausgangsstrings
     * ersetzt werden.
     * 
     * @param text
     *            Zeichenkette, in der Zeichen ersetzt werden sollen.
     * @param position
     *            Position innerhalb der Zeichenkette, an der Zeichen ersetzt werden sollen.
     * @return String, durch den die Zeichen an der angegebenen Position zu ersetzen sind.
     */
    public String getErsetzung(String text, int position) {
        for (int laenge = maxKeyLaenge; laenge > 0; laenge--) {
            if (position + laenge > text.length()) {
                continue;
            }
            String sub = text.substring(position, position + laenge);
            List varianten = (List) ersetzungen.get(sub);
            if (varianten == null) {
                continue;
            }
            for (int i = 0; i < varianten.size(); i++) {
                Ersetzung ersetzung = (Ersetzung) varianten.get(i);
                if (ersetzung.regeln.length > 0) {
                    for (int regel = 0; regel < ersetzung.regeln.length; regel++) {
                        if (transformator.werteRegelAus(ersetzung.regeln[regel], text, position, laenge)) {
                            laengeLetzteErsetzung = laenge;
                            return ersetzung.ersatz;
                        }
                    }
                } else {
                    laengeLetzteErsetzung = laenge;
                    return ersetzung.ersatz;
                }
            }
        }
        laengeLetzteErsetzung = 1;
        return "";
    }

    /**
     * Gibt die Anzahl von Zeichen zurück, die durch die letzte ermittelte Ersetzung ersetzt worden wären.
     * 
     * @return Anzahl ersetzter Zeichen.
     */
    public int getLaengeLetzteErsetzung() {
        return laengeLetzteErsetzung;
    }

    private void addErsetzung(String ersetzenVon, Ersetzung ersetzung) {
        List varianten = (List) ersetzungen.get(ersetzenVon);
        if (varianten == null) {
            varianten = new ArrayList();
            ersetzungen.put(ersetzenVon, varianten);
            if (maxKeyLaenge < ersetzenVon.length()) {
                maxKeyLaenge = ersetzenVon.length();
            }
        }
        varianten.add(ersetzung);
    }

}
