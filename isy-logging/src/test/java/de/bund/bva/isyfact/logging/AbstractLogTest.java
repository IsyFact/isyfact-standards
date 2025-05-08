package de.bund.bva.isyfact.logging;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.bva.isyfact.logging.impl.Ereignisschluessel;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import org.junit.Assert;
import org.junit.Before;

/**
 * Base class for all logging tests. The class contains methods to check the log files created in the test cases.
 */
public abstract class AbstractLogTest {

    /** Prefix for all expected log files. */
    protected static final String VORLAGE_PREFIX = "VORLAGE_";

    /** Path to expected logs. */
    private static final Path EXPECTED_LOGS_PATH = Paths.get("src", "test", "resources", "logausgaben");

    /** Path where the tests write the log files. */
    protected static final String LOG_VERZEICHNIS = "target/var/log/isy-logging/";

    /** Log file name. */
    protected static final String LOG_DATEI = "testserver_testsystem.log";

    /** Logger to use in the subclasses. */
    private static final IsyLoggerStandard LOGGER = IsyLoggerFactory.getLogger(AbstractLogTest.class);

    /** Prefix for the attribute "zeitstempel" in a log file. */
    private static final String JSON_ZEITSTEMPEL_PRAEFIX = "zeitstempel\":\"";

    /** Suffix for the an attribute in a log file. */
    private static final String JSON_ATTRIBUT_SUFFIX = "\"";

    /** Prefix for the duration in a log entry. */
    private static final String JSON_DAUERTE_PRAEFIX = "dauerte ";

    /** Suffix for the duration in a log entry. */
    private static final String JSON_DAUER_SUFFIX = "ms";

    /** Prefix for the attribute "parameter2" in a log file. */
    private static final String JSON_PARAMETER2_PRAEFIX = "parameter2\":\"";

    /** Prefix for the attribute "parameter2" in a log file. */
    private static final String JSON_PARAMETER2_PRAEFIX_OHNE_LEERZEICHEN = "parameter2\":";

    /** Log entry closing symbol. */
    private static final String JSON_ENDE = "}";

    /** Prefix for the attribute "parameter4" in a log file. */
    private static final String JSON_PARAMETER4_PRAEFIX_OHNE_LEERZEICHEN = "parameter4\":";

    /** Prefix for the attribute "dauer" in a log file. */
    private static final String JSON_DAUER_PRAEFIX = "dauer\":\"";

    /** Prefix for the attribute "schluessel" in a log file. */
    private static final String JSON_SCHLUESSEL_PRAEFIX = "schluessel\":\"";

    /** Prefix for the attribute "level" in a log file. */
    private static final String JSON_LEVEL_PRAEFIX = "level\":\"";

    /** Prefix for the attribute "nachricht" in a log file. */
    private static final String JSON_NACHRICHT_PRAEFIX = "nachricht\":\"";

    /** Prefix for the attribute "exception" in a log file. */
    private static final String JSON_EXCEPTION_PRAEFIX = "exception\":\"";

    /** Attribute delimiter. */
    private static final String JSON_NAECHSTES_ATTRIBUT = ",";

    /** Failed test marker. */
    protected static final String KENNZEICHNUNG_FEHLERTEST = "Fehlertest";

    /** Log entry, which is a delimiter between test cases (first log entry when a test begins). */
    private static final String TRENNER_NEUER_TESTFALL = "***** NEUERTESTFALL *****";

    /**
     * Flag, if the result should be saved as expected output instead of being asserted. Must be set
     * manually before test execution.
     */
    private static final boolean UEBERNEHME_IN_VORLAGE = false;

    /** Event key for the test creating log entry without a message. */
    public static final String EREIGNISSCHLUESSEL_OHNE_NACHRICHT = "OHNENACHRIT";

    /**
     * Test setup.
     */
    @Before
    public void setUp() {

        // A start log entry will be created before each test to mark the beginning of the log entries for this test.
        // This is needed because the log file cannot be deleted between the executions of several tests.
        LOGGER.debug(TRENNER_NEUER_TESTFALL);

        // Set the correlation id
        while (MdcHelper.entferneKorrelationsId() != null) {
            // Reset correlation idt.
        }
        MdcHelper.entferneMarkerFachdaten();
        MdcHelper.pushKorrelationsId("STATISCHE-KORR-ID-1;STATISCHE-KORR-ID-2;STATISCHE-KORR-ID-3");
    }

    /**
     * Helper method to check the created log file. The log file will be compared to the expected output.
     * Both files are read and compared line by line.
     *
     * @param testfallName
     *            test case.
     */
    protected void pruefeLogdatei(String testfallName) {
        pruefeLogdatei(testfallName, false);
    }

    /**
     * Helper method to check the created log file. The log file will be compared to the expected output.
     * Both files are read and compared line by line.
     *
     * @param testfallName
     *            test case.
     * @param jsonVergleich
     *            flag, if JSON or String comparison should be performed.
     */
    protected void pruefeLogdatei(String testfallName, boolean jsonVergleich) {

        try {

            // Expected ouput file exists for each test case. The file name contains the name of the test case.
            File vorlageDatei = new File(EXPECTED_LOGS_PATH.toString(), VORLAGE_PREFIX + testfallName + ".log");
            File ergebnisDatei = new File(LOG_VERZEICHNIS + LOG_DATEI);

            if (!vorlageDatei.exists()) {
                Assert.fail("Vorlagedatei existiert nicht: " + vorlageDatei.getAbsolutePath());
            }
            if (!ergebnisDatei.exists()) {
                Assert.fail("Ergebnisdatei existiert nicht: " + ergebnisDatei.getAbsolutePath());
            }

            // Collect log entries, which belong the current log record (lines after the last delimiter to the
            // end of file).
            List<String> ergebnis = new ArrayList<>();
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
                // Write the result into the expected ouput file. This can be used to bulk adjust the
                // expected output files.
                vorlageDatei.delete();
                PrintWriter vorlageWriter = new PrintWriter(new FileWriter(vorlageDatei));
                for (String zeile : ergebnis) {
                    vorlageWriter.println(zeile);
                }
                vorlageWriter.close();
            } else {
                // Read expected ouput.
                BufferedReader vorlageReader = new BufferedReader(new FileReader(vorlageDatei));
                String zeileVorlage;
                int i = 0;
                while ((zeileVorlage = vorlageReader.readLine()) != null) {
                    // Consistency check of the line
                    pruefeLogZeile(ergebnis.get(i));
                    // Compare line by line the expected output with the result file.
                    if (jsonVergleich) {
                        vergleicheLogZeileJson(zeileVorlage, ergebnis.get(i));
                    } else {
                        vergleicheLogZeile(zeileVorlage, ergebnis.get(i), i, testfallName);
                    }
                    i++;
                }
                if (ergebnis.size() > i) {
                    // Too many log entries were created.
                    Assert.fail("Die erstellte Logdatei enthält zuviele Zeilen: " + zeileErgebnis);
                }
                vorlageReader.close();
            }

        } catch (IOException e) {
            Assert.fail("Fehler beim Zugriff auf die Vorlage-Logdatei:" + testfallName + e.getMessage());
        }

    }

    private void vergleicheLogZeileJson(String zeileVorlage, String zeileErgebnis) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void vergleicheJsonNodes(JsonNode nodeVorlage, JsonNode nodeErgebnis, String ereignisschluessel) {
        Iterator<String> feldnamenVorlageIter = nodeVorlage.fieldNames();
        Iterator<String> feldnamenErgebnisIter = nodeErgebnis.fieldNames();

        List<String> feldnamenVorlage = new ArrayList<>();
        List<String> feldnamenErgebnis = new ArrayList<>();

        while (feldnamenVorlageIter.hasNext()) {
            feldnamenVorlage.add(feldnamenVorlageIter.next());
            feldnamenErgebnis.add(feldnamenErgebnisIter.next());
        }

        Collections.sort(feldnamenVorlage);
        Collections.sort(feldnamenErgebnis);

        // Check if there are too many fields in the result
        List<String> feldnamenErgebnisZuViel = new ArrayList<>();
        while (feldnamenErgebnisIter.hasNext()) {
            feldnamenErgebnisZuViel.add(feldnamenErgebnisIter.next());
        }
        Assert.assertTrue("Zuviele Felder im Ergebnis: " + feldnamenErgebnisZuViel,
                feldnamenErgebnisZuViel.isEmpty());

        // Compare result to the expected output
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
                    // Difference is allowed
                    textVorlage = "";
                    textErgebnis = "";
                }
                if ("dauer".equals(feldnameVorlage)) {
                    // Duration must be a number
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

                    // Difference is allowed
                    textVorlage = "";
                    textErgebnis = "";
                }
                if ("parameter2".equals(feldnameVorlage)) {
                    if (("EISYLO01005").equals(ereignisschluessel)) {
                        // Parameter contains the duration
                        textVorlage = "";
                        textErgebnis = "";
                    }
                }
                if ("nachricht".equals(feldnameVorlage)) {
                    // "dauerte 100 ms" replaced by "dauerte ERSETZT ms"
                    textVorlage = ersetzeString(textVorlage, JSON_DAUERTE_PRAEFIX, JSON_DAUER_SUFFIX);
                    textErgebnis = ersetzeString(textErgebnis, JSON_DAUERTE_PRAEFIX, JSON_DAUER_SUFFIX);

                    // "hashcode=12345," replaced by "hashcode=ERSETZT,"
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
     * Helper method to compare 2 lines. Depending on the log key the variable parts (e.g. "zeitstempel",
     * "hashwerte") are replaced by constant values, so that the values are equal.
     *
     * @param zeileVorlage
     *            the expected line.
     * @param zeileErgebnis
     *            the actual line.
     * @param zeilennummer
     *            the line number.
     * @param testfallName
     *            test case.
     */
    private void vergleicheLogZeile(String zeileVorlage, String zeileErgebnis, int zeilennummer,
            String testfallName) {

        // Tests should run on Windows and Unix, so the line endings are adjusted
        zeileVorlage = zeileVorlage.replaceAll("\\\\r\\\\n", "\\\\n");
        zeileErgebnis = zeileErgebnis.replaceAll("\\\\r\\\\n", "\\\\n");

        // The id cannot be asserted when an exception occurs.
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

        // Exceptions cannot be compared (code lines and ids are to different in different environments)
        zeileVorlage = ersetzeString(zeileVorlage, JSON_EXCEPTION_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
        zeileErgebnis = ersetzeString(zeileErgebnis, JSON_EXCEPTION_PRAEFIX, JSON_ATTRIBUT_SUFFIX);

        zeileVorlage = ersetzeString(zeileVorlage, JSON_ZEITSTEMPEL_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
        zeileErgebnis = ersetzeString(zeileErgebnis, JSON_ZEITSTEMPEL_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
        zeileVorlage = ersetzeString(zeileVorlage, JSON_DAUERTE_PRAEFIX, JSON_DAUER_SUFFIX);
        zeileErgebnis = ersetzeString(zeileErgebnis, JSON_DAUERTE_PRAEFIX, JSON_DAUER_SUFFIX);

        zeileVorlage = ersetzeString(zeileVorlage, JSON_DAUER_PRAEFIX, JSON_ATTRIBUT_SUFFIX);
        zeileErgebnis = ersetzeString(zeileErgebnis, JSON_DAUER_PRAEFIX, JSON_ATTRIBUT_SUFFIX);

        // Replace "hashCode" in text and JSON
        zeileVorlage = ersetzeString(zeileVorlage, "hashCode=", ",");
        zeileVorlage = ersetzeString(zeileVorlage, "hashCode\":\"", "\"");
        zeileErgebnis = ersetzeString(zeileErgebnis, "hashCode=", ",");
        zeileErgebnis = ersetzeString(zeileErgebnis, "hashCode\":\"", "\"");

        // Replace "Bereitsverarbeitet"
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

        // Parameter values can be different in the different test environments. Therefore, the logged values
        // cannot be compared with the parameter values.
        if (zeileVorlage.contains(Ereignisschluessel.EISYLO02004.name())) {
            zeileVorlage = ersetzeString(zeileVorlage, "besitzt den Wert", ".\"");
            zeileErgebnis = ersetzeString(zeileErgebnis, "besitzt den Wert", ".\"");
            zeileVorlage = ersetzeString(zeileVorlage, JSON_PARAMETER2_PRAEFIX_OHNE_LEERZEICHEN, JSON_ENDE);
            zeileErgebnis = ersetzeString(zeileErgebnis, JSON_PARAMETER2_PRAEFIX_OHNE_LEERZEICHEN, JSON_ENDE);
        }

        // Special case: the hash of the object is written (Class@1234567{Daten), if the parameters are serialized.
        if (zeileVorlage.contains("wurde mit folgenden Parametern aufgerufen")) {
            zeileVorlage = ersetzeString(zeileVorlage, "@", "\"");
            zeileErgebnis = ersetzeString(zeileErgebnis, "@", "\"");
        }

        if (zeileVorlage.contains("overrides previous : de.bund.bva.isyfact.logging.LogbackTest")) {
            zeileVorlage = ersetzeString(zeileVorlage, "@", "\"");
            zeileErgebnis = ersetzeString(zeileErgebnis, "@", "\"");
        }

        if ("testAufrufMitExceptionInterceptor".equals(testfallName)) {
            // Test case with an Exception written by the method interceptor. The id of the 'EnhancerByCGLIB'
            // in the stack trace is different in every call.
            zeileVorlage = ersetzeString(zeileVorlage, "EnhancerByCGLIB$$", "\"");
            zeileErgebnis = ersetzeString(zeileErgebnis, "EnhancerByCGLIB$$", "\"");
        }

        Assert.assertEquals("Fehler in Zeile " + zeilennummer, zeileVorlage, zeileErgebnis);
    }

    /**
     * Helper method to replace the duration in parameter 4.
     *
     * @param zeile
     *            the line in which the duration should be replaced.
     * @return the line with the replacement.
     */
    private String ersetzeDauerInParameter4(String zeile) {
        return ersetzeString(zeile, JSON_PARAMETER4_PRAEFIX_OHNE_LEERZEICHEN, JSON_NAECHSTES_ATTRIBUT);
    }

    /**
     * Execute consistency checks in the log lines.
     *
     * @param zeileErgebnis
     *            log line.
     */
    private void pruefeLogZeile(String zeileErgebnis) {
        String dauer = leseSubString(zeileErgebnis, JSON_DAUER_PRAEFIX, JSON_ATTRIBUT_SUFFIX, 0);
        if (dauer != null) {
            int dauerInt = Integer.parseInt(dauer);
            Assert.assertTrue("Es wurde eine ungültige Dauer gelogged: " + dauerInt, dauerInt > 0
                    && dauerInt < 1000);
        }

        if (zeileErgebnis.contains(JSON_DAUERTE_PRAEFIX)) {
            Assert.assertNotNull("Text 'dauerte' in Nachricht gefunden, aber kein Marker 'dauer' vorhanden",
                    dauer);
        }

        if (dauer != null) {
            Assert.assertTrue("Marker 'dauer' vorhanden, aber Text 'dauerte' in Nachricht nicht gefunden",
                    zeileErgebnis.contains(JSON_DAUERTE_PRAEFIX));
        }

        String level = leseSubString(zeileErgebnis, JSON_LEVEL_PRAEFIX, JSON_ATTRIBUT_SUFFIX, 0);

        // "ALL" is used as default value, if the log level cannot be parsed. This value is return only
        // if a wrong log level is set.
        if ("ALL".equalsIgnoreCase(level)) {
            Assert.fail("Ungültiges Loglevel " + level);
        }

        // No keys must be provided only in tests, which use logback directly.
        boolean schluesselPflicht = !zeileErgebnis.contains("de.bund.bva.isyfact.logging.LogbackTest");
        String schluessel = null;
        if (schluesselPflicht) {
            // Check the key in the relevant log levels.
            if (!"DEBUG".equalsIgnoreCase(level) && !"TRACE".equalsIgnoreCase(level)) {
                schluessel = leseSubString(zeileErgebnis, JSON_SCHLUESSEL_PRAEFIX, JSON_ATTRIBUT_SUFFIX, 0);
                Assert.assertNotNull("Logeintrag besitzt keinen Schluessel.", schluessel);
                int schluesselLaenge = schluessel.length();
                // The length of the key must be 10 (error key) or 11 (event key).
                Assert.assertTrue("Schluessel hat falsche Länge: " + schluesselLaenge,
                        (schluesselLaenge == 10 || schluesselLaenge == 11));
            }
        }

        String nachricht = leseSubString(zeileErgebnis, JSON_NACHRICHT_PRAEFIX, JSON_ATTRIBUT_SUFFIX, 0);
        if (!EREIGNISSCHLUESSEL_OHNE_NACHRICHT.equals(schluessel)) {
            Assert.assertNotNull("Logeintrag besitzt keine Nachricht.", nachricht);
            // Some placeholders are not replaced in negative tests.
            boolean platzhalterErsetzen = !zeileErgebnis.contains(KENNZEICHNUNG_FEHLERTEST);
            if (platzhalterErsetzen) {
                Assert.assertFalse("Nachricht enthält nicht ersetzte Platzhalter: " + nachricht,
                        nachricht.contains("{}"));
            }
        }
    }

    /**
     * Replace the substring in a log line between the first occurrence of the given prefix and suffix.
     *
     * @param logzeile
     *            the log line.
     * @param praefix
     *            the prefix.
     * @param suffix
     *            the suffix.
     * @return the log line with replaced values.
     */
    private String ersetzeString(String logzeile, String praefix, String suffix) {
        return ersetzeString(logzeile, praefix, suffix, 0);
    }

    /**
     * Replace the substring in a log line between the first occurrence of the given prefix and suffix.
     *
     * @param logzeile
     *            the log line.
     * @param praefix
     *            the prefix.
     * @param suffix
     *            the suffix.
     * @param offset
     *            character index, after which the replacement should be done.
     * @return the log line with replaced values.
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

        // The longest replacement is an object id with max 40 characters - except for stack traces.
        // Stack traces are longer and will not be checked.
        if (!praefix.equals(JSON_EXCEPTION_PRAEFIX)) {
            int anzahlErsetzterZeichen = ersetzenEnde - ersetzenStart;
            Assert.assertTrue("Fehler in den Tests - es wurden zu viele Zeichen ersetzt ("
                    + anzahlErsetzterZeichen + ") Vorher: [" + logZeileVorher + "] Nachher: [" + logzeile
                    + "]", anzahlErsetzterZeichen < 40);
        }

        return ersetzeString(logzeile, praefix, suffix, ersetzenEnde);
    }

    /**
     * Read the substring from a log line between the first occurrence of the given prefix and suffix.
     *
     * @param logzeile
     *            the log line.
     * @param praefix
     *            the prefix.
     * @param suffix
     *            the suffix.
     * @param offset
     *            character index, after which the characters will be read.
     * @return the read substring.
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
