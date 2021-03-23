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
package de.bund.bva.isyfact.sonderzeichen.dinspec91379.core.transformation;

/**
 * Schnittstelle für alle möglichen Transformatoren. Definiert Methoden, welche jeder Transformator anbieten
 * kann.
 * 
 */
public interface Transformator {

    /**
     * Transformiert eine Zeichenkette. Stellt sicher, dass die Zeichenkette nach der Operation die angegebene
     * Länge hat. Es wird dabei nicht unterschieden, ob die ursprüngliche Zeichenkettenlänge bereits das
     * Maximum überschritten hat oder erst durch eine Transformation die Zeichenkette verlängert wurde.<br>
     * Liefert <code>null</code> zurück, falls die Zeichenkette als <code>null</code> übergeben wurde.
     * 
     * Wichtig! Es gelten die gleichen Einschränkungen für non-BMP characters wir für
     * {@link #transformiere(String)}
     * 
     * @param zeichenkette
     *            Die zu transformierende Zeichenkette
     * @param maximaleLaenge
     *            Die maximale Länge der Zeichenkette
     * @return die transformierte Zeichenkette, oder null, falls null übergeben wurde
     */
    String transformiere(String zeichenkette, int maximaleLaenge);

    /**
     * Transformiert eine Zeichenkette und gibt sie zurück. Leerzeichen am Anfang und am Ende der Zeichenkette
     * werden abgeschnitten. Doppelte Leerzeichen innerhalb der Zeichenkette werden zu einem Leerzeichen
     * umgewandelt.<br>
     * Liefert <code>null</code> zurück, falls die Zeichenkette als <code>null</code> übergeben wurde.
     * 
     * Wichtig! Die Transformationsfunktion arbeitet die Zeichenkette char für char ab. Sollte ein
     * Unicode-Character, welcher aus mehreren char Objekten besteht definiert sein (non-BMP character, z.B. I
     * mit angehängtem Circumflex (\\u006C\\u0302), so liefert die Tranformationsfunktion das korrekte
     * Ergebnis, kann aber nicht zwischen SL und NON-SL Zeichen unterscheiden. So könnten Zeichen außerhalb
     * des Definitionsbereichs (z.B. alle \\u####\\u0302) der Transformation transformiert werden.<br>
     * Zur Überprüfung ob eine Zeichenkette innerhalb des für den Transformator gültigen Bereichs liegt,
     * sollte daher die Funktion {@link #isGueltigerString(String, String[])} benutzt werden.
     * @param zeichenkette
     *            Die zu transformierende Zeichenkette
     * @return die transformierte Zeichenkette, oder null, falls null übergeben wurde
     */
    String transformiere(String zeichenkette);

    /**
     * Transformiert eine Zeichenkette und gibt sie zurück. Führt keine Trim Operationen am Ende aus.
     * 
     * Wichtig! Es gelten die gleichen Einschränkungen für non-BMP characters wir für
     * {@link #transformiere(String)}
     * 
     * @param zeichenkette
     *            Die zu transformierende Zeichenkette
     * @return die transformierte Zeichenkette, oder null, falls null übergeben wurde
     */
    String transformiereOhneTrim(String zeichenkette);

    /**
     * Gibt den regulären Ausdruck für alle erlaubten Ausdrücke innerhalb der Zeichenkategorie zurück.
     * @param kategorieListe
     *            die erlaubten Ausdrücke der Zeichenkategorie
     * @return der reguläre Ausdruck
     */
    String getRegulaererAusdruck(String[] kategorieListe);

    /**
     * Überprüft ob die übergebene Zeichenkette nur Zeichen enthält, welche in der jeweiligen Kategorie des
     * Zielzeichensatzes des Transformators existiert. Der Zielzeichensatz besteht aus der Menge der Zeichen
     * auf die abgebildet werden kann.
     * @param zeichenkette
     *            die zu überprüfende Zeichenkette
     * @param kategorieListe
     *            die Liste der Kategorien
     * @return true, wenn die übergebene Zeichenkette nur Zeichen der jeweiligen Kategorie enthält
     */
    boolean isGueltigerString(String zeichenkette, String[] kategorieListe);

    /**
     * Gibt alle gueltigen Zeichen des Transformators zurueck.
     * @param kategorie
     *            die Kategorie
     * @return alle gültuigen Zeichen des Transformators
     */
    String[] getGueltigeZeichen(String kategorie);

    /**
     * Prüft mit Hilfe einer Regel, ob ein zu transliterierender Substring eine bestimmte Bedingung erfüllt.
     * 
     * @param regel
     *            Nummer der auszuwertenden Regel.
     * @param text
     *            String, aus dem der zu prüfende Substring stammt.
     * @param position
     *            Position des Substrings im Text.
     * @param laenge
     *            Länge des Substrings.
     * @return true, wenn die Bedingung erfüllt ist, sonst false.
     */
    boolean werteRegelAus(int regel, String text, int position, int laenge);

}
