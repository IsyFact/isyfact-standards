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
package de.bund.bva.isyfact.batchrahmen.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import de.bund.bva.isyfact.batchrahmen.AnwendungTestConfig;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.config.AbstractOidcProviderTest;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher;

import ch.qos.logback.classic.LoggerContext;


@SpringBootTest(classes = AnwendungTestConfig.class)
class BatchrahmenTest extends AbstractOidcProviderTest {

    /**
     * File for Batch protocol. Is set in {@link #setup()}.
     */
    private static String ERGEBNIS_DATEI;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.security.oauth2.client.registration.ropc-testclient.client-id}")
    private String ropcClientId;

    @Value("${spring.security.oauth2.client.registration.ropc-testclient.client-secret}")
    private String ropcClientSecret;

    @Value("${isy.security.oauth2.client.registration.ropc-testclient.username}")
    private String ropcUser;

    @Value("${isy.security.oauth2.client.registration.ropc-testclient.password}")
    private String ropcPassword;

    @Value("${isy.security.oauth2.client.registration.ropc-testclient.bhknz}")
    private String ropcBhknz;

    @Value("${spring.security.oauth2.client.registration.cc-testclient.client-id}")
    private String ccClientId;

    @Value("${spring.security.oauth2.client.registration.cc-testclient.client-secret}")
    private String ccClientSecret;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("DELETE FROM BATCHSTATUS_KONFIGURATIONSPARAMETER; DELETE FROM BATCHSTATUS;");

        try {
            ERGEBNIS_DATEI = new File(
                    BatchrahmenTest.class.getResource("/resources/batch/ausgabe/ergebnisdatei.xml").toURI())
                    .getAbsolutePath();

            embeddedOidcProvider.removeAllClients();
            embeddedOidcProvider.removeAllUsers();
            // client with authorization-grant-type=password
            embeddedOidcProvider.addUser(
                    ropcClientId,
                    ropcClientSecret,
                    ropcUser,
                    ropcPassword,
                    Optional.of(ropcBhknz),
                    Collections.singleton("Anwender")
            );
            embeddedOidcProvider.addClient(
                    ccClientId,
                    ccClientSecret,
                    Collections.singleton("Anwender")
            );

        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the execution of the batch frame with the TestBatchLauncher, which executes the batch in a separate VM.
     */
    @Test
    void testBatchrahmenMitTestBatchLauncher() throws Exception {
        TestBatchLauncher batchLauncher =
                new TestBatchLauncher("/resources/batch/basic-test-batch-1-config.properties");

        assertEquals(BatchReturnCode.OK.getWert(), batchLauncher.starteBatch(BatchStartTyp.START, null));
        assertEquals("beendet", getBatchStatus("basicTestBatch-1"));
    }

    /**
     * Tests the execution of the batch frame.
     */
    @Test
    void testBatchrahmen() throws Exception {
        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher
                .run(new String[]{"-start", "-cfg", "/resources/batch/basic-test-batch-1-config.properties"}));
        assertEquals("beendet", getBatchStatus("basicTestBatch-1"));
    }

    /**
     * Tests the exclusion of a bean in the batch context while executing the batch frame.
     */
    @Test
    void testBatchrahmenWithExclusion() {
        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher
                .run(new String[]{"-start", "-cfg", "/resources/batch/basic-test-batch-2-config.properties"}));
        assertEquals("beendet", getBatchStatus("basicTestBatch-1"));
    }

    /**
     * Tests the handling of the status record if the batch terminates with an error in the initialization.
     * The status must be set to "finished" if the abort occurs during initialization.
     */
    @Test
    void testFehlerInInit() throws Exception {
        assertEquals(BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-initError", "true"}));
        assertEquals("neu", getBatchStatus("errorTestBatch-1"));

        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(
                new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-initError", "false"}));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));

        assertEquals(BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-initError", "true"}));
        // TODO: batchStatus cannot be "finished" because "-initError" is true and therefore an exception is thrown.
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));

        // Test after abort
        assertEquals(BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufError", "true"}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));

        assertEquals(BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(new String[]{"-start", "-ignoriereRestart", "-cfg",
                "/resources/batch/error-test-batch-1-config.properties", "-initError", "true"}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
    }

    /**
     * Tests that after an abort the batch can only be restarted with ignore run.
     */
    @Test
    void testIgnoriereLauf() throws Exception {
        assertEquals(BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufError", "true"}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));

        assertEquals(BatchReturnCode.FEHLER_PARAMETER.getWert(), BatchLauncher.run(
                new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufError", "true"}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));

        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(new String[]{"-start", "-ignoriereRestart", "-cfg",
                "/resources/batch/error-test-batch-1-config.properties", "-laufError", "false"}));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));
    }

    /**
     * Tests that after an abort the batch can be started with Restart.
     */
    @Test
    void testRestart() throws Exception {
        assertEquals(BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        BatchProtokollTester bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(BatchReturnCode.FEHLER_PARAMETER.getWert(), BatchLauncher.run(
                new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[]{"-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(
                new String[]{"-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufError", "false", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        // From here on check, that the above behavior also applies,
        // if the abort occurs after initialization but before processing a record (0 records processed).
        assertEquals(BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(BatchReturnCode.FEHLER_PARAMETER.getWert(), BatchLauncher.run(
                new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[]{"-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(
                new String[]{"-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                        "-laufErrorSofort", "false", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1"));
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());
    }

    /**
     * Tests a batch that continues to run as daemon (uendlich). The batch is terminated with "kill -s INT pid".
     * Afterwards it must be checked if the message Batch finished was written in the log and the status in the DB is set to aborted.
     * <p>
     * Windows, the batch can be terminated with "taskkill /PID 1234". PID can be taken from the task manager.
     */
    @Test
    @Disabled("Erfordert manuelles 'kill -15' von außen")
    void testShutdown() throws Exception {
        TestBatchLauncher batchLauncher =
                new TestBatchLauncher("/resources/batch/infinite-test-batch-1-config.properties");
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
    @Disabled("Testet ob der Batch mit dem Laufzeitparameter abgebrochen werden kann. Anschließend muss geprüft "
            + "werden, ob im Log die Meldung Batch beendet geschrieben wurde und der Status in der DB auf abgebrochen "
            + "steht.")
    void testLaufzeitParameter() throws Exception {
        TestBatchLauncher batchLauncher =
                new TestBatchLauncher("/resources/batch/infinite-test-batch-1-config.properties");
        assertEquals(BatchReturnCode.FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN.getWert(), batchLauncher
                .starteBatch(BatchStartTyp.START, "/laufzeit_out.xml", new String[]{"-laufzeit", "1"}));
        BatchProtokollTester bpt = new BatchProtokollTester("/laufzeit_out.xml");
        assertTrue(bpt.enthaeltMeldungsId("MAX_LAUFZEIT_UEBERSCHRITTEN"));
    }

    /**
     * Tests if the batch with parameter exception terminates if the runtime parameter is specified without the minutes.
     * Here "-runtime" is the last parameter.
     */
    @Test
    void testLaufzeitParameterOhneMinuten() throws Exception {
        TestBatchLauncher batchLauncher =
                new TestBatchLauncher("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(BatchReturnCode.FEHLER_PARAMETER.getWert(), batchLauncher
                .starteBatch(BatchStartTyp.START, "/laufzeit_out.xml", new String[]{"-laufzeit"}));
    }

    /**
     * Tests if the batch with parameter exception terminates if the runtime parameter is specified without the minutes.
     * The parameter "-laufzeit" is directly followed by another parameter.
     */
    @Test
    void testLaufzeitParameterOhneMinutenIgnoriereTestlauf() throws Exception {
        TestBatchLauncher batchLauncher =
                new TestBatchLauncher("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(BatchReturnCode.FEHLER_PARAMETER.getWert(), batchLauncher.starteBatch(BatchStartTyp.START, "/laufzeit_out.xml",
                new String[]{"-laufzeit", "-ignoriereLauf"}));
    }

    /**
     * Tests if the batch with parameter exception terminates if the runtime parameter is specified without the minutes.
     * The parameter "-laufzeit" is specified as an option at the front.
     */
    @Test
    void testLaufzeitParameterOhneMinutenAlsOption() throws Exception {
        assertEquals(BatchReturnCode.FEHLER_PARAMETER.getWert(), BatchLauncher.run(new String[]{"-start", "-laufzeit", "-cfg",
                "/resources/batch/gesicherter-test-batch-authenticated-ropc-config.properties", "-Batchrahmen.Ergebnisdatei",
                "/testOutput/batchRahmen.out.xml"}));
    }

    /**
     * Tests if the batch with parameter exception aborts if the runtime parameter is specified but the minutes are not numeric.
     */
    @Test
    void testLaufzeitParameterMinutenNichtNumerisch() throws Exception {
        TestBatchLauncher batchLauncher =
                new TestBatchLauncher("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(BatchReturnCode.FEHLER_KONFIGURATION.getWert(), batchLauncher.starteBatch(BatchStartTyp.START, new String[]{"-laufzeit", "ABC"}));
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
    void testGesicherterBatchGutFallRopc() throws Exception {
        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-authenticated-ropc-config.properties"}));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch-1"));
    }

    /**
     * Tests the protection of a batch before processing a set with client credentials.
     */
    @Test
    void testGesicherterBatchGutFallClientCredentials() throws Exception {
        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-authenticated-client-credentials-config.properties"}));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch-1"));
    }

    /**
     * Tests the protection of a batch before processing a set.
     */
    @Test
    void testGesicherterBatchFalscherBenutzer() throws Exception {
        assertEquals(BatchReturnCode.FEHLER_KONFIGURATION.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-wrong-user-config.properties"}));
        assertEquals("abgebrochen", getBatchStatus("batchGesichertWrongUser"));
    }

    /**
     * Tests the protection of a batch before processing a set.
     */
    @Test
    void testGesicherterBatchNichtErforderlicheRolle() throws Exception {
        assertEquals(BatchReturnCode.FEHLER_KONFIGURATION.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-without-roles-config.properties"}));
        // Since the batch has not been started, the status must still be "new".
        assertEquals("neu", getBatchStatus("batchGesichertFalscheRolle"));
    }

    /**
     * Tests the execution of the batch frame with the TestBatchLauncher, which executes the batch in a separate VM.
     */
    @Test
    void testReturnCodeTestBatch() throws Exception {
        TestBatchLauncher batchLauncher =
                new TestBatchLauncher("/resources/batch/returnCode-test-batch-1-config.properties");
        assertEquals(BatchReturnCode.FEHLER_AUSGEFUEHRT.getWert(),
                batchLauncher.starteBatch(BatchStartTyp.START, null));
        assertEquals("beendet", getBatchStatus("returnCodeTestBatch-1"));
    }

    @Test
    void testBatchIdInLoggerContext() {
        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher
                .run(new String[]{"-start", "-cfg", "/resources/batch/basic-test-batch-1-config.properties"}));
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        final String actualBatchId = loggerContext.getProperty("BatchId");
        assertEquals("BatchId ist in Batch LoggerContext gesetzt", "basicTestBatch-1", actualBatchId);
    }

    @Test
    void launchTestBatch() throws IOException {
        String batchConfig = "/resources/batch/basic-test-batch-1-config.properties";
        TestBatchLauncher sut = new TestBatchLauncher(batchConfig);

        BatchStartTyp type = BatchStartTyp.START;

        String[] params = { };

        int returnVal = sut.starteBatch(type, params);

        assertThat(returnVal)
                .isEqualTo(BatchReturnCode.OK.getWert());
    }
}
