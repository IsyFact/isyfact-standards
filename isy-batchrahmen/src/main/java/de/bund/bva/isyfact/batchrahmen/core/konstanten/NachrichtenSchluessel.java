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
package de.bund.bva.isyfact.batchrahmen.core.konstanten;

/**
 * Enthï¿½lt Konstanten für Nachrichten des Batchrahmens.
 * 
 * 
 */
public abstract class NachrichtenSchluessel {

    // **** Fehler in der Konfiguration ****
    /** Parameter fehlt. */
    public static final String ERR_KONF_PARAMETER_FEHLT = "BAT100";

    /** Parameter ungültig. */
    public static final String ERR_KONF_PARAMETER_UNGUELTIG = "BAT110";

    /** Konfigurationsdatei konnte nicht gelesen werden. */
    public static final String ERR_KONF_DATEI_LESEN = "BAT120";

    /** Batch-Bean muss konfiguriert werden. */
    public static final String ERR_KONF_BEAN_PFLICHT = "BAT130";

    // **** Fehler in den Kommandozeilenparametern ****
    /** Kommandozeilenparameter ungültig. */
    public static final String ERR_KOMMANDO_PARAMETER_UNGUELTIG = "BAT200";

    /** Konfigurationsdatei nicht angegeben. */
    public static final String ERR_KOMMANDO_PARAMETER_KEINE_CONFIG = "BAT210";

    /** Zwei ausschlieï¿½ende Parameter wurden angegeben. */
    public static final String ERR_KOMMANDO_PARAMETER_KONFLIKT = "BAT220";

    /** Von zwei Parametern muss einer gesetzt sein. */
    public static final String ERR_KOMMANDO_PARAMETER_NOETIG = "BAT230";

    /** Parameter muss mit - beginnen. */
    public static final String ERR_KOMMANDO_PARAMETER_PRAEFIX = "BAT240";

    /** Fuer den Kommandozeilenparameter {0} ist kein Wert angegeben. */
    public static final String ERR_KOMMANDO_PARAMETER_WERT_NOETIG = "BAT250";

    // **** Fehler bei der Initialisierung ****
    /** Statuseintrag schon in DB. */
    public static final String ERR_BATCH_IN_DB = "BAT300";

    /** Letzter Lauf ist abgebrochen. */
    public static final String ERR_IGNORIERE_RESTART = "BAT320";

    /** Batch läuft schon. */
    public static final String ERR_BATCH_AKTIV = "BAT310";

    /** Restart nicht möglich. */
    public static final String ERR_BATCH_INIT_ABGEBR = "BAT330";

    // **** Fehler bei der Ausführung ****
    /** Nicht alle Datensätze wurden verarbeitet. */
    public static final String ERR_BATCH_UNVOLLSTAENDIG = "BAT400";

    /** Fehler bei der Verarbeitung des Ergebnis-Protokolls. */
    public static final String ERR_BATCH_PROTOKOLL = "BAT410";

    // **** Nachrichten fuer ReturnCodes ****
    /** OK. */
    public static final String MSG_RC_OK = "RC_OK";

    /** Ausgeführt mit Fehlern. */
    public static final String MSG_RC_FEHLER_AUSGEFUEHRT = "RC_FEHLER_AUSGEFUEHRT";

    /** Abbruch. */
    public static final String MSG_RC_FEHLER_ABBRUCH = "RC_FEHLER_ABBRUCH";

    /** Fehler in den Aufrufparametern. */
    public static final String MSG_RC_FEHLER_PARAMETER = "RC_FEHLER_PARAMETER";

    /** Fehler in der Konfiguration. */
    public static final String MSG_RC_FEHLER_KONFIGURATION = "RC_FEHLER_KONFIGURATION";

    /** Fehler in der Konfiguration. */
    public static final String MSG_RC_FEHLER_MANUELLER_ABBRUCH = "RC_FEHLER_MANUELLER_ABBRUCH";

    /** Abbruch wegen Laufzeitüberchreitung.. */
    public static final String MSG_RC_FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN =
        "RC_FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN";

}
