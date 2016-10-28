package de.bund.bva.isyfact.logging.impl;

/*
 * #%L
 * isy-logging
 * %%
 * 
 * %%
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
 * #L%
 */
/**
 * Fehlerschlüssel von Isy-Logging.
 * 
 */
public final class FehlerSchluessel {

    /**
     * Privater Konstruktor der Klasse. Verhindert, dass Instanzen der Klasse angelegt werden.
     * 
     */
    private FehlerSchluessel() {
    }

    /**
     * Der Logger der bereitgestetllten SLF4J-Implementierung implementiert nicht das benötigte Inferface
     * LocationAwareLogger. Bereitgestellt wurde: {0}.
     */
    public static final String FALSCHES_LOGGING_FRAMEWORK = "ISYLO00000";

    /**
     * Beim Erstellen eines Logeintrags im Level {0} in Logger {1} wurde kein Schlüssel und keine Exception
     * übergeben.
     */
    public static final String FEHLERHAFTER_EINTRAG_KEIN_SCHLUESSEL = "ISYLO00001";

    /**
     * Beim Erstellen eines Logeintrags im Level {0} in Logger {1} wurde keine Kategorie übergeben.
     */
    public static final String FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE = "ISYLO00002";

    /**
     * Fehler beim Aufruf der Methode {0} durch den Log-Interceptor.
     */
    public static final String LOG_INTERCEPTOR_FEHLER_BEI_AUFRUF = "ISYLO01000";
    
    /**
     * Fehler bei der Serialisierung der Aufrufparameter.
     */
    public static final String FEHLER_SERIALISIERUNG_AUFRUFPARAMETER = "ISYLO01001";

}
