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
package de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.konstanten;

/**
 * This class contains constants for the resources of the respective transformation table.
 *
 */
public class TransformationsKonstanten {

    /** The string that describes the characters to remove */
    public static final String ZEICHEN_ENTFERNE = "";

    /** The entry in a transformation table that describes the standard case */
    public static final String EINTRAG_STANDARD = "standard";

    /** String representing the space */
    public static final String STRING_SPACE = " ";

    /** The path to the transformation table identical */
    public static final String TRANSFORMATIONS_TABELLE_IDENTISCH =
        "/resources/tabellen/transformation_identisch.transform";

    /** The path to the table identical category */
    public static final String KATEGORIE_TABELLE_IDENTISCH = "/resources/tabellen/kategorie_identisch.kat";

}
