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
package de.bund.bva.pliscommon.persistence.exception;

/**
 * Fehlerschlüssel für plis-persistence.
 * 
 */
public final class FehlerSchluessel {

    /**
     * Fehlerhafte Konfiguration der Enum-Persistierung: {0}.
     */
    public static final String FALSCHE_ENUM_KONFIGURATION = "PERSI00001";

    /**
     * Unbekannter Stringwert {0} für den Enumtyp {1}.
     */
    public static final String UNBEKANNTER_STRING = "PERSI00002";

    /**
     * Unbekannte Ausprägung {0} für den Enumtyp {1}.
     */
    public static final String UNBEKANNTE_AUSPRAEGUNG = "PERSI00003";

    /**
     * Keine Datenbank-Connection verfügbar.
     */
    public static final String KEINE_DB_CONNECTION_VERFUEGBAR = "PERSI00004";

    /**
     * Keine valide Datenbank-Connection verfügbar.
     */
    public static final String KEINE_VALIDE_DB_CONNECTION_VERFUEGBAR = "PERSI00007";

    /**
     * Die Version des Datenbankschemas entspricht nicht der erwarteten Version ({0}).
     */
    public static final String FALSCHE_DB_SCHEMAVERSION = "PERSI00005";

    /**
     * Beim Prüfen der Version des Datenbankschemas ist ein Fehler aufgetreten.
     */
    public static final String PRUEFEN_DER_SCHEMAVERSION_FEHLGESCHLAGEN = "PERSI00006";

}
