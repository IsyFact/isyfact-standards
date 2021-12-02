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
package de.bund.bva.pliscommon.batchrahmen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.pliscommon.batchrahmen.core.launcher.BatchLauncher;
import de.bund.bva.pliscommon.batchrahmen.persistence.rahmen.BatchStatus;
import de.bund.bva.pliscommon.batchrahmen.persistence.rahmen.BatchStatusDao;
import de.bund.bva.pliscommon.batchrahmen.test.BatchProtokollTester;
import de.bund.bva.pliscommon.batchrahmen.test.TestBatchLauchner;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.AutorisierungTechnicalException;

/**
 * Developer testing of the batch framework.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@ContextConfiguration(locations = {"/resources/anwendung/jpa.xml", "/resources/anwendung/querschnitt.xml"})
public class BatchrahmenTest {

    /**
     * Enables/disables the runtime test {@link #testLaufzeitParameter()}.
     */
    private static final boolean DO_TEST_LAUFZEIT = false;

    /**
     * Enables/disables shutdown test {@link #testShutdown()}, requires manual 'kill -15' from outside.
     */
    private static final boolean DO_TEST_SHUTDOWN = false;

    private BatchStatusDao dao;

    /**
     * File for the batch log. Set in {@link #init()}.
     */
    private static String ERGEBNIS_DATEI;

    private TransactionTemplate txTemplate;

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private PlatformTransactionManager txManager;

    @Before
    public void init() {
        this.dao = new BatchStatusDao(emf);
        TransactionDefinition def =
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.txTemplate = new TransactionTemplate(txManager, def);
        this.txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
                em.flush();
                em.clear();
                em.createNativeQuery("delete from BATCHSTATUS").executeUpdate();
            }
        });

        try {
            ERGEBNIS_DATEI = new File(
                    BatchrahmenTest.class.getResource("/resources/batch/ausgabe/ergebnisdatei.xml").toURI())
                    .getAbsolutePath();

        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the execution of the batch frame using the TestBatchLauncher, which runs the batch in its own VM.
     */
    @Test
    @Ignore("TestBatchLauncher startet Batch in eigener VM, dadurch NPE beim Test.")
    public void testBatchrahmenMitTestBatchLauncher() throws IOException {
        TestBatchLauchner batchLauncher =
                new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(0, batchLauncher.starteBatch(BatchStartTyp.START, null));
        assertEquals("beendet", getBatchStatus("basicTestBatch-1").getBatchStatus());
    }

    /**
     * Tests the execution of the batch frame.
     */
    @Test
    public void testBatchrahmen() throws IOException {
        assertEquals(0, BatchLauncher
                .run(new String[]{"-start", "-cfg", "/resources/batch/basic-test-batch-1-config.properties"}));
        assertEquals("beendet", getBatchStatus("basicTestBatch-1").getBatchStatus());
    }

    /**
     * Tests the handling of the status record when the batch terminates with an error in the initialization.
     * The status must be set to "finished" in the event of an abort during initialization.
     */
    @Test
    public void testFehlerInInit() throws IOException {
        assertEquals(2, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/error-test-batch-1-config.properties", "-initError", "true"}));
        assertEquals("neu", getBatchStatus("errorTestBatch-1").getBatchStatus());

        assertEquals(0, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/error-test-batch-1-config.properties", "-initError", "false"}));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1").getBatchStatus());

        assertEquals(2, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/error-test-batch-1-config.properties", "-initError", "true"}));
        // TODO: batchStatus cannot be "finished", because "-initError" is {@code true} and therefore an exception is thrown.
        assertEquals("beendet", getBatchStatus("errorTestBatch-1").getBatchStatus());

        // Test after abort
        assertEquals(2, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/error-test-batch-1-config.properties", "-laufError", "true"}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());

        assertEquals(2, BatchLauncher.run(new String[]{"-start", "-ignoriereRestart", "-cfg",
                "/resources/batch/error-test-batch-1-config.properties", "-initError", "true"}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
    }

    /**
     * Tests that after a termination the batch can only be restarted with ignoreRun.
     */
    @Test
    public void testIgnoriereLauf() throws IOException {
        assertEquals(2, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/error-test-batch-1-config.properties", "-laufError", "true"}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());

        assertEquals(3, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/error-test-batch-1-config.properties", "-laufError", "true"}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());

        assertEquals(0, BatchLauncher.run(new String[]{"-start", "-ignoriereRestart", "-cfg",
                "/resources/batch/error-test-batch-1-config.properties", "-laufError", "false"}));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1").getBatchStatus());
    }

    /**
     * Tests that after a termination the batch can be started with Restart.
     */
    @Test
    // @Ignore("TestBatchLauncher startet Batch in eigener VM, dadurch NPE beim Test.")
    public void testRestart() throws IOException {
        assertEquals(2,
                BatchLauncher
                        .run(new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                                "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        BatchProtokollTester bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(3,
                BatchLauncher
                        .run(new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                                "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(2,
                BatchLauncher.run(
                        new String[]{"-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                                "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        assertEquals(0,
                BatchLauncher.run(
                        new String[]{"-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                                "-laufError", "false", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        // From here on check that the above behavior also applies if the abort occurs after initialization but before
        // processing of a record (0 records processed).
        assertEquals(2,
                BatchLauncher
                        .run(new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                                "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(3,
                BatchLauncher
                        .run(new String[]{"-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                                "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(2,
                BatchLauncher.run(
                        new String[]{"-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                                "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        assertEquals(0,
                BatchLauncher.run(
                        new String[]{"-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                                "-laufErrorSofort", "false", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI}));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());
    }

    /**
     * Tests a batch that continues to run as a daemon. The batch is terminated with "kill -s INT pid" in order.
     * Then check whether the message Batch finished was written in the log and the status in the DB is set to aborted.
     * <p>
     * Under Windows the batch can be terminated with "taskkill /PID 1234". PID can be taken from the task manager.
     */
    @Test
    public void testShutdown() throws Exception {
        if (!DO_TEST_SHUTDOWN) {
            return;
        }
        System.out.println("Manueller Test, siehe JavaDoc");
        TestBatchLauchner batchLauncher =
                new TestBatchLauchner("/resources/batch/infinite-test-batch-1-config.properties");
        batchLauncher.starteBatch(BatchStartTyp.START, "/batch-2_out.xml", null);
        // assertEquals(143, batchLauncher.starteBatch(BatchStartTyp.START, "/batch-2_out.xml", null));
        BatchProtokollTester bpt = new BatchProtokollTester("/batch-2_out.xml");
        assertTrue(bpt.enthaeltMeldungsId("BENUTZERABBRUCH"));
    }

    /**
     * Tests whether the batch can be terminated with the runtime parameter.
     * Then check whether the message Batch finished was written in the log and the status in the DB is set to aborted.
     */
    @Test
    public void testLaufzeitParameter() throws Exception {
        if (!DO_TEST_LAUFZEIT) {
            return;
        }
        System.out.println("Manueller Test, siehe JavaDoc");
        TestBatchLauchner batchLauncher =
                new TestBatchLauchner("/resources/batch/infinite-test-batch-1-config.properties");
        assertEquals(144, batchLauncher.starteBatch(BatchStartTyp.START, "/laufzeit_out.xml",
                new String[]{"-laufzeit", "1"}));
        BatchProtokollTester bpt = new BatchProtokollTester("/laufzeit_out.xml");
        assertTrue(bpt.enthaeltMeldungsId("MAX_LAUFZEIT_UEBERSCHRITTEN"));
    }

    /**
     * Tests if the batch terminates with a parameter exception if the runtime parameter is specified without the minutes.
     * Here "-runtime" is the last parameter.
     */
    @Test
    public void testLaufzeitParameterOhneMinuten() throws Exception {
        TestBatchLauchner batchLauncher =
                new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(3, batchLauncher.starteBatch(BatchStartTyp.START, "/laufzeit_out.xml",
                new String[]{"-laufzeit"}));
    }

    /**
     * Tests if the batch terminates with a parameter exception if the runtime parameter is specified without the minutes.
     * The parameter "-runtime" is directly followed by another parameter.
     */
    @Test
    public void testLaufzeitParameterOhneMinutenIgnoriereTestlauf() throws Exception {
        TestBatchLauchner batchLauncher =
                new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(3, batchLauncher.starteBatch(BatchStartTyp.START, "/laufzeit_out.xml",
                new String[]{"-laufzeit", "-ignoriereLauf"}));
    }

    /**
     * Tests whether the batch terminates with a parameter exception if the runtime parameter is specified without the minutes.
     * The parameter "-runtime" in the front is specified as an option.
     */
    @Test
    public void testLaufzeitParameterOhneMinutenAlsOption() throws Exception {
        assertEquals(3,
                BatchLauncher.run(new String[]{"-start", "-laufzeit", "-cfg",
                        "/resources/batch/gesicherter-test-batch-1-config.properties", "-Batchrahmen.Ergebnisdatei",
                        "/testOutput/batchRahmen.out.xml"}));
    }

    /**
     * Tests if the batch terminates with parameter exception if the runtime parameter is specified but the minutes are not numeric.
     */
    @Test
    public void testLaufzeitParameterMinutenNichtNumerisch() throws Exception {
        TestBatchLauchner batchLauncher =
                new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(4, batchLauncher.starteBatch(BatchStartTyp.START, new String[]{"-laufzeit", "ABC"}));
    }

    /**
     * Reads the BatchStatus with the specified Id in a separate transaction.
     *
     * @param batchId Id as key for the BatchStatus
     * @return The BatchStatus
     */
    private BatchStatus getBatchStatus(final String batchId) {
        return this.txTemplate.execute(new TransactionCallback<BatchStatus>() {
            @Override
            public BatchStatus doInTransaction(TransactionStatus status) {
                return BatchrahmenTest.this.dao.leseBatchStatus(batchId);
            }
        });
    }

    /**
     * Tests the protection of a batch before processing a record.
     */
    @Test
    public void testGesicherterBatchGutFall() throws Exception {
        assertEquals(0, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-1-config.properties"}));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch-1").getBatchStatus());
    }

    /**
     * Tests the protection of a batch before processing a record.
     */
    @Test
    public void testGesicherterBatchLoginNichtMoeglich() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-2-config.properties"}));
        assertEquals("abgebrochen", getBatchStatus("gesicherterTestBatch-2").getBatchStatus());
    }

    /**
     * Tests the protection of a batch before processing a record.
     */
    @Test
    public void testGesicherterBatchNichtErforderlicheRolle() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-3-config.properties"}));
        assertEquals("abgebrochen", getBatchStatus("gesicherterTestBatch-3").getBatchStatus());
    }

    /**
     * Tests the protection of a batch before initialization.
     */
    @Test
    public void testGesicherterBatch2GutFall() throws Exception {
        assertEquals(0, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch2-1-config.properties"}));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch2-1").getBatchStatus());
    }

    /**
     * Tests the protection of a batch before initialization.
     */
    @Test
    public void testGesicherterBatch2LoginNichtMoeglich() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch2-2-config.properties"}));
        // Since the batch has not been started, the status must still be set to new.
        assertEquals("neu", getBatchStatus("gesicherterTestBatch2-2").getBatchStatus());
    }

    /**
     * Tests the protection of a batch before initialization.
     */
    @Test
    public void testGesicherterBatch2NichtErforderlicheRolle() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/gesicherter-test-batch2-3-config.properties"}));
        // Since the batch has not been started, the status must still be set to new.
        assertEquals("neu", getBatchStatus("gesicherterTestBatch2-3").getBatchStatus());
    }

    /**
     * Tests the execution of the batch frame using the TestBatchLauncher, which runs the batch in its own VM.
     */
    @Test
    @Ignore("TestBatchLauncher startet Batch in eigener VM, dadurch NPE beim Test.")
    public void testReturnCodeTestBatch() throws Exception {
        TestBatchLauchner batchLauncher =
                new TestBatchLauchner("/resources/batch/returnCode-test-batch-1-config.properties");
        assertEquals(BatchReturnCode.FEHLER_AUSGEFUEHRT.getWert(),
                batchLauncher.starteBatch(BatchStartTyp.START, null));
        assertEquals("beendet", getBatchStatus("returnCodeTestBatch-1").getBatchStatus());
    }
}
