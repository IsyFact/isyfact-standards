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
package de.bund.bva.pliscommon.batchrahmen.batch.rahmen;

import java.util.Date;

import de.bund.bva.pliscommon.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.pliscommon.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.StatistikEintrag;

/**
 * Interface fuer die Beans, welche in den Batchrahmen gehaengt werden.
 * <p>
 * Diese Beans enthalten die konkrete Batchlogik:
 * <ul>
 * <li>Sie führen das Lesen von Datensaetzen sowie ihre Verarbeitung durch.
 * <li>Sie lesen im Fall von Restarts vor.
 * <li>Sie protokollieren den Batchablauf über die Methoden
 * {@link #initialisieren(BatchKonfiguration, long, String, BatchStartTyp , Date , BatchErgebnisProtokoll )},
 * {@link #checkpointGeschrieben(long)} und {@link #batchBeendet()}.
 * </ul>
 * Durch den Batchrahmen wird das Transaktionshandling, das Handling der Status-Tabelle, das Initialisieren
 * der Spring-Kontexte ue bernommen.
 *
 */
public interface BatchAusfuehrungsBean {

    /**
     * Initialisiert die Ausfuehrungsklasse: Oeffnet die benoetigten Dateien oder Datenbank-Cursors, liest
     * beim Restart-Verfahren bis zum angegebenen Satz bzw. dem angegebenen Datenbankschluessel vor.
     *
     * @param konfiguration
     *            Die Konfiguration des Batches. Welche Properties fuer die Klasse relevant sind, ist nicht
     *            vorgegeben. Die Properties enthalten sowohl Informationen der Konfigurations- Property-Datei
     *            als auch (ueberschreibend) die angegebenen Kommandozeilen-Parameter.
     *            <p>
     *            Bei einem Restart werden die Daten des letzten Laufs uebermittelt.
     * @param satzNummer
     *            die Satznummer, bis zu der bei einem Restart vorgelesen werden soll. Dieser Schluessel kann
     *            ignoriert werden, falls der Parameter dbKey zum Restart verwendet wird.
     * @param dbKey
     *            der Datenbankschluessel, ab dem fuer einen Restart Werte ausgelesen werden sollen. Dieser
     *            Schluessel kann ignoriert werden, falls der Parameter satzNummer zum Restart verwendet wird.
     * @param startTyp
     *            Der Starttyp (Start bzw. Restart).
     * @param datumLetzterErfolg
     *            Datum an dem der Batch das letzte Mal erfolgreich durchgelaufen ist.
     * @param protokoll
     *            Ergebnis Protokoll des Batches. Wï¿½hrend der Initialisierung sollten die benï¿½tigten
     *            {@link StatistikEintrag} initialisiert werden.
     *
     * @throws BatchAusfuehrungsException
     *             Falls ein Fehler auftritt.
     *
     * @return falls bekannt: Die Gesamtanzahl an Datensaetzen. Sonst: -1.
     */
    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
        BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
        throws BatchAusfuehrungsException;

    /**
     * verarbeite den naechsten Satz.
     *
     * @throws BatchAusfuehrungsException
     *             Falls ein Fehler auftritt.
     *
     * @return Das Verarbeitungsergebnis des Satzes. Es besteht aus: -Dem Datenbankschluessel des
     *         verarbeiteten Datensatzes fuer ein spaeteres Wiederaufsetzen des Batches. Null, falls kein
     *         Wiederaufsetzen ueber Datenbankschluessel unterstuetzt wird. -Einem Kennzeichen dafuer, dass
     *         weitere Datensaetze zu verarbeiten sind.
     */
    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException;

    /**
     * Diese Methode wird im Kontext einer Transaktion aufgerufen. Sie wird aufgerufen, wenn der Batch beendet
     * wird. Dies kann protokolliert werden, Dateien kï¿½nnen geschlossen, Ressourcen freigegeben werden.
     * <p>
     * Fehler bei Freigaben sollten abgefangen und nicht weitergeworfen werden, damit sich der Batch
     * erfolgreich beendet.
     */
    public void batchBeendet();

    /**
     * Ein Checkpunkt wurde geschrieben. Dies bedeutet, dass die letzte Transaktion beendet und eine neue
     * gestartet wurde. Der Batch kann dies protokollieren und ggf. Objekte neu laden.
     *
     * @param satzNummer
     *            die in den BatchStatus geschriebene Satznummer.
     *
     * @throws BatchAusfuehrungsException
     *             Falls ein Fehler auftritt.
     */
    public void checkpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException;

    /**
     * Diese Methode wird unmittelbar vor dem Schreiben eines Checkpoints (Commit) aufgerufen.
     *
     * @param satzNummer
     *            die Nummer des zuletzt verarbeiteten Satzes.
     *
     * @throws BatchAusfuehrungsException
     *             Falls ein Fehler auftritt.
     */
    public void vorCheckpointGeschrieben(long satzNummer) throws BatchAusfuehrungsException;

    /**
     * Diese Methode wird aufgerufen, wenn ein Rollback durchgeführt wurde. Der Batch kann somit z.B.
     * notwendige Aufräumarbeiten durchführen.
     */
    public void rollbackDurchgefuehrt();

    /**
     * Diese Methode wird aufgerufen, unmittelbar bevor ein Rollback durchgeführt wird. Der Batch kann somit
     * z.B. notwendige Aufräumarbeiten durchführen.
     */
    public void vorRollbackDurchgefuehrt();

    /**
     * Diese Methode wird vor der Initialisierung (
     * {@link #initialisieren(BatchKonfiguration, long, String, BatchStartTyp, Date, BatchErgebnisProtokoll)})
     * aufgerufen. Es wird erwartet, dass das {@link BatchAusfuehrungsBean} sich authentifiziert, indem es ein
     * {@link AuthenticationCredentials} Objekt mit den erforderlichen Daten zurück gibt. Ist dies geschehen,
     * werden die angegebenen Daten im Aufrufkontext hinterlegt, um eine Berechtigungsprüfung zu ermöglichen.
     * @param konfiguration
     *            die Batchkonfiguration, aus der bei bedarf Informationen gelesen werden können.
     * @return Informationen zur Authentifizierung des Batches oder <code>null</code> falls keine
     *         Berechtigungsprüfung benötigt wird.
     */
    public AuthenticationCredentials getAuthenticationCredentials(BatchKonfiguration konfiguration);
}
