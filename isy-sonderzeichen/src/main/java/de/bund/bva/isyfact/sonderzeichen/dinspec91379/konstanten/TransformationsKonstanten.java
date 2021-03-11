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
package de.bund.bva.isyfact.sonderzeichen.dinspec91379.konstanten;

/**
 * Diese Klasse enthält Konstanten zu den Ressourcen der jeweiligen Transformations Tabelle.
 *
 */
public class TransformationsKonstanten {

    /** Die Zeichenkette, welche die Zeichen für das Entfernen beschreibt */
    public static final String ZEICHEN_ENTFERNE = "";

    /** Der Eintrag in einer Transformationstabelle, welcher den Standardfall beschreibt */
    public static final String EINTRAG_STANDARD = "standard";

    /** String, welches das Leerzeichen darstellt */
    public static final String STRING_SPACE = " ";

    /** Der Pfad zur Transformations Tabelle Identisch */
    public static final String TRANSFORMATIONS_TABELLE_IDENTISCH =
        "/resources/tabellen/transformation_dinspec91379.transform";

    /** Der Pfad zur Kategorie Tabelle Identisch */
    public static final String KATEGORIE_TABELLE = "/resources/tabellen/kategorie_dinspec91379.kat";

    public static final String TRANSFORMATIONS_TABELLE_TRANSCRIPTION = "/resources/tabellen/transformation_din91379_transkription.transform";
}
