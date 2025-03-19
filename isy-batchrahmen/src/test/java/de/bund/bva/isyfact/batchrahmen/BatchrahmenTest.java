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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;

import ch.qos.logback.classic.LoggerContext;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher;
import de.bund.bva.isyfact.batchrahmen.test.BatchProtokollTester;
import de.bund.bva.isyfact.batchrahmen.test.TestBatchLauchner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
/* @SpringBootTest(classes = AnwendungTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test",
    "isy.logging.anwendung.version=test" })*/
@ContextConfiguration(classes = AnwendungTestConfig.class)
public class BatchrahmenTest {

    /** File for Batch protocol. Is set in {@link #init()}. */
    private static String ERGEBNIS_DATEI;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void init() {
        jdbcTemplate.execute("DELETE FROM BATCHSTATUS_KONFIGURATIONSPARAMETER; DELETE FROM BATCHSTATUS;");

        try {
            ERGEBNIS_DATEI = new File(
                BatchrahmenTest.class.getResource("/resources/batch/ausgabe/ergebnisdatei.xml").toURI())
                .getAbsolutePath();

        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the execution of the batch frame with the TestBatchLauncher, which executes the batch in a separate VM.
     */
    @Test
    public void testBatchrahmenMitTestBatchLauncher() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(0, batchLauncher.starteBatch(BatchStartTyp.START, null));
        assertEquals("beendet", getBatchStatus("basicTestBatch-1"));
    }

    /**
     * Tests the execution of the batch frame.
     */
    @Test
    public void testBatchrahmen() throws Exception {
        assertEquals(0, BatchLauncher
            .run(new String[] { "-start", "-cfg", "/resources/batch/basic-test-batch-1-config.properties" }));
        assertEquals("beendet", getBatchStatus("basicTestBatch-1"));
    }

    /**
     * Tests the handling of the status record if the batch terminates with an error in the initialization.
     * The status must be set to "finished" if the abort occurs during initialization.
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
        // TODO: batchStatus cannot be "finished" because "-initError" is true and therefore an exception is thrown.
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));

        // Test after abort
        assertEquals(2, BatchLauncher.run(
            new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                "-laufError", "true" }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));

        assertEquals(2, BatchLauncher.run(new String[] { "-start", "-ignoriereRestart", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-initError", "true" }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
    }

    /**
     * Tests that after an abort the batch can only be restarted with ignore run.
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
     * Tests that after an abort the batch can be started with Restart.
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

        // From here on check, that the above behavior also applies,
        // if the abort occurs after initialization but before processing a record (0 records processed).
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
     * Tests a batch that continues to run as daemon (uendlich). The batch is terminated with "kill -s INT pid".
     * Afterwards it must be checked if the message Batch finished was written in the log and the status in the DB is set to aborted.
     *
     * Windows, the batch can be terminated with "taskkill /PID 1234". PID can be taken from the task manager.
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
     * Tests if the batch can be aborted with the runtime parameter.
     * Afterwards it must be checked if the message Batch finished was written in the log and the status in the DB is set to aborted.
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
     * Tests if the batch with parameter exception terminates if the runtime parameter is specified without the minutes.
     * Here "-runtime" is the last parameter.
     */
    @Test
    public void testLaufzeitParameterOhneMinuten() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(3, batchLauncher
            .starteBatch(BatchStartTyp.START, "/laufzeit_out.xml", new String[] { "-laufzeit" }));
    }

    /**
     * Tests if the batch with parameter exception terminates if the runtime parameter is specified without the minutes.
     * The parameter "-laufzeit" is directly followed by another parameter.
     */
    @Test
    public void testLaufzeitParameterOhneMinutenIgnoriereTestlauf() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(3, batchLauncher.starteBatch(BatchStartTyp.START, "/laufzeit_out.xml",
            new String[] { "-laufzeit", "-ignoriereLauf" }));
    }

    /**
     * Tests if the batch with parameter exception terminates if the runtime parameter is specified without the minutes.
     * The parameter "-laufzeit" is specified as an option at the front.
     */
    @Test
    public void testLaufzeitParameterOhneMinutenAlsOption() throws Exception {
        assertEquals(3, BatchLauncher.run(new String[] { "-start", "-laufzeit", "-cfg",
            "/resources/batch/gesicherter-test-batch-1-config.properties", "-Batchrahmen.Ergebnisdatei",
            "/testOutput/batchRahmen.out.xml" }));
    }

    /**
     * Tests if the batch with parameter exception aborts if the runtime parameter is specified but the minutes are not numeric.
     */
    @Test
    public void testLaufzeitParameterMinutenNichtNumerisch() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(4, batchLauncher.starteBatch(BatchStartTyp.START, new String[] { "-laufzeit", "ABC" }));
    }

    /**
     * Reads the BatchStatus with the specified Id in a separate transaction.
     *
     * @param batchId Id as key for the BatchStatus
     * @return the BatchStatus
     */
    private String getBatchStatus(final String batchId) {
        String sql = "SELECT batchstatus FROM batchstatus WHERE batchid = ?";
        return jdbcTemplate.queryForObject(sql, String.class, batchId);
    }

    /**
     * Tests the protection of a batch before processing a set.
     */
    @Test
    public void testGesicherterBatchGutFall() throws Exception {
        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch-1-config.properties" }));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch-1"));
    }

    /**
     * Tests the protection of a batch before processing a set.
     */
    @Test
    public void testGesicherterBatchLoginNichtMoeglich() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch-2-config.properties" }));
        assertEquals("abgebrochen", getBatchStatus("gesicherterTestBatch-2"));
    }

    /**
     * Tests the protection of a batch before processing a set.
     */
    @Test
    public void testGesicherterBatchNichtErforderlicheRolle() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch-3-config.properties" }));
        assertEquals("abgebrochen", getBatchStatus("gesicherterTestBatch-3"));
    }

    /**
     * Tests the protection of a batch before initialization.
     */
    @Test
    public void testGesicherterBatch2GutFall() throws Exception {
        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch2-1-config.properties" }));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch2-1"));
    }

    /**
     * Tests the protection of a batch before initialization.
     */
    @Test
    public void testGesicherterBatch2LoginNichtMoeglich() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch2-2-config.properties" }));
        // Da der Batch nicht gestartet ist, muss der Status noch auf neu stehen
        assertEquals("neu", getBatchStatus("gesicherterTestBatch2-2"));
    }

    /**
     * Tests the protection of a batch before initialization.
     */
    @Test
    public void testGesicherterBatch2NichtErforderlicheRolle() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch2-3-config.properties" }));
        // Da der Batch nicht gestartet ist, muss der Status noch auf neu stehen
        assertEquals("neu", getBatchStatus("gesicherterTestBatch2-3"));
    }

    /**
     * Tests the protection of a batch when token renewal is deactivated.
     */
    @Test
    public void testGesicherterBatchNoReauth() throws Exception {
        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch2-1-config.properties" }));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch2-1"));
    }

    /**
     * Tests the execution of the batch frame with the TestBatchLauncher, which executes the batch in a separate VM.
     */
    @Test
    public void testReturnCodeTestBatch() throws Exception {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/returnCode-test-batch-1-config.properties");
        assertEquals(BatchReturnCode.FEHLER_AUSGEFUEHRT.getWert(),
            batchLauncher.starteBatch(BatchStartTyp.START, null));
        assertEquals("beendet", getBatchStatus("returnCodeTestBatch-1"));
    }

    @Test
    public void testBatchIdInLoggerContext() {
        assertEquals(0, BatchLauncher
                .run(new String[] { "-start", "-cfg", "/resources/batch/basic-test-batch-1-config.properties" }));
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        final String actualBatchId = loggerContext.getProperty("BatchId");
        assertEquals("BatchId ist in Batch LoggerContext gesetzt", "basicTestBatch-1", actualBatchId);
    }

    /**
     * test if correlation-id in AufrufKontext will be set during batch execution
     */
    @Test
    public void testAufrufKontextTestBatch() {
        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-cfg",
                "/resources/batch/aufruf-kontext-test-batch-config.properties" }));
        assertEquals("beendet", getBatchStatus("aufrufKontextTestBatch"));
    }
}
