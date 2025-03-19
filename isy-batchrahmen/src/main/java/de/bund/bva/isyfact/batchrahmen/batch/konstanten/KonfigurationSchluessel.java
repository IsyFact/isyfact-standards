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
package de.bund.bva.isyfact.batchrahmen.batch.konstanten;

/**
 * Enthält alle Schlüssel für Konfigurationsparameter.
 *
 *
 */
public abstract class KonfigurationSchluessel {

    /***************************************************************************
     * PROPERTY - PARAMETER *
     **************************************************************************/

    /** Property-Name fuer die Batchrahmen-Bean. */
    public static final String PROPERTY_BATCHRAHMEN_BEAN_NAME = "Batchrahmen.BeanName";

    /** Property-Name fuer die Logback-Kontiguration. */
    public static final String PROPERTY_BATCHRAHMEN_LOGBACK_CONF = "Batchrahmen.LogbackConfigFile";

    /** Property-Name fuer das Commit-Intervall. */
    public static final String PROPERTY_BATCHRAHMEN_COMMIT_INTERVALL = "Batchrahmen.CommitIntervall";

    /** Property-Name fuer das Commit-Intervall. */
    public static final String PROPERTY_BATCHRAHMEN_ZU_VERARBEITENDE_DATENSAETZE_ANZAHL =
        "Batchrahmen.AnzahlZuVerarbeitendeDatensaetze";

    /** Property whether the token should be renewed on each batch step. */
    public static final String PROPERTY_BATCHRAHMEN_TOKEN_ERNEUERUNG =
        "Batchrahmen.TokenErneuerung";

    /**
     * Property-Name fuer die Ergebnisdatei.
     */
    public static final String PROPERTY_BATCHRAHMEN_ERGEBNIS_DATEI = "Batchrahmen.Ergebnisdatei";

    /**
     * Property-Name fuer das Clear-Intervall (clearen des Hibernate Sssion-Caches).
     */
    public static final String PROPERTY_BATCHRAHMEN_CLEAR_INTERVALL = "Batchrahmen.ClearIntervall";

    /** Property-Name fuer den Namen der Ausfuehrungs-Bean. */
    public static final String PROPERTY_AUSFUEHRUNGSBEAN = "AusfuehrungsBean";

    /** Property-Name fuer die ID des Batches. */
    public static final String PROPERTY_BATCH_ID = "BatchId";

    /** Property-Name fuer den Namen des Batches. */
    public static final String PROPERTY_BATCH_NAME = "BatchName";

    /***************************************************************************
     * KOMMANDOZEILEN - PARAMETER *
     **************************************************************************/

    /** Kommandozeilen-Parameter fuer die Property-Datei. */
    public static final String KOMMANDO_PARAM_PROP_DATEI = "cfg";

    /** Kommandozeilen-Parameter fuer die Start-Art des Batches: Start. */
    public static final String KOMMANDO_PARAM_START = "start";

    /** Kommandozeilen-Parameter fuer die Start-Art des Batches: Restart. */
    public static final String KOMMANDO_PARAM_RESTART = "restart";

    /**
     * Kommandozeilen-Parameter dafuer, dass auch bei Abbruch neu gestartet wird.
     */
    public static final String KOMMANDO_PARAM_IGNORIERE_RESTART = "ignoriereRestart";

    /** Kommandozeilen-Parameter dafuer, dass auch bei Lauf neu gestartet wird. */
    public static final String KOMMANDO_PARAM_IGNORIERE_LAUF = "ignoriereLauf";

    /** Kommandozeilen-Parameter fuer die maximale Laufzeit des Batches (optional). */
    public static final String KOMMANDO_PARAM_LAUFZEIT = "laufzeit";

}
