package de.bund.bva.isyfact.logging;

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

import de.bund.bva.pliscommon.exception.PlisException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;

/**
 * Spezifisches Logger-Interface gemäß des Bausteins IsyFact-Logging. Diese Klasse wird zum Erstellen von
 * Logeinträgen verwendet.
 * 
 */
public interface IsyLogger {

    /**
     * Erstellung eines Logeintrags im Level 'Trace'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void trace(String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, im Level 'Trace'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void traceFachdaten(String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Debug'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void debug(String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, im Level 'Debug'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void debugFachdaten(String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void info(LogKategorie kategorie, String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, im Level 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void infoFachdaten(LogKategorie kategorie, String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void info(LogKategorie kategorie, String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void infoFachdaten(LogKategorie kategorie, String nachricht, PlisException exception,
            Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void info(LogKategorie kategorie, String nachricht, PlisTechnicalRuntimeException exception,
            Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void info(LogKategorie kategorie, String schluessel, String nachricht, Throwable t,
            Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void infoFachdaten(LogKategorie kategorie, String nachricht,
            PlisTechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void infoFachdaten(LogKategorie kategorie, String schluessel, String nachricht, Throwable t,
            Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Warn'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void warn(String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Warn'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void warn(String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Warn'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void warnFachdaten(String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Warn'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void warn(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Warn'.
     * 
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void warn(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Warn'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void warnFachdaten(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Warn'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void warnFachdaten(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Warn'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void warnFachdaten(String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void error(String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Error'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void errorFachdaten(String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void error(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void error(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void error(String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Error'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void errorFachdaten(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Error'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void errorFachdaten(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Error'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void errorFachdaten(String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void fatal(String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Fatal'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void fatalFachdaten(String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void fatal(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void fatal(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void fatal(String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Fatal'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void fatalFachdaten(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Fatal'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void fatalFachdaten(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Fatal'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void fatalFachdaten(String schluessel, String nachricht, Object... werte);
    
    /**
     * Prüft, ob das Log-Level 'Trace' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isTraceEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Debug' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isDebugEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Info' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isInfoEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Warn' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isWarnEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Error' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isErrorEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Fatal' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isFatalEnabled();

}
