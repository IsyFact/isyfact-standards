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
package de.bund.bva.isyfact.batchrahmen;

import java.io.File;
import java.net.URISyntaxException;

import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher;
import de.bund.bva.isyfact.batchrahmen.test.BatchProtokollTester;
import de.bund.bva.isyfact.batchrahmen.test.TestBatchLauchner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@ContextConfiguration(classes = AnwendungTestConfig.class)
public class BatchrahmenTest {

    /** Datei für das Batch-Protokoll. Wird in {@link #init()} gesetzt. */
    private static String ERGEBNIS_DATEI;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void init() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS BATCHSTATUS;"
                               + "CREATE TABLE IF NOT EXISTS BATCHSTATUS ("
                               +   "BATCHID VARCHAR2(255) NOT NULL,"
                               +   "BATCHNAME VARCHAR2(255),"
                               +   "BATCHSTATUS VARCHAR2(255),"
                               +   "SATZNUMMERLETZTESCOMMIT BIGINT,"
                               +   "SCHLUESSELLETZTESCOMMIT VARCHAR2(255),"
                               +   "DATUMLETZTERSTART TIMESTAMP,"
                               +   "DATUMLETZTERABBRUCH TIMESTAMP,"
                               +   "DATUMLETZTERERFOLG TIMESTAMP,"
                               +   "CONSTRAINT BATCHSTATUS_PK PRIMARY KEY (BATCHID));");
        //jdbcTemplate.execute("DELETE FROM batchstatus");

        try {
            ERGEBNIS_DATEI = new File(
                BatchrahmenTest.class.getResource("/resources/batch/ausgabe/ergebnisdatei.xml").toURI())
                .getAbsolutePath();

        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Testet die Ausführung des Batchrahmens mit dem TestBatchLauncher, der den Batch in einer eigenen VM
     * ausführt.
     */
    @Test
    public void testBatchrahmenMitTestBatchLauncher() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(0, batchLauncher.starteBatch(BatchStartTyp.START, null));
        assertEquals("beendet", getBatchStatus("basicTestBatch-1"));
    }

    /**
     * Testet die Ausführung des Batchrahmens.
     */
    @Test
    public void testBatchrahmen() throws Exception {
        assertEquals(0, BatchLauncher
            .run(new String[] { "-start", "-cfg", "/resources/batch/basic-test-batch-1-config.properties" }));
        assertEquals("beendet", getBatchStatus("basicTestBatch-1"));
    }

    /**
     * Testet die Behandlung des Status-Satzes, wenn der Batch mit einem Fehler in der Initialisierung
     * abbricht. Der Status muss bei einem Abbruch während der Initialisierung auf "beendet" stehen.
     */
    @Test
    public void testFehlerInInit() throws Exception {
        assertEquals(2, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-initError", "true" }));
        assertEquals("neu", getBatchStatus("errorTestBatch-1"));

        assertEquals(0, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-initError", "false" }));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));

        assertEquals(2, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-initError", "true" }));
        // TODO: batchStatus kann nicht "beendet" sein, da "-initError" true ist und deswegen eine Exception
        // geworfen wird.
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));

        // Test nach Abbruch
        assertEquals(2, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufError", "true" }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));

        assertEquals(2, BatchLauncher.run(new String[] { "-start", "-ignoriereRestart", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-initError", "true" }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
    }

    /**
     * Testet, dass nach einem Abbruch der Batch nur mit ignoriereLauf neu gestartet werden kann.
     */
    @Test
    public void testIgnoriereLauf() throws Exception {
        assertEquals(2, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufError", "true" }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));

        assertEquals(3, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufError", "true" }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));

        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-ignoriereRestart", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-laufError", "false" }));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));
    }

    /**
     * Testet, dass nach einem Abbruch der Batch mit Restart gestartet werden kann.
     */
    @Test
    public void testRestart() throws Exception {
        assertEquals(2, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        BatchProtokollTester bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(3, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(2, BatchLauncher.run(
            new String[] { "-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        assertEquals(0, BatchLauncher.run(
            new String[] { "-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufError", "false", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        // Ab hier Prüfung, dass das obige Verhalten auch greift, wenn der Abbruch nach der Initialisierung
        // aber vor Verarbeitung eines Satzes erfolgt (0 Sätze verarbeitet).
        assertEquals(2, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(3, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(2, BatchLauncher.run(
            new String[] { "-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        assertEquals(0, BatchLauncher.run(
            new String[] { "-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufErrorSofort", "false", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());
    }

    /**
     * Testet einen Batch, der als Daemon (uendlich) weiter läuft. Der Batch wird mit "kill -s INT pid"
     * geordnet beendet. Anschließend muss geprüft werden, ob im Log die Meldung Batch beendet geschrieben
     * wurde und der Status in der DB auf abgebrochen steht.
     *
     * Unter Windows kann der Batch mit "taskkill /PID 1234" beendet werden. PID ist dem Task-Manager zu
     * entnehmen.
     */
    @Test
    @Ignore("Erfordert manuelles 'kill -15' von außen")
    public void testShutdown() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/infinite-test-batch-1-config.properties");
        batchLauncher.starteBatch(BatchStartTyp.START, "/batch-2_out.xml", null);
        // assertEquals(143, batchLauncher.starteBatch(BatchStartTyp.START, "/batch-2_out.xml", null));
        BatchProtokollTester bpt = new BatchProtokollTester("/batch-2_out.xml");
        assertTrue(bpt.enthaeltMeldungsId("BENUTZERABBRUCH"));
    }

    /**
     * Testet ob der Batch mit dem Laufzeitparameter abgebrochen werden kann. Anschließend muss geprüft
     * werden, ob im Log die Meldung Batch beendet geschrieben wurde und der Status in der DB auf abgebrochen
     * steht.
     */
    @Test
    @Ignore(
        "Testet ob der Batch mit dem Laufzeitparameter abgebrochen werden kann. Anschließend muss geprüft "
            + "werden, ob im Log die Meldung Batch beendet geschrieben wurde und der Status in der DB auf abgebrochen "
            + "steht.")
    public void testLaufzeitParameter() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/infinite-test-batch-1-config.properties");
        assertEquals(144, batchLauncher
            .starteBatch(BatchStartTyp.START, "/laufzeit_out.xml", new String[] { "-laufzeit", "1" }));
        BatchProtokollTester bpt = new BatchProtokollTester("/laufzeit_out.xml");
        assertTrue(bpt.enthaeltMeldungsId("MAX_LAUFZEIT_UEBERSCHRITTEN"));
    }

    /**
     * Testet ob der Batch mit Parameter-Exception abbricht, wenn der Laufzeit-Parameter ohne die Minuten
     * angegeben ist. Dabei ist "-laufzeit" der letzte Parameter.
     */
    @Test
    public void testLaufzeitParameterOhneMinuten() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(3, batchLauncher
            .starteBatch(BatchStartTyp.START, "/laufzeit_out.xml", new String[] { "-laufzeit" }));
    }

    /**
     * Testet ob der Batch mit Parameter-Exception abbricht, wenn der Laufzeit-Parameter ohne die Minuten
     * angegeben ist. Der Parameter "-laufzeit" wird direkt mit einem anderen Parameter gefolgt.
     */
    @Test
    public void testLaufzeitParameterOhneMinutenIgnoriereTestlauf() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(3, batchLauncher.starteBatch(BatchStartTyp.START, "/laufzeit_out.xml",
            new String[] { "-laufzeit", "-ignoriereLauf" }));
    }

    /**
     * Testet ob der Batch mit Parameter-Exception abbricht, wenn der Laufzeit-Parameter ohne die Minuten
     * angegeben ist. Der Parameter "-laufzeit" vorne als Option angegeben.
     */
    @Test
    public void testLaufzeitParameterOhneMinutenAlsOption() throws Exception {
        assertEquals(3, BatchLauncher.run(new String[] { "-start", "-laufzeit", "-cfg",
            "/resources/batch/gesicherter-test-batch-1-config.properties", "-Batchrahmen.Ergebnisdatei",
            "/testOutput/batchRahmen.out.xml" }));
    }

    /**
     * Testet ob der Batch mit Parameter-Exception abbricht, wenn der Laufzeit-Parameter angegeben ist, aber
     * die Minuten nicht numerisch sind.
     */
    @Test
    public void testLaufzeitParameterMinutenNichtNumerisch() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(4, batchLauncher.starteBatch(BatchStartTyp.START, new String[] { "-laufzeit", "ABC" }));
    }

    /**
     * Liest den BatchStatus mit der angegebenen Id in einer eigenen Transaktion.
     *
     * @param batchId Id als Schlüssel für den BatchStatus
     * @return Den BatchStatus
     */
    private String getBatchStatus(final String batchId) {
        String sql = "SELECT batchstatus FROM batchstatus WHERE batchid = ?";
        return jdbcTemplate.queryForObject(sql, String.class, batchId);
    }

    /**
     * Testet die Absicherung eines Batches vor der Verarbeitung eines Satzes.
     */
    @Test
    public void testGesicherterBatchGutFall() throws Exception {
        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch-1-config.properties" }));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch-1"));
    }

    /**
     * Testet die Absicherung eines Batches vor der Verarbeitung eines Satzes.
     */
    @Test
    public void testGesicherterBatchLoginNichtMoeglich() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch-2-config.properties" }));
        assertEquals("abgebrochen", getBatchStatus("gesicherterTestBatch-2"));
    }

    /**
     * Testet die Absicherung eines Batches vor der Verarbeitung eines Satzes.
     */
    @Test
    public void testGesicherterBatchNichtErforderlicheRolle() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch-3-config.properties" }));
        assertEquals("abgebrochen", getBatchStatus("gesicherterTestBatch-3"));
    }

    /**
     * Testet die Absicherung eines Batches vor der Initialisierung.
     */
    @Test
    public void testGesicherterBatch2GutFall() throws Exception {
        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch2-1-config.properties" }));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch2-1"));
    }

    /**
     * Testet die Absicherung eines Batches vor der Initialisierung.
     */
    @Test
    public void testGesicherterBatch2LoginNichtMoeglich() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch2-2-config.properties" }));
        // Da der Batch nicht gestartet ist, muss der Status noch auf neu stehen
        assertEquals("neu", getBatchStatus("gesicherterTestBatch2-2"));
    }

    /**
     * Testet die Absicherung eines Batches vor der Initialisierung.
     */
    @Test
    public void testGesicherterBatch2NichtErforderlicheRolle() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch2-3-config.properties" }));
        // Da der Batch nicht gestartet ist, muss der Status noch auf neu stehen
        assertEquals("neu", getBatchStatus("gesicherterTestBatch2-3"));
    }

    /**
     * Testet die Ausführung des Batchrahmens mit dem TestBatchLauncher, der den Batch in einer eigenen VM
     * ausführt.
     */
    @Test
    public void testReturnCodeTestBatch() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/returnCode-test-batch-1-config.properties");
        assertEquals(BatchReturnCode.FEHLER_AUSGEFUEHRT.getWert(),
            batchLauncher.starteBatch(BatchStartTyp.START, null));
        assertEquals("beendet", getBatchStatus("returnCodeTestBatch-1"));
    }
}
