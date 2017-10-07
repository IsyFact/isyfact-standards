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
 * Entwicklertests des Batch-Rahmens.
 *
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@ContextConfiguration(locations = { "/resources/anwendung/jpa.xml", "/resources/anwendung/querschnitt.xml" })
public class BatchrahmenTest implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * Aktiviert/Deaktiviert den Laufzeit-Test {@link #testLaufzeitParameter()}.
     */
    private static final boolean DO_TEST_LAUFZEIT = false;

    /**
     * Aktiviert/Deaktiviert den Shutdown-Test {@link #testShutdown()}, erfordert manuelles 'kill -15' von
     * außen.
     */
    private static final boolean DO_TEST_SHUTDOWN = false;

    private BatchStatusDao dao;

    /** Datei für das Batch-Protokoll. Wird in {@link #init()} gesetzt. */
    private static String ERGEBNIS_DATEI;

    private TransactionTemplate txTemplate;

    /**
     * Testet die Ausführung des Batchrahmens mit dem TestBatchLauncher, der den Batch in einer eigenen VM
     * ausführt.
     * @throws IOException
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
     * Testet die Ausführung des Batchrahmens.
     * @throws IOException
     */
    @Test
    public void testBatchrahmen() throws IOException {
        assertEquals(0, BatchLauncher
            .run(new String[] { "-start", "-cfg", "/resources/batch/basic-test-batch-1-config.properties" }));
        assertEquals("beendet", getBatchStatus("basicTestBatch-1").getBatchStatus());
    }

    /**
     * Testet die Behandlung des Status-Satzes, wenn der Batch mit einem Fehler in der Initialisierung
     * abbricht. Der Status muss bei einem Abbruch während der Initialisierung auf "beendet" stehen.
     * @throws IOException
     */
    @Test
    public void testFehlerInInit() throws IOException {
        assertEquals(2, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-initError", "true" }));
        assertEquals("neu", getBatchStatus("errorTestBatch-1").getBatchStatus());

        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-initError", "false" }));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1").getBatchStatus());

        assertEquals(2, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-initError", "true" }));
        // TODO: batchStatus kann nicht "beendet" sein, da "-initError" true ist und deswegen eine Exception
        // geworfen wird.
        assertEquals("beendet", getBatchStatus("errorTestBatch-1").getBatchStatus());

        // Test nach Abbruch
        assertEquals(2, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-laufError", "true" }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());

        assertEquals(2, BatchLauncher.run(new String[] { "-start", "-ignoriereRestart", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-initError", "true" }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
    }

    /**
     * Testet, dass nach einem Abbruch der Batch nur mit ignoriereLauf neu gestartet werden kann.
     * @throws IOException
     */
    @Test
    public void testIgnoriereLauf() throws IOException {
        assertEquals(2, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-laufError", "true" }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());

        assertEquals(3, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-laufError", "true" }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());

        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-ignoriereRestart", "-cfg",
            "/resources/batch/error-test-batch-1-config.properties", "-laufError", "false" }));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1").getBatchStatus());
    }

    /**
     * Testet, dass nach einem Abbruch der Batch mit Restart gestartet werden kann.
     * @throws IOException
     */
    @Test
    // @Ignore("TestBatchLauncher startet Batch in eigener VM, dadurch NPE beim Test.")
    public void testRestart() throws IOException {
        assertEquals(2,
            BatchLauncher
                .run(new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                    "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        BatchProtokollTester bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(3,
            BatchLauncher
                .run(new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                    "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(2,
            BatchLauncher.run(
                new String[] { "-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                    "-laufError", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        assertEquals(0,
            BatchLauncher.run(
                new String[] { "-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                    "-laufError", "false", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        // Ab hier Prüfung, dass das obige Verhalten auch greift, wenn der Abbruch nach der Initialisierung
        // aber vor Verarbeitung eines Satzes erfolgt (0 Sätze verarbeitet).
        assertEquals(2,
            BatchLauncher
                .run(new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                    "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(3,
            BatchLauncher
                .run(new String[] { "-start", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                    "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertFalse(bpt.isStartmodusRestart());

        assertEquals(2,
            BatchLauncher.run(
                new String[] { "-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                    "-laufErrorSofort", "true", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("abgebrochen", getBatchStatus("errorTestBatch-1").getBatchStatus());
        bpt = new BatchProtokollTester(ERGEBNIS_DATEI);
        assertTrue(bpt.isStartmodusRestart());

        assertEquals(0,
            BatchLauncher.run(
                new String[] { "-restart", "-cfg", "/resources/batch/error-test-batch-1-config.properties",
                    "-laufErrorSofort", "false", "-Batchrahmen.Ergebnisdatei", ERGEBNIS_DATEI }));
        assertEquals("beendet", getBatchStatus("errorTestBatch-1").getBatchStatus());
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
     *
     * @throws IOException
     */
    @Test
    public void testShutdown() throws IOException {
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
     * Testet ob der Batch mit dem Laufzeitparameter abgebrochen werden kann. Anschließend muss geprüft
     * werden, ob im Log die Meldung Batch beendet geschrieben wurde und der Status in der DB auf abgebrochen
     * steht.
     *
     * @throws IOException
     */
    @Test
    public void testLaufzeitParameter() throws IOException {
        if (!DO_TEST_LAUFZEIT) {
            return;
        }
        System.out.println("Manueller Test, siehe JavaDoc");
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/infinite-test-batch-1-config.properties");
        assertEquals(144, batchLauncher.starteBatch(BatchStartTyp.START, "/laufzeit_out.xml",
            new String[] { "-laufzeit", "1" }));
        BatchProtokollTester bpt = new BatchProtokollTester("/laufzeit_out.xml");
        assertTrue(bpt.enthaeltMeldungsId("MAX_LAUFZEIT_UEBERSCHRITTEN"));
    }

    /**
     * Testet ob der Batch mit Parameter-Exception abbricht, wenn der Laufzeit-Parameter ohne die Minuten
     * angegeben ist. Dabei ist "-laufzeit" der letzte Parameter.
     *
     * @throws IOException
     */
    @Test
    public void testLaufzeitParameterOhneMinuten() throws IOException {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(3, batchLauncher.starteBatch(BatchStartTyp.START, "/laufzeit_out.xml",
            new String[] { "-laufzeit" }));
    }

    /**
     * Testet ob der Batch mit Parameter-Exception abbricht, wenn der Laufzeit-Parameter ohne die Minuten
     * angegeben ist. Der Parameter "-laufzeit" wird direkt mit einem anderen Parameter gefolgt.
     *
     * @throws IOException
     */
    @Test
    public void testLaufzeitParameterOhneMinutenIgnoriereTestlauf() throws IOException {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(3, batchLauncher.starteBatch(BatchStartTyp.START, "/laufzeit_out.xml",
            new String[] { "-laufzeit", "-ignoriereLauf" }));
    }

    /**
     * Testet ob der Batch mit Parameter-Exception abbricht, wenn der Laufzeit-Parameter ohne die Minuten
     * angegeben ist. Der Parameter "-laufzeit" vorne als Option angegeben.
     *
     * @throws IOException
     */
    @Test
    public void testLaufzeitParameterOhneMinutenAlsOption() throws Exception {
        assertEquals(3,
            BatchLauncher.run(new String[] { "-start", "-laufzeit", "-cfg",
                "/resources/batch/gesicherter-test-batch-1-config.properties", "-Batchrahmen.Ergebnisdatei",
                "/testOutput/batchRahmen.out.xml" }));
    }

    /**
     * Testet ob der Batch mit Parameter-Exception abbricht, wenn der Laufzeit-Parameter angegeben ist, aber
     * die Minuten nicht numerisch sind.
     *
     * @throws IOException
     */
    @Test
    public void testLaufzeitParameterMinutenNichtNumerisch() throws IOException {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/basic-test-batch-1-config.properties");
        assertEquals(4, batchLauncher.starteBatch(BatchStartTyp.START, new String[] { "-laufzeit", "ABC" }));
    }

    /**
     * Liest den BatchStatus mit der angegebenen Id in einer eigenen Transaktion.
     * @param batchId
     *            Id als Schlüssel für den BatchStatus
     * @return Den BatchStatus
     */
    private BatchStatus getBatchStatus(final String batchId) {
        return this.txTemplate.execute(new TransactionCallback<BatchStatus>() {
            @Override
            public BatchStatus doInTransaction(TransactionStatus status) {
                return BatchrahmenTest.this.dao.leseBatchStatus(batchId);
            }
        });
    }

    @Before
    public void init() throws Exception {
        final EntityManagerFactory emf =
            (EntityManagerFactory) this.applicationContext.getBean("entityManagerFactory");
        PlatformTransactionManager txManager =
            (PlatformTransactionManager) this.applicationContext.getBean("transactionManager");
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
     * Testet die Absicherung eines Batches vor der Verarbeitung eines Satzes.
     * @throws IOException
     * @throws AutorisierungTechnicalException
     * @throws AuthentifizierungFehlgeschlagenException
     */
    @Test
    public void testGesicherterBatchGutFall() throws Exception {
        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch-1-config.properties" }));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch-1").getBatchStatus());
    }

    /**
     * Testet die Absicherung eines Batches vor der Verarbeitung eines Satzes.
     * @throws IOException
     * @throws AutorisierungTechnicalException
     * @throws AuthentifizierungFehlgeschlagenException
     */
    @Test
    public void testGesicherterBatchLoginNichtMoeglich() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch-2-config.properties" }));
        assertEquals("abgebrochen", getBatchStatus("gesicherterTestBatch-2").getBatchStatus());
    }

    /**
     * Testet die Absicherung eines Batches vor der Verarbeitung eines Satzes.
     * @throws IOException
     * @throws AutorisierungTechnicalException
     * @throws AuthentifizierungFehlgeschlagenException
     */
    @Test
    public void testGesicherterBatchNichtErforderlicheRolle() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch-3-config.properties" }));
        assertEquals("abgebrochen", getBatchStatus("gesicherterTestBatch-3").getBatchStatus());
    }

    /**
     * Testet die Absicherung eines Batches vor der Initialisierung.
     * @throws IOException
     * @throws AutorisierungTechnicalException
     * @throws AuthentifizierungFehlgeschlagenException
     */
    @Test
    public void testGesicherterBatch2GutFall() throws Exception {
        assertEquals(0, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch2-1-config.properties" }));
        assertEquals("beendet", getBatchStatus("gesicherterTestBatch2-1").getBatchStatus());
    }

    /**
     * Testet die Absicherung eines Batches vor der Initialisierung.
     * @throws IOException
     * @throws AutorisierungTechnicalException
     * @throws AuthentifizierungFehlgeschlagenException
     */
    @Test
    public void testGesicherterBatch2LoginNichtMoeglich() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch2-2-config.properties" }));
        // Da der Batch nicht gestartet ist, muss der Status noch auf neu stehen
        assertEquals("neu", getBatchStatus("gesicherterTestBatch2-2").getBatchStatus());
    }

    /**
     * Testet die Absicherung eines Batches vor der Initialisierung.
     * @throws IOException
     * @throws AutorisierungTechnicalException
     * @throws AuthentifizierungFehlgeschlagenException
     */
    @Test
    public void testGesicherterBatch2NichtErforderlicheRolle() throws Exception {
        assertEquals(4, BatchLauncher.run(new String[] { "-start", "-cfg",
            "/resources/batch/gesicherter-test-batch2-3-config.properties" }));
        // Da der Batch nicht gestartet ist, muss der Status noch auf neu stehen
        assertEquals("neu", getBatchStatus("gesicherterTestBatch2-3").getBatchStatus());
    }

    /**
     * Testet die Ausführung des Batchrahmens mit dem TestBatchLauncher, der den Batch in einer eigenen VM
     * ausführt.
     * @throws IOException
     */
    @Test
    @Ignore("TestBatchLauncher startet Batch in eigener VM, dadurch NPE beim Test.")
    public void testReturnCodeTestBatch() throws IOException {
        TestBatchLauchner batchLauncher =
            new TestBatchLauchner("/resources/batch/returnCode-test-batch-1-config.properties");
        assertEquals(BatchReturnCode.FEHLER_AUSGEFUEHRT.getWert(),
            batchLauncher.starteBatch(BatchStartTyp.START, null));
        assertEquals("beendet", getBatchStatus("returnCodeTestBatch-1").getBatchStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
