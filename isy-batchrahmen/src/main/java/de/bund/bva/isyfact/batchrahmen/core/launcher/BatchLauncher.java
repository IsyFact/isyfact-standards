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
package de.bund.bva.isyfact.batchrahmen.core.launcher;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenException;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenInitialisierungException;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenProtokollException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.batchrahmen.core.protokoll.DefaultBatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.sicherheit.common.exception.SicherheitTechnicalRuntimeException;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Diese Klasse startet einen Batch (siehe {@link Batchrahmen} mit der ï¿½bergebenen Konfiguration. Die
 * Konfiguration erfolgt über Kommandozeilen-Argumente sowie über eine Property-Datei.
 * <p>
 * Die Verarbeitungs-Logik ist dabei auf einen Batchrahmen und eine Ausfuehrungsbean aufgeteilt. Siehe dazu
 * das Detailkonzept Batch der Migrationsstufe 1.
 * <p>
 * Kommandozeilenargumente müssen stets die Form <tt>-ParameterName ParameterWert</tt> oder
 * <tt>-ParameterName</tt> besitzen. Folgende Parameter sind fuer den Batchrahmen relevant:
 * <ul>
 * <li>cfg &lt;Dateiname&gt;: Name der Property-Datei
 * <li>start: Starten des Batches im "Start" Modus.
 * <li>restart: Starten des Batches im "Restart" Modus nach einem Fehler-Abbruch.
 * <li>ignoriereRestart: Auch bei Fehlern Start akzeptieren, nicht auf Restart beharren.
 * <li>ignoriereLauf: Auch bei Status "Laeuft" Start akzeptieren.
 * </ul>
 * <p>
 * Die Property-Datei darf folgende Grüüe besitzen:
 * <ul>
 * <li>Batchrahmen.BeanName: Name der Batchrahmen-Bean.
 * <li>Anwendung.SpringDateien.&lt;N&gt;: Namen der Spring-Konfigurationsdateien des Systems.
 * <li>Batchrahmen.SpringDateien.&lt;N&gt;: Namen der Spring-Konfigurationsdateien des Batchrahmens.
 * <li>Batchrahmen.LogbackConfigFile: Pfad zur Log4J Konfigurationsdatei.
 * <li>Batchrahmen.CommitIntervall: Anzahl Satz-Verarbeitungen pro Commit.
 * <li>Batchrahmen.AnzahlZuVerarbeitendeDatensaetze: Anzahl zu verarbeitende Datensütze.
 * <li>AusfuehrungsBean: Name der Ausfuehrungsbean fuer die Batch-Logik.
 * <li>BatchId: ID des Batches (ID des Batch-Status-Datensatzes).
 * <li>BatchName: Name des Batches in der Batch-Status-Tabelle.
 * </ul>
 * <p>
 * Es künnen beliebige weitere Kommandozeilen-Parameter und Properties eingetragen werden. Die
 * Kommandozeilen-Parameter werden den Properties hinzugefügt und überschreiben sie ggf., bevor sie an die
 * Batchrahmen-Bean weitergegeben werden. Die Batchrahmen-Bean gibt die komplette Konfiguration an die
 * Ausfuehrungsbean weiter, welche sie zur Konfiguration verwenden kann.
 *
 *
 */
public class BatchLauncher {
    /** Die Konfiguration fuer den Batch-Rahmen. */
    private BatchKonfiguration rahmenKonfiguration;

    /**
     * Das Protokoll, zur Speicherung von Meldungen und Statistiken der Batch-Ausfuehrung.
     */
    private BatchErgebnisProtokoll protokoll;

    /**
     * Main-Methode zum Starten des Batches. Diese Methode ruft die Methode {@link #run(String[])} auf und
     * liefert deren ReturnCode per System.exit() zurück.
     *
     * @param args
     *            Kommandozeilen-Parameter.
     */
    public static void main(String[] args) {
        System.exit(BatchLauncher.run(args));
    }

    /**
     * Startet den Batch. Zur Konfiguration siehe Klassen-Kommentar.
     *
     * @param args
     *            Die Kommandozeilen-Argumente. Beschreibung siehe Klassen-Kommentar.
     * @return Return-Code des Batches.
     */
    public static int run(final String[] args) {
        IsyLogger log = null;
        BatchKonfiguration rahmenKonfiguration = null;
        DefaultBatchErgebnisProtokoll protokoll = null;
        String ergebnisDatei = null;
        BatchReturnCode returnCode = BatchReturnCode.FEHLER_ABBRUCH;
        try {
            rahmenKonfiguration = new BatchKonfiguration(args);
            ergebnisDatei =
                rahmenKonfiguration.getAsString(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_ERGEBNIS_DATEI,
                    null);
            initialisiereLogback(rahmenKonfiguration);
            protokoll = new DefaultBatchErgebnisProtokoll(ergebnisDatei);
            protokoll.batchStart(rahmenKonfiguration, args);

            log = IsyLoggerFactory.getLogger(BatchLauncher.class);
            log.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001, "Starte Batch.");
            new BatchLauncher(rahmenKonfiguration, protokoll).launch();
            if (protokoll.isBatchAbgebrochen()) {
                returnCode = BatchReturnCode.FEHLER_MANUELLER_ABBRUCH;
            }
            if (protokoll.isMaximaleLaufzeitUeberschritten()) {
                returnCode = BatchReturnCode.FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN;
            } else if (protokoll.enthaeltFehlerNachrichten()) {
                returnCode = BatchReturnCode.FEHLER_AUSGEFUEHRT;
            } else {
                returnCode = protokoll.getReturnCode();
                if (returnCode == null) {
                    returnCode = BatchReturnCode.OK;
                }
            }
        } catch (BatchAusfuehrungsException ex) {
            protokolliereFehler(log, protokoll, ex);
            if (ex.getReturnCode() != null) {
                returnCode = ex.getReturnCode();
            }
        } catch (BatchrahmenException ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = ex.getReturnCode();
        } catch (SicherheitTechnicalRuntimeException ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = BatchReturnCode.FEHLER_KONFIGURATION;
        } catch (Throwable ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = BatchReturnCode.FEHLER_ABBRUCH;
        } finally {
            if (protokoll != null) {
                protokoll.setReturnCode(returnCode);
                protokoll.batchEnde();
            }
        }
        System.out.print(returnCode.getWert() + ": " + returnCode.getText());
        return returnCode.getWert();
    }

    /**
     * Protokolliert einen Fehler. Wenn möglich wird dieses über den {@link IsyLogger} log durchgeführt. Auf
     * jeden Fall erfolgt eine Ausgabe auf System.Err. Zusätzlich wird im ErgebnisProtokoll protokolliert.
     * @param log
     *            Der Logger.
     * @param protokoll
     *            Das DefaultBatchErgebnisProtokoll.
     * @param ex
     *            Die Aufgetretene Exception.
     */
    private static void protokolliereFehler(IsyLogger log, BatchErgebnisProtokoll protokoll, Throwable ex) {
        String nachricht = exceptionToString(ex);
        System.err.println(nachricht);
        if (log != null) {
            log.error(BatchRahmenEreignisSchluessel.EPLBAT00001, "Fehler bei der Batchausführung.", ex);
        } else {
            ex.printStackTrace();
        }
        String ausnahmeId = "ERROR";
        if (ex instanceof BatchAusfuehrungsException) {
            ausnahmeId = ((BatchAusfuehrungsException) ex).getAusnahmeId();
        } else if (ex instanceof BatchrahmenException) {
            ausnahmeId = ((BatchrahmenException) ex).getAusnahmeId();
        }
        if (protokoll != null) {
            try {
                protokoll.ergaenzeMeldung(new VerarbeitungsMeldung(ausnahmeId, MeldungTyp.FEHLER, nachricht));
            } catch (BatchrahmenProtokollException protokollEx) {
                System.err.println("Die Fehlermeldung " + protokollEx.toString()
                    + " konnte nicht in das Ergebnisprotokoll geschrieben werden.");
            }
        }
    }

    /**
     * Konvertiert eine Exception inkl. Stacktrace in einen String.
     * @param t
     *            Exception
     * @return String inkl. Stacktrace ohne Zeilenumbrüche.
     */
    private static String exceptionToString(Throwable t) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        t.printStackTrace(new PrintStream(out));
        return out.toString().replaceAll("\\r{0,1}\\n", " | ");
    }

    /**
     * initialisiert Logback mit der Log-Konfiguration, welche in der Property-Datei mit Schluessel
     * {@link KonfigurationSchluessel#PROPERTY_BATCHRAHMEN_LOGBACK_CONF} angegeben wurde. Als default wird
     * /config/logback-batch.xml verwendet.
     *
     * @param konf
     *            die Batch-Konfiguration.
     * @throws JoranException
     *             Wenn der Logger nicht konfiguriert werden konnte.
     */
    private static void initialisiereLogback(BatchKonfiguration konf) throws JoranException {
        String propertyFile =
            konf.getAsString(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_LOGBACK_CONF,
                "/config/logback-batch.xml");
        URL configLocation = BatchLauncher.class.getResource(propertyFile);
        if (configLocation == null) {
            throw new BatchrahmenInitialisierungException(NachrichtenSchluessel.ERR_KONF_DATEI_LESEN,
                propertyFile);
        }
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset(); // override default configuration
        context.putProperty("BatchId", konf.getAsString(KonfigurationSchluessel.PROPERTY_BATCH_ID));
        jc.doConfigure(configLocation);
    }

    /**
     * erzeugt eine neue Instanz und setzt die Konfiguration.
     *
     * @param rahmenKonfiguration
     *            die Konfiguration fuer den Batch-Rahmen.
     * @param protokoll
     *            das ErgebinsProtokoll.
     */
    public BatchLauncher(BatchKonfiguration rahmenKonfiguration, BatchErgebnisProtokoll protokoll) {
        this.rahmenKonfiguration = rahmenKonfiguration;
        this.protokoll = protokoll;
    }

    /**
     * Erzeugt die Spring-Kontexte fuer die Anwendung sowie den Batchrahmen. Startet die Batchrahmen-Bean über
     * Methode {@link Batchrahmen#runBatch(BatchKonfiguration, BatchErgebnisProtokoll)}.
     * @throws BatchAusfuehrungsException
     *             Wenn ein Fehler während der Batchausführung auftritt.
     *
     */
    private void launch() throws BatchAusfuehrungsException {
        Function<String, Class> loadClass = name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
            return null;
        };

        List<Class> anwendungContextConfigs = rahmenKonfiguration.getAnwendungSpringKonfigFiles().stream().map(loadClass).collect(
            Collectors.toList());
        List<Class> batchContextConfigs = rahmenKonfiguration.getBatchRahmenSpringKonfigFiles().stream().map(loadClass).collect(
            Collectors.toList());

        List<Class> configs = new ArrayList<>(anwendungContextConfigs);
        configs.addAll(batchContextConfigs);

        ConfigurableApplicationContext
            rahmen = new SpringApplicationBuilder().sources(configs.toArray(new Class[0]))
                                                   .bannerMode(Banner.Mode.OFF)
                                                   .registerShutdownHook(true)
                                                   .profiles(rahmenKonfiguration.getSpringProfiles())
                                                   .run();

        String rahmenBeanName =
            this.rahmenKonfiguration.getAsString(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_BEAN_NAME,
                "batchrahmen");
        Batchrahmen rahmenBean = (Batchrahmen) rahmen.getBean(rahmenBeanName);
        try {
            rahmenBean.runBatch(this.rahmenKonfiguration, this.protokoll);
        } finally {
            rahmen.close();
        }
    }
}
