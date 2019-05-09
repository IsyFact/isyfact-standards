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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.bva.isyfact.logging.impl.Ereignisschluessel;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import org.junit.Assert;
import org.junit.Before;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Oberklasse aller Logging-Tests. Die Klasse kapselt insbesondere die Funktionalität zum Prüfen der durch die
 * Testfälle erstellten Logdatei .
 * 
 */
public abstract class AbstractLogTest {

    /** Präfix aller Vorlage-Logdateien. */
    protected static final String VORLAGE_PREFIX = "VORLAGE_";

    /** Pfad, in dem die Logdatei durch die Tests erstellt wird. */
    protected static final String LOG_VERZEICHNIS = "target/var/log/isy-logging/";

    /** Name der Logdatei. */
    protected static final String LOG_DATEI = "testserver_testsystem.log";

    /** Der in den Subklassen einheitlich zu verwendende Logger. */
    private static final IsyLoggerStandard LOGGER = IsyLoggerFactory.getLogger(AbstractLogTest.class);

    /** Konstante für die Einleitung des Attributs "zeitstempel" in einem Logeintrag. */
    private static final String JSON_ZEITSTEMPEL_PRAEFIX = "zeitstempel\":\"";

    /** Konstante für den Abschluss eines Attributs im Logeintrag. */
    private static final String JSON_ATTRIBUT_SUFFIX = "\"";

    /** Konstante für die Einleitung der Bearbeitungsdauer in einer Lognachricht. */
    private static final String JSON_DAUERTE_PRAEFIX = "dauerte ";

    /** Konstante für den Abschluss der Bearbeitungsdauer in einer Lognachricht. */
    private static final String JSON_DAUER_SUFFIX = "ms";

    /** Konstante für die Einleitung des Attributs "parameter2" in einem Logeintrag. */
    private static final String JSON_PARAMETER2_PRAEFIX = "parameter2\":\"";

    /** Konstante für die Einleitung des Attributs "parameter2" in einem Logeintrag. */
    private static final String JSON_PARAMETER2_PRAEFIX_OHNE_LEERZEICHEN = "parameter2\":";

    /** Abschließendes Zeichen des Logeintrags. */
    private static final String JSON_ENDE = "}";

    /** Konstante für die Einleitung des Attributs "parameter4" in einem Logeintrag. */
    private static final String JSON_PARAMETER4_PRAEFIX_OHNE_LEERZEICHEN = "parameter4\":";

    /** Konstante für die Einleitung des Attributs "dauer" in einem Logeintrag. */
    private static final String JSON_DAUER_PRAEFIX = "dauer\":\"";

    /** Konstante für die Einleitung des Attributs "schluessel" in einem Logeintrag. */
    private static final String JSON_SCHLUESSEL_PRAEFIX = "schluessel\":\"";

    /** Konstante für die Einleitung des Attributs "level\":\"" in einem Logeintrag. */
    private static final String JSON_LEVEL_PRAEFIX = "level\":\"";

    /** Konstante für die Einleitung des Attributs "nachricht" in einem Logeintrag. */
    private static final String JSON_NACHRICHT_PRAEFIX = "nachricht\":\"";

    /** Konstante für die Einleitung des Attributs "exception" in einem Logeintrag. */
    private static final String JSON_EXCEPTION_PRAEFIX = "exception\":\"";

    /** Konstante für den Trenner zwischen Attributen. */
    private static final String JSON_NAECHSTES_ATTRIBUT = ",";

    /** Kennzeichung eines Fehlertests. */
    protected static final String KENNZEICHNUNG_FEHLERTEST = "Fehlertest";

    /**
     * Konstante für eine Lognachricht, die als Trenner der Testfälle dient (erster Logeintrag bei Beginn des
     * Tests.
     */
    private static final String TRENNER_NEUER_TESTFALL = "***** NEUERTESTFALL *****";

    /**
     * Flag zum Kennzeichnen, ob statt einer Prüfung, dass erstellte Ergebnis als Vorlage übernommen werden
     * soll. Muss manuell, vor Ausführung der Tests gesetzt werden.
     */
    private static final boolean UEBERNEHME_IN_VORLAGE = false;

    /** Ereignisschlüssel, zum Test des Erstellens eines Logeintrags ohne Nachricht. */
    public static final String EREIGNISSCHLUESSEL_OHNE_NACHRICHT = "OHNENACHRIT";

    /**
     * Vorbereiten des Tests.
     */
    @Before
    public void setUp() {

        // Vor Start jedes Testfalls wird ein einleitender Logeintrag erstellt, um zu erkennen, wo die
        // Logeinträge dieses Tests beginnen. Dies ist notwendig, da die Logdatei beim Ausführen mehrerer
        // Tests nicht zwischenzeitlich geköscht werden kann.
        LOGGER.debug(TRENNER_NEUER_TESTFALL);

        // KorrelationsId setzen
        while (MdcHelper.entferneKorrelationsId() != null) {
            // Korrelations-Id wird geleert.
        }
        MdcHelper.entferneMarkerFachdaten();
        MdcHelper.pushKorrelationsId("STATISCHE-KORR-ID-1;STATISCHE-KORR-ID-2;STATISCHE-KORR-ID-3");
    }

    /**
     * Hilfsmethode zum überprüfen der erstellten Logdatei. Datei wird die Logdatei mit einer Vorlagedatei
     * verglichen. Dies geschieht in dem die Vorlagedatei und die erstellte Logdatei Zeilenweise eingelesen
     * und verglichen werden.
     * 
     * @param testfallName
     *            name des ausgeführten Testfalls.
     * @throws IOException
     *             beim einem Fehler bei Zugriff auf die Logdatei.
     */
    protected void pruefeLogdatei(String testfallName) {
        pruefeLogdatei(testfallName, false);
    }

    /**
     * Hilfsmethode zum überprüfen der erstellten Logdatei. Datei wird die Logdatei mit einer Vorlagedatei
     * verglichen. Dies geschieht in dem die Vorlagedatei und die erstellte Logdatei Zeilenweise eingelesen
     * und verglichen werden.
     * 
     * @param testfallName
     *            name des ausgeführten Testfalls.
     * @param jsonVergleich
     *            gibt an, ob der Vergleich auf JSON-oder auf String-Basis erfolgen soll.
     * @throws IOException
     *             beim einem Fehler bei Zugriff auf die Logdatei.
     */
    protected void pruefeLogdatei(String testfallName, boolean jsonVergleich) {

        try {

            // Für jeden Testfall existiert eine eigene Vorlagedatei, die den Namen des Testsfalls im Namen
            // trägt
            File vorlageDatei = new File("logausgaben/" + VORLAGE_PREFIX + testfallName + ".log");
            File ergebnisDatei = new File(LOG_VERZEICHNIS + LOG_DATEI);

            if (!vorlageDatei.exists()) {
                Assert.fail("Vorlagedatei existiert nicht: " + vorlageDatei.getAbsolutePath());
            }
            if (!ergebnisDatei.exists()) {
                Assert.fail("Ergebnisdatei existiert nicht: " + ergebnisDatei.getAbsolutePath());
            }

            // Es werden die Logeinträge gesammelt, die zum aktuellen Logeintrag gehören. Das sind die Zeilen
            // nach
            // dem letzten Trenner bis zum Ende der Datei.
            List<String> ergebnis = new ArrayList<String>();
            BufferedReader ergebnisReader = new BufferedReader(new FileReader(ergebnisDatei));
            String zeileErgebnis;
            while ((zeileErgebnis = ergebnisReader.readLine()) != null) {
                if (zeileErgebnis.contains(TRENNER_NEUER_TESTFALL)) {
                    ergebnis.clear();
                } else {
                    ergebnis.add(zeileErgebnis);
                }

            }
            ergebnisReader.close();

            if (UEBERNEHME_IN_VORLAGE) {
                // Automatisches übernehmen des Ergenisses als Vorlage. Dieser Mechanismus kann bei
                // querschnittlichen Änderungen die Vorlagedateien gerade zu ziehen.
                vorlageDatei.delete();
                PrintWriter vorlageWriter = new PrintWriter(new FileWriter(vorlageDatei));
                for (String zeile : ergebnis) {
                    vorlageWriter.println(zeile);
                }
                vorlageWriter.close();
            } else {
                // Einlesen der Vorlage.
                BufferedReader vorlageReader = new BufferedReader(new FileReader(vorlageDatei));
                String zeileVorlage;
                int i = 0;
                while ((zeileVorlage = vorlageReader.readLine()) != null) {
                    // Konsistenzprüfung der Zeile an sich
                    pruefeLogZeile(ergebnis.get(i), i);
                    // Zeilenweise vergleichen der Vorlage mit der Ergebnisdatei.
                    if (jsonVergleich) {
                        vergleicheLogZeileJson(zeileVorlage, ergebnis.get(i), i, testfallName);
                    } else {
                        vergleicheLogZeile(zeileVorlage, ergebnis.get(i), i, testfallName);
                    }
                    i++;
                }
                if (ergebnis.size() > i) {
                    // Es wurden zu viele Logeinträge erstellt.
                    Assert.fail("Die erstellte Logdatei enthält zuviele Zeilen: " + zeileErgebnis);
                }
                vorlageReader.close();
            }

        } catch (IOException e) {
            Assert.fail("Fehler beim Zugriff auf die Vorlage-Logdatei:" + testfallName + e.getMessage());
        }

    }

    private void vergleicheLogZeileJson(String zeileVorlage, String zeileErgebnis, int zeilennummer,
            String testfallName) {
        ObjectMapper om = new ObjectMapper();
        try {
            JsonNode treeVorlage = om.readTree(zeileVorlage);
            JsonNode treeErgebnis = om.readTree(zeileErgebnis);

            JsonNode schluesselNode = treeVorlage.get("schluessel");
            String ereignisschluessel = null;
            if (schluesselNode != null) {
                ereignisschluessel = schluesselNode.asText();
            }
            vergleicheJsonNodes(treeVorlage, treeErgebnis, ereignisschluessel);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void vergleicheJsonNodes(JsonNode nodeVorlage, JsonNode nodeErgebnis, String ereignisschluessel) {
        Iterator<String> feldnamenVorlageIter = nodeVorlage.fieldNames();
        Iterator<String> feldnamenErgebnisIter = nodeErgebnis.fieldNames();

        List<String> feldnamenVorlage = new ArrayList<String>();
        List<String> feldnamenErgebnis = new ArrayList<String>();

        while (feldnamenVorlageIter.hasNext()) {
            feldnamenVorlage.add(feldnamenVorlageIter.next());
            feldnamenErgebnis.add(feldnamenErgebnisIter.next());
        }

        Collections.sort(feldnamenVorlage);
        Collections.sort(feldnamenErgebnis);

        // Erkennen zuvieler Felder im Ergebnis
        List<String> feldnamenErgebnisZuViel = new ArrayList<String>();
        while (feldnamenErgebnisIter.hasNext()) {
            feldnamenErgebnisZuViel.add(feldnamenErgebnisIter.next());
        }
        Assert.assertTrue("Zuviele Felder im Ergebnis: " + feldnamenErgebnisZuViel,
                feldnamenErgebnisZuViel.isEmpty());

        // Vergleich Vorlage mit Ergebnis
        for (int i = 0; i < feldnamenVorlage.size(); i++) {

            String feldnameVorlage = feldnamenVorlage.get(i);
            String feldnameErgebnis = feldnamenErgebnis.get(i);

            Assert.assertEquals("Feldnamen stimmen nicht überein.", feldnameVorlage, feldnameErgebnis);

            JsonNode jsonNodeVorlage = nodeVorlage.get(feldnameVorlage);
            JsonNode jsonNodeErgebnis = nodeErgebnis.get(feldnameVorlage);

            Assert.assertEquals("Node-Typen nicht identisch. ValueNode:", jsonNodeVorlage.isValueNode(),
                    jsonNodeErgebnis.isValueNode());

            if (jsonNodeVorlage.isValueNode()) {
                String textVorlage = jsonNodeVorlage.asText();
                String textErgebnis = jsonNodeErgebnis.asText();

                if ("zeitstempel".equals(feldnameVorlage)) {
                    // Abweichung wird akzeptiert
                    textVorlage = "";
                    textErgebnis = "";
                }
                if ("dauer".equals(feldnameVorlage)) {
                    // Dauer muss eine Zahl sein
                    try {
                        Integer.parseInt(textVorlage);
                    } catch (Exception e) {
                        Assert.fail("Dauer in Vorlage ist keine Zahl: " + textVorlage);
                    }
                    try {
                        Integer.parseInt(textErgebnis);
                    } catch (Exception e) {
                        Assert.fail("Dauer in Ergebnis ist keine Zahl: " + textVorlage);
                    }

                    // Abweichung wird akzeptiert
                    textVorlage = "";
                    textErgebnis = "";
                }
                if ("parameter2".equals(feldnameVorlage)) {
                    if (("EISYLO01005").equals(ereignisschluessel)) {
                        // Parameter enthält die Dauer
                        textVorlage = "";
                        textErgebnis = "";
                    }
                }
                if ("nachricht".equals(feldnameVorlage)) {
                    // "dauerte 100 ms" ersetzen durch "dauerte ERSETZT ms"
                    textVorlage = ersetzeString(textVorlage, JSON_DAUERTE_PRAEFIX, JSON_DAUER_SUFFIX);
                    textErgebnis = ersetzeString(textErgebnis, JSON_DAUERTE_PRAEFIX, JSON_DAUER_SUFFIX);

                    // "hashcode=12345," ersetzen durch "hashcode=ERSETZT,"
                    textVorlage = ersetzeString(textVorlage, "hashCode=", ",");
                    textErgebnis = ersetzeString(textErgebnis, "hashCode=", ",");
                }

                Assert.assertEquals("Textinhalte unterscheiden sich in Knoten '" + feldnameVorlage + "'",
                        textVorlage, textErgebnis);

            } else {
                vergleicheJsonNodes(jsonNodeVorlage, jsonNodeErgebnis, ereignisschluessel);
            }
        }

    }

    /**
     * Hilfsmethode zum Vergleichen zweier Logzeilen. In Abhängigkeit des Logschlüssels werden dabei die
     * Variablen Teile (bspw. Zeitstempel, Hashwerte...) durch eine Konstante ersetzt, damit die Werte gleich
     * sind.
     * 
     * @param zeileVorlage
     *            die erwartete Zeile.
     * @param zeileErgebnis
     *            die erstellte Zeile.
     * @param zeilennummer
     *            die Laufendenummer der Zeile.
     * @param testfallName
     *            Name des ausgeführten Testfalls.
     */
    private void vergleicheLogZeile(String zeileVorlage, String zeileErgebnis, int zeilennummer,
            String testfallName) {

        // Tests sollen auf Windoes und Unix laufen - daher Zeilenenden anpassen
        zeileVorlage = zeileVorlage.replaceAll("\\\\r\\\\n", "\\\\n");
        zeileErgebnis = zeileErgebnis.replaceAll("\\\\r\\\\n", "\\\\n");

        // Bei der Exception kann die ID im Stacktrace nicht überprüft werden.
        if (zeileVorlage.contains(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK)
                && zeileErgebnis.contains(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK)) {
            zeileVorlage = ersetzeString(zeileVorlage,
                    "SLF4J-Implementierung bereitgestellt: de.bund.bva.isyfact.logging.LoggingTest. #", "\\n");
            zeileVorlage = ersetzeString(zeileVorlage,
                    "SLF4J-Implementierung bereitgestellt: de.bund.bva.isyfact.logging.LogbackTest. #", "\\n");

            zeileErgebnis = ersetzeString(zeileErgebnis,
                    "SLF4J-Implementierung bereitgestellt: de.bund.bva.isyfact.logging.LoggingTest. #", "\\n");
            zeileErgebnis = ersetzeString(zeileErgebnis,
                    "SLF4J-Implementierung bereitgestellt: de.bund.bva.isyfact.logging.LogbackTest. #", "\\n");
        }

        // Exceptions können nicht verglichen werden (Codezeilen, IDs zu unterschiedlich auf verschiedenen
        // Umgebungen)
        zeileVorlage = ersetzeString(zeileVorlage, JSON_EXCEPTION_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
        zeileErgebnis = ersetzeString(zeileErgebnis, JSON_EXCEPTION_PRAEFIX, JSON_ATTRIBUT_SUFFIX);

        zeileVorlage = ersetzeString(zeileVorlage, JSON_ZEITSTEMPEL_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
        zeileErgebnis = ersetzeString(zeileErgebnis, JSON_ZEITSTEMPEL_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
        zeileVorlage = ersetzeString(zeileVorlage, JSON_DAUERTE_PRAEFIX, JSON_DAUER_SUFFIX);
        zeileErgebnis = ersetzeString(zeileErgebnis, JSON_DAUERTE_PRAEFIX, JSON_DAUER_SUFFIX);

        zeileVorlage = ersetzeString(zeileVorlage, JSON_DAUER_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
        zeileErgebnis = ersetzeString(zeileErgebnis, JSON_DAUER_PRAEFIX, JSON_ATTRIBUT_SUFFIX);

        // hashCode in Text und als JSON ersetzen ersetzen
        zeileVorlage = ersetzeString(zeileVorlage, "hashCode=", ",");
        zeileVorlage = ersetzeString(zeileVorlage, "hashCode\":\"", "\"");
        zeileErgebnis = ersetzeString(zeileErgebnis, "hashCode=", ",");
        zeileErgebnis = ersetzeString(zeileErgebnis, "hashCode\":\"", "\"");

        // "Bereitsverarbeitet ersetzen
        zeileVorlage = ersetzeString(zeileVorlage, "[\"Bereits verarbeitet: ", "\"]");
        zeileVorlage = ersetzeString(zeileVorlage, "[Bereits verarbeitet: ", "]");
        zeileErgebnis = ersetzeString(zeileErgebnis, "[\"Bereits verarbeitet: ", "\"]");
        zeileErgebnis = ersetzeString(zeileErgebnis, "[Bereits verarbeitet: ", "]");

        if (zeileVorlage.contains(Ereignisschluessel.EISYLO01004.name())) {
            zeileVorlage = ersetzeString(zeileVorlage, JSON_PARAMETER2_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
            zeileErgebnis = ersetzeString(zeileErgebnis, JSON_PARAMETER2_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
        }

        if (zeileVorlage.contains(Ereignisschluessel.EISYLO01014.name())) {
            zeileVorlage = ersetzeDauerInParameter4(zeileVorlage);
            zeileErgebnis = ersetzeDauerInParameter4(zeileErgebnis);
        }

        if (zeileVorlage.contains(Ereignisschluessel.EISYLO01005.name())) {
            zeileVorlage = ersetzeString(zeileVorlage, JSON_PARAMETER2_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
            zeileErgebnis = ersetzeString(zeileErgebnis, JSON_PARAMETER2_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
        }

        if (zeileVorlage.contains(Ereignisschluessel.EISYLO01015.name())) {
            zeileVorlage = ersetzeString(zeileVorlage, JSON_PARAMETER2_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
            zeileErgebnis = ersetzeString(zeileErgebnis, JSON_PARAMETER2_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
            zeileVorlage = ersetzeDauerInParameter4(zeileVorlage);
            zeileErgebnis = ersetzeDauerInParameter4(zeileErgebnis);
        }

        // Umgebungsparameter können zwischen den Verschiedenen Umgebungen, auf denen die Tests laufen,
        // variieren. Daher können die geloggten Werte im Text als auch im Parameter nicht verglichen werden.
        if (zeileVorlage.contains(Ereignisschluessel.EISYLO02004.name())) {
            zeileVorlage = ersetzeString(zeileVorlage, "besitzt den Wert", ".\"");
            zeileErgebnis = ersetzeString(zeileErgebnis, "besitzt den Wert", ".\"");
            zeileVorlage = ersetzeString(zeileVorlage, JSON_PARAMETER2_PRAEFIX_OHNE_LEERZEICHEN, JSON_ENDE);
            zeileErgebnis = ersetzeString(zeileErgebnis, JSON_PARAMETER2_PRAEFIX_OHNE_LEERZEICHEN, JSON_ENDE);
        }

        // Sondernbehandlung: Wenn die übergebenen Parameter serializiert werden, wir der Hash des Objects mit
        // ausgegben (Klasse@1234567{Daten)
        if (zeileVorlage.contains("wurde mit folgenden Parametern aufgerufen")) {
            zeileVorlage = ersetzeString(zeileVorlage, "@", "\"");
            zeileErgebnis = ersetzeString(zeileErgebnis, "@", "\"");
        }

        if (zeileVorlage.contains("overrides previous : de.bund.bva.isyfact.logging.LogbackTest")) {
            zeileVorlage = ersetzeString(zeileVorlage, "@", "\"");
            zeileErgebnis = ersetzeString(zeileErgebnis, "@", "\"");
        }

        if ("testAufrufMitExceptionInterceptor".equals(testfallName)) {
            // Testfall mit Ausgabe einer Exception durch den Methodinterceptor. Die Id des 'EnhancerByCGLIB'
            // im Stacktrace ändert sich bei jedem Auruf.
            zeileVorlage = ersetzeString(zeileVorlage, "EnhancerByCGLIB$$", "\"");
            zeileErgebnis = ersetzeString(zeileErgebnis, "EnhancerByCGLIB$$", "\"");
        }

        Assert.assertEquals("Fehler in Zeile " + zeilennummer, zeileVorlage, zeileErgebnis);
    }

    /**
     * Hilfsmethode zum Ersetzen der Dauer in Parameter 4.
     * 
     * @param zeile
     *            die Zeile, in der die Ersetzung stattfinden soll.
     * @return die Zeile mit der Ersetzung.
     */
    private String ersetzeDauerInParameter4(String zeile) {
        return ersetzeString(zeile, JSON_PARAMETER4_PRAEFIX_OHNE_LEERZEICHEN, JSON_NAECHSTES_ATTRIBUT);
    }

    /**
     * Führt einzelne Konsistenzprüfungen auf den Logzeilen durch.
     * 
     * @param zeileErgebnis
     *            die erstellte Zeile.
     * @param zeilennummer
     *            die Laufendenummer der Zeile.
     */
    private void pruefeLogZeile(String zeileErgebnis, int zeilennummer) {
        String dauer = leseSubString(zeileErgebnis, JSON_DAUER_PRAEFIX, JSON_ATTRIBUT_SUFFIX, 0);
        if (dauer != null) {
            int dauerInt = Integer.parseInt(dauer);
            Assert.assertTrue("Es wurde eine ungültige Dauer gelogged: " + dauerInt, dauerInt > 0
                    && dauerInt < 1000);
        }

        if (zeileErgebnis.contains(JSON_DAUERTE_PRAEFIX)) {
            Assert.assertTrue("Text 'dauerte' in Nachricht gefunden, aber kein Marker 'dauer' vorhanden",
                    dauer != null);
        }

        if (dauer != null) {
            Assert.assertTrue("Marker 'dauer' vorhanden, aber Text 'dauerte' in Nachricht nicht gefunden",
                    zeileErgebnis.contains(JSON_DAUERTE_PRAEFIX));
        }

        String level = leseSubString(zeileErgebnis, JSON_LEVEL_PRAEFIX, JSON_ATTRIBUT_SUFFIX, 0);

        // "ALL wird als Default verwendet, wenn das Loglevel nicht geparsed werden konnte. Da wir dieses
        // Loglevel beim Aufruf nicht verwenden entspricht wird dieses nur zurückgeliefert, wenn ein
        // Fehlerhaftes Level übergeben wurde.
        if (level.equalsIgnoreCase("ALL")) {
            Assert.fail("Ungültiges Loglevel " + level);
        }

        // Nur bei den Tests, die direkt Logback verwenden, müssen keine Schlüssel angegeben werden.
        boolean schluesselPflicht = !zeileErgebnis.contains("de.bund.bva.isyfact.logging.LogbackTest");
        String schluessel = null;
        if (schluesselPflicht) {
            // Schlüssel prüfen bei relevanten Leveln
            if (!level.equalsIgnoreCase("DEBUG") && !level.equalsIgnoreCase("TRACE")) {
                schluessel = leseSubString(zeileErgebnis, JSON_SCHLUESSEL_PRAEFIX, JSON_ATTRIBUT_SUFFIX, 0);
                Assert.assertNotNull("Logeintrag besitzt keinen Schluessel.", schluessel);
                int schluesselLaenge = schluessel.length();
                // Die Länge des Schlüssels muss 10 (Fehlerschluessel) oder 11 (Ereignisschluessel) sein
                Assert.assertTrue("Schluessel hat falsche Länge: " + schluesselLaenge,
                        (schluesselLaenge == 10 || schluesselLaenge == 11));
            }
        }

        String nachricht = leseSubString(zeileErgebnis, JSON_NACHRICHT_PRAEFIX, JSON_ATTRIBUT_SUFFIX, 0);
        if (!EREIGNISSCHLUESSEL_OHNE_NACHRICHT.equals(schluessel)) {
            Assert.assertNotNull("Logeintrag besitzt keine Nachricht.", nachricht);
            int nachrichtLaenge = nachricht.length();
            //Assert.assertTrue("Nachricht hat unplausible Länge: " + nachrichtLaenge, (nachrichtLaenge > 25));
            // Bei den explizit fehlerhaften Tests werden Platzhalter teilweise nicht ersetzt.
            boolean paltzhalterErsetzen = !zeileErgebnis.contains(KENNZEICHNUNG_FEHLERTEST);
            if (paltzhalterErsetzen) {
                Assert.assertFalse("Nachricht enthält nicht ersetzte Platzhalter: " + nachricht,
                        nachricht.contains("{}"));
            }
        }
    }

    /**
     * Ersetzt einen String in einer Logzeile beginnend nach dem ersten auftreten des übergebenen Präfix, bis
     * zum danach erstmaligen auftreten des übergebenen Suffix.
     * 
     * @param logzeile
     *            die Logzeile.
     * @param praefix
     *            der Präfix.
     * @param suffix
     *            der Suffix.
     * @return die Logzeile mit durchgeführten Ersetzungen.
     */
    private String ersetzeString(String logzeile, String praefix, String suffix) {
        return ersetzeString(logzeile, praefix, suffix, 0);
    }

    /**
     * Ersetzt einen String in einer Logzeile beginnend nach dem ersten auftreten des übergebenen Präfix, bis
     * zum danach erstmaligen auftreten des übergebenen Suffix.
     * 
     * @param logzeile
     *            die Logzeile.
     * @param praefix
     *            der Präfix.
     * @param suffix
     *            der Suffix.
     * @param offset
     *            index des Zeichens, ab dem mit der Ersetzung begonnen werden soll.
     * @return die Logzeile mit durchgeführten Ersetzungen.
     */
    private String ersetzeString(String logzeile, String praefix, String suffix, int offset) {

        int ersetzenStart = logzeile.indexOf(praefix, offset);

        if (ersetzenStart < 0) {
            return logzeile;
        }

        ersetzenStart += praefix.length();
        int ersetzenEnde = logzeile.indexOf(suffix, ersetzenStart);

        if (ersetzenEnde < 0) {
            Assert.fail(suffix + " in Zeile nicht nach " + praefix + " gefunden in Zeile " + logzeile);
        }

        String logZeileVorher = logzeile;

        logzeile = logzeile.substring(0, ersetzenStart) + "ERSETZT" + logzeile.substring(ersetzenEnde);

        // Die längste Ersetzung ist eine Objekt ID mit max 40 Zeichen - bis auf Stacktraces.
        // Diese sind länger und werden daher hier nicht geprüft.
        if (!praefix.equals(JSON_EXCEPTION_PRAEFIX)) {
            int anzahlErsetzterZeichen = ersetzenEnde - ersetzenStart;
            Assert.assertTrue("Fehler in den Tests - es wurden zu viele Zeichen ersetzt ("
                    + anzahlErsetzterZeichen + ") Vorher: [" + logZeileVorher + "] Nachher: [" + logzeile
                    + "]", anzahlErsetzterZeichen < 40);
        }

        return ersetzeString(logzeile, praefix, suffix, ersetzenEnde);
    }

    /**
     * Liest einen String in einer Logzeile beginnend nach dem ersten auftreten des übergebenen Präfix, bis
     * zum danach erstmaligen auftreten des übergebenen Suffix.
     * 
     * @param logzeile
     *            die Logzeile.
     * @param praefix
     *            der Präfix.
     * @param suffix
     *            der Suffix.
     * @param offset
     *            index des Zeichens, ab dem mit der Ersetzung begonnen werden soll.
     * @return die Logzeile mit durchgeführten Ersetzungen.
     */
    private String leseSubString(String logzeile, String praefix, String suffix, int offset) {

        int lesenStart = logzeile.indexOf(praefix, offset);

        if (lesenStart < 0) {
            return null;
        }

        lesenStart += praefix.length();
        int lesenEnde = logzeile.indexOf(suffix, lesenStart);

        if (lesenEnde < 0) {
            Assert.fail(suffix + " in Zeile nicht nach " + praefix + " gefunden in Zeile " + logzeile);
        }

        return logzeile.substring(lesenStart, lesenEnde);
    }

}
