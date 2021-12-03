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
package de.bund.bva.pliscommon.batchrahmen.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.pliscommon.batchrahmen.core.launcher.BatchLauncher;

/**
 * This class allows batches to be started easily from JUnit tests.
 *
 * Example:
 *
 * <code><pre>
 * public void testBatch1() {
 *   TestBatchLauchner batchLauncher = new TestBatchLauchner("/resources/batch/batch-1-config.properties");
 *   assertEquals(0, batchLauncher.starteBatch(BatchStartTyp.START, "/batch-1_out.xml", null ));
 * }
 * </pre></code>
 *
 */
public class TestBatchLauchner {

    /** The Logger. */
    public static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestBatchLauchner.class);

    /** Property configuration of the batch to be started. */
    private String batchKonfig;

    /**
     * Creates a BatchLauncher for the specified batch. The JRE and the class path are formed from the
     * current JRE.
     * @param batchKonfig
     *            Configuration file of the batch
     */
    public TestBatchLauchner(String batchKonfig) {
        this.batchKonfig = batchKonfig;
    }

    /**
     * Starts the {@link BatchLauncher} with the given parameters. The JRE and the class path are formed from
     * the current JRE.
     * @param batchLauncherParams
     *            Parameters for the BatchLauncher
     * @param batchParameter
     *            Parameters for the Batch
     * @return The return code of the batch process.
     * @throws IOException
     *             If the batch could not be started.
     */
    private int starteBatch(String[] batchLauncherParams, String[] batchParameter) throws IOException {
        // Use the Maven Surefire classpath, if set.
        String classpath = System.getProperty("surefire.test.class.path");
        if (classpath == null) {
            classpath = System.getProperty("java.class.path");
        }
        LOG.debug("Classpath: {}", classpath);
        String javaHome = System.getProperty("java.home");
        LOG.debug("Java Home: {}", javaHome);
        List<String> params = new ArrayList<String>(3 + batchLauncherParams.length);
        params.add(javaHome + "/bin/java"); // Java-Command
        // Retrieve all Java VM Parameters
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        for (String vmargs : arguments) {
            params.add(vmargs);
        }
        params.add("-classpath");
        params.add(classpath);
        params.add(BatchLauncher.class.getName()); // Main-Class
        for (String batchParam : batchLauncherParams) {
            params.add(batchParam);
        }
        if (batchParameter != null) {
            for (String batchParam : batchParameter) {
                params.add(batchParam);
            }
        }
        ProcessBuilder processBuilder = new ProcessBuilder(params);
        processBuilder.redirectErrorStream(true);
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001, "Starte Batch...");
        String cmdLine = "";
        for (String param : params) {
            cmdLine += param + " ";
        }
        LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
            "Die Kommandozeile ist:\n{}", cmdLine);
        Process batchProcess = processBuilder.start();
        BufferedReader processIn = null;
        try {
            processIn = new BufferedReader(new InputStreamReader(batchProcess.getInputStream(), StandardCharsets.UTF_8));
            final BufferedReader in = processIn;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            String input = in.readLine();
                            if (input != null) {
                                System.out.println(input);
                            }
                        }
                    } catch (IOException ex) {
                        // Thread terminated.
                    }
                }
            });
            t.start();
            try {
                int returnCode = batchProcess.waitFor();
                LOG.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001, "Batch beendet...");
                return returnCode;
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return -1;
            }
        } finally {
            if (processIn != null) {
                processIn.close();
            }
        }
    }

    /**
     * Starts a batch.
     * @param startTyp
     *            Start mode
     * @param parameter
     *            Call parameters of the batch
     * @return The return code of the batch process.
     * @throws IOException
     *             If the batch could not be started.
     */
    public int starteBatch(BatchStartTyp startTyp, String[] parameter) throws IOException {
        return starteBatch(new String[] { getStartTypParam(startTyp), "-cfg", this.batchKonfig }, parameter);
    }

    /**
     * Starts a batch.
     * @param startTyp
     *            Start mode
     * @param ergebnisProtokoll
     *            Path in which the result log is to be written.
     * @param parameter
     *            Call parameters of the batch
     * @return The return code of the batch process.
     * @throws IOException
     *             If the batch could not be started.
     */
    public int starteBatch(BatchStartTyp startTyp, String ergebnisProtokoll, String[] parameter)
        throws IOException {
        return starteBatch(new String[] { getStartTypParam(startTyp), "-cfg", this.batchKonfig,
            "-Batchrahmen.Ergebnisdatei", ergebnisProtokoll }, parameter);
    }

    /**
     * Determines the command line parameters for the specified start type.
     * @param startTyp
     *            Start type
     * @return Command line parameters for the start type.
     */
    private String getStartTypParam(BatchStartTyp startTyp) {
        switch (startTyp) {
        case RESTART:
            return "-restart";
        case START:
            return "-start";
        default:
            return null;
        }
    }
}
