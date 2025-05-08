package de.bund.bva.isyfact.batchrahmen.test;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.resetAllRequests;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import de.bund.bva.isyfact.batchrahmen.AnwendungTestConfig;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.config.AbstractOidcProviderTest;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher;
import de.bund.bva.isyfact.security.authentication.ClaimsOnlyOAuth2Token;

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

    @BeforeEach
    public void clearSecurityContext() {
        // the SecurityContext has to be cleared in order to avoid sharing it between different batch runs
        SecurityContextHolder.clearContext();
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

        ClaimsOnlyOAuth2Token token = assertInstanceOf(ClaimsOnlyOAuth2Token.class,
                SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertEquals("basicTestBatch-1", token.getDisplayName());
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
    void testGesicherterBatchGutFallRopc() {
        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-authenticated-ropc-config.properties"}));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch-1"));

        Jwt token = assertInstanceOf(Jwt.class, SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertEquals(getIssuerLocation(), token.getIssuer().toString());
        assertEquals("ropc-testclient-id", token.getClaimAsString("azp"));
    }

    /**
     * Tests the protection of a batch before processing a set with client credentials.
     */
    @Test
    void testGesicherterBatchGutFallClientCredentials() throws Exception {
        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-authenticated-client-credentials-config.properties"}));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch-1"));

        Jwt token = assertInstanceOf(Jwt.class, SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertEquals(getIssuerLocation(), token.getIssuer().toString());
        assertEquals("cc-testclient-id", token.getClaimAsString("azp"));
    }

    /**
     * Tests the protection of a batch before processing a set.
     */
    @Test
    void testGesicherterBatchFalscherBenutzer() throws Exception {
        assertEquals(BatchReturnCode.FEHLER_KONFIGURATION.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-wrong-user-config.properties"}));
        assertEquals("abgebrochen", getBatchStatus("batchGesichertWrongUser"));

        // authentication failed so we expect the batchname to be set in the claim
        ClaimsOnlyOAuth2Token token = assertInstanceOf(ClaimsOnlyOAuth2Token.class,
                SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertEquals("batchGesichertWrongUser", token.getDisplayName());
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
        assertEquals("basicTestBatch-1", actualBatchId, "BatchId ist in Batch LoggerContext gesetzt");
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

    /**
     * Tests initial batch authentication call for the client credentials flow.
     */
    @Test
    void testBatchInitialAuthenticationWithClientCredentials() {
        // reset WireMock request call count
        resetAllRequests();

        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/basic-test-batch-authenticated-client-credentials-config.properties"}));

        // should be called 1 time for initial authentication, because the token is still valid for each batch step
        verify(1, postRequestedFor(urlMatching(ISSUER_PATH + ".*")));

        Jwt token = assertInstanceOf(Jwt.class, SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertEquals(getIssuerLocation(), token.getIssuer().toString());
        assertEquals("cc-testclient-id", token.getClaimAsString("azp"));
    }

    /**
     * Tests initial batch authentication call for the password flow.
     */
    @Test
    void testBatchInitialAuthenticationWithPassword() {
        // reset WireMock request call count
        resetAllRequests();

        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/basic-test-batch-authenticated-ropc-config.properties"}));

        // should be called 1 time for initial authentication, because the token is still valid for each batch step
        verify(1, postRequestedFor(urlMatching(ISSUER_PATH + ".*")));

        Jwt token = assertInstanceOf(Jwt.class, SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertEquals(getIssuerLocation(), token.getIssuer().toString());
        assertEquals("ropc-testclient-id", token.getClaimAsString("azp"));
    }

    /**
     * Tests batch authentication calls for the password flow.
     */
    @Test
    void testBatchAuthenticationWithPassword() {
        // reset WireMock request call count
        resetAllRequests();

        assertEquals(BatchReturnCode.OK.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/basic-authentication-test-batch-authenticated-ropc-config.properties"}));

        // should be called 11 (1 + 10 + 1) times, because oauth2MinimumTokenValidity is set to the value of
        // tokenLifespan, meaning re-authentication should be done for each step of the batch
        // 1x initialisiere, 10x verarbeite, 1x beende
        verify(12, postRequestedFor(urlMatching(ISSUER_PATH + ".*")));

        Jwt token = assertInstanceOf(Jwt.class, SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertEquals(getIssuerLocation(), token.getIssuer().toString());
        assertEquals("ropc-testclient-id", token.getClaimAsString("azp"));
    }

    /**
     * Tests that an exception is thrown when the batch property oauth2MinimumTokenValidity is set to a negative number.
     */
    @Test
    void testBatchInvalidOAuth2MinimumTokenValidityProperty() {
        assertEquals(BatchReturnCode.FEHLER_KONFIGURATION.getWert(), BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/basic-authentication-test-batch-invalid-minimum-token-validity-config.properties",
                "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        BatchProtokollTester bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.enthaeltFehler("BAT110", "-10"));
    }

}
