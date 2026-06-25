package de.bund.bva.isyfact.batchrahmen.core.launcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;

import com.nimbusds.oauth2.sdk.util.StringUtils;

import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.config.BatchSecurityConfiguration;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenException;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenInitialisierungException;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenMaxWiederholungenException;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenProtokollException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.isyfact.batchrahmen.core.protokoll.DefaultBatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * This class starts a batch with the transferred configuration.
 * The configuration is done via command line arguments und a property file.
 * <p>
 * The processing logic is divided into a {@link Batchrahmen} and an executing bean ({@code Ausführungsbean}).
 * See the <em>Detailkonzept Batch</em> of the <em>Migrationsstufe 1</em>.
 * <p>
 * Any further command line parameters and properties can be entered.
 * The command line parameters are added to the properties and overwrite them, if necessary, before they are passed on
 * to the {@link Batchrahmen}-Bean.
 * The {@link Batchrahmen}-Bean forwards the complete configuration to the executing bean, which can use it for
 * configuration.
 *
 * @see Batchrahmen
 */
public class BatchLauncher {

    /**
     * The configuration for Batch-Rahmen.
     */
    private final BatchKonfiguration rahmenKonfiguration;

    /**
     * The protocol, for storing messages und statistics of Batch-Ausfuehrung.
     */
    private final BatchErgebnisProtokoll protokoll;

    /**
     * Main method for starting batch. This method calls the method {@link #start(String[])} which return its ReturnCode
     * via Runtime.getRuntime().exit().
     * Prefer using {@link #start(String[])} when calling programmatically.
     *
     * @param args command line parameters.
     */
    public static void main(String[] args) {
        IsyLogger log = IsyLoggerFactory.getLogger(BatchLauncher.class);
        log.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                "Der Aufruf der main Methode des BatchLaunchers ist veraltet und wird in " +
                        "zukünftigen Versionen entfernt. Stattdessen kann BatchLauncher.start verwendet werden.");
        start(args);
    }

    /**
     * Method for starting batch. This method calls the method {@link #run(String[])} and returns its ReturnCode via System.exit().
     *
     * @param args command line parameters.
     */
    public static void start(String[] args) {
        Runtime.getRuntime().exit(run(args));
    }

    /**
     * Starts batch. For configuration see class comment.
     *
     * @param args command line parameters. For description see class comment.
     * @return return code of batch.
     */
    public static int run(final String[] args) {
        IsyLogger log = null;
        BatchKonfiguration rahmenKonfiguration;
        DefaultBatchErgebnisProtokoll protokoll = null;
        String ergebnisDatei;
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
            returnCode = bestimmeReturnCode(protokoll);
        } catch (BatchAusfuehrungsException ex) {
            protokolliereFehler(log, protokoll, ex);
            if (ex.getReturnCode() != null) {
                returnCode = ex.getReturnCode();
            }
        } catch (BatchrahmenMaxWiederholungenException ex) {
            if (log != null) {
                log.info(LogKategorie.JOURNAL, ex.getAusnahmeId(), ex.getMessage());
            }
            returnCode = ex.getReturnCode();
        } catch (BatchrahmenException ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = ex.getReturnCode();
        } catch (AuthenticationException | AccessDeniedException | ClientAuthorizationException ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = BatchReturnCode.FEHLER_KONFIGURATION;
        } catch (Throwable ex) {
            protokolliereFehler(log, protokoll, ex);
        } finally {
            if (protokoll != null) {
                protokoll.setReturnCode(returnCode);
                protokoll.batchEnde();
            }
        }
        return returnCode.getWert();
    }

    /**
     * Determines the return code based on the batch result protocol after successful execution.
     * Checks in order: max runtime exceeded, error messages present, manual abort, protocol code, OK.
     *
     * @param protokoll the batch result protocol
     * @return the appropriate {@link BatchReturnCode}
     */
    private static BatchReturnCode bestimmeReturnCode(DefaultBatchErgebnisProtokoll protokoll) {
        if (protokoll.isMaximaleLaufzeitUeberschritten()) {
            return BatchReturnCode.FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN;
        }
        if (protokoll.enthaeltFehlerNachrichten()) {
            return BatchReturnCode.FEHLER_AUSGEFUEHRT;
        }
        if (protokoll.isBatchAbgebrochen()) {
            return BatchReturnCode.FEHLER_MANUELLER_ABBRUCH;
        }
        BatchReturnCode protokollCode = protokoll.getReturnCode();
        return protokollCode != null ? protokollCode : BatchReturnCode.OK;
    }

    private static void protokolliereFehler(IsyLogger log, BatchErgebnisProtokoll protokoll, Throwable ex) {
        String nachricht = exceptionToString(ex);
        System.err.println(nachricht);
        if (log != null) {
            log.error(BatchRahmenEreignisSchluessel.EPLBAT00001, "Fehler bei der Batchausführung.", ex);
        } else {
            ex.printStackTrace(System.err);
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
                System.err.println("Die Fehlermeldung " + protokollEx
                        + " konnte nicht in das Ergebnisprotokoll geschrieben werden.");
            }
        }
    }

    /**
     * Converts an exception including a stack trace into a string.
     *
     * @param t Exception
     * @return String including stack trace without line breaks.
     */
    protected static String exceptionToString(Throwable t) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(byteArrayOutputStream, false, StandardCharsets.UTF_8)) {
            printStream.println(t.getClass().getSimpleName() + ": " + t.getMessage());
            t.printStackTrace(printStream);
            printStream.println();
            return byteArrayOutputStream.toString(StandardCharsets.UTF_8).replaceAll("\\r?\\n", " | ");
        } catch (IOException e) {
            return t.getClass().getSimpleName() + ": " + t.getMessage();
        }
    }

    /**
     * Initializes Logback with the log configuration which was specified in the property file with key
     * {@link KonfigurationSchluessel # PROPERTY_BATCHRAHMEN_LOGBACK_CONF}.
     * /config/logback-batch.xml is used as default.
     *
     * @param konfiguration batch configuration.
     * @throws JoranException If the Logger could not be configured.
     */
    private static void initialisiereLogback(BatchKonfiguration konfiguration) throws JoranException {
        String propertyFile =
                konfiguration.getAsString(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_LOGBACK_CONF,
                        "/config/logback-batch.xml");
        String commandLinePath =
                konfiguration.getAsString(KonfigurationSchluessel.KOMMANDO_PARAM_LOGBACK_KONFIGURATION,
                        null);

        // 1. logback-batch.xml in filesystem
        File file = commandLinePath != null ? new File(commandLinePath) : null;
        boolean isFile = file != null && file.exists();

        // 2. logback-batch.xml in classpath
        URL configLocation = isFile ? null : BatchLauncher.class.getResource(propertyFile);

        if (!isFile && configLocation == null) {
            throw new BatchrahmenInitialisierungException(NachrichtenSchluessel.ERR_KONF_DATEI_LESEN,
                    propertyFile);
        }
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset(); // override default configuration
        context.putProperty("BatchId", konfiguration.getAsString(KonfigurationSchluessel.PROPERTY_BATCH_ID));

        if (isFile) {
            jc.doConfigure(file);
        } else {
            jc.doConfigure(configLocation);
        }
    }

    /**
     * Creates a new instance and sets the configuration.
     *
     * @param rahmenKonfiguration configuration for Batch-Rahmen.
     * @param protokoll           The result-protocol.
     */
    public BatchLauncher(BatchKonfiguration rahmenKonfiguration, BatchErgebnisProtokoll protokoll) {
        this.rahmenKonfiguration = rahmenKonfiguration;
        this.protokoll = protokoll;
    }

    /**
     * Creates the spring contexts for the application and the Batchrahmen.
     * Starts the Batchrahmen-Bean using the method
     * {@link Batchrahmen#runBatch(BatchKonfiguration, BatchErgebnisProtokoll)}.
     *
     * @throws BatchAusfuehrungsException When an error occurs during batch execution.
     */
    private void launch() throws BatchAusfuehrungsException {

        List<Class<?>> configs = new ArrayList<>();
        try {
            for (final String name : rahmenKonfiguration.getAnwendungSpringKonfigFiles()) {
                configs.add(Class.forName(name));
            }
            for (final String name : rahmenKonfiguration.getBatchRahmenSpringKonfigFiles()) {
                configs.add(Class.forName(name));
            }
            // add BatchSecurityConfiguration last to allow custom bean overrides
            configs.add(BatchSecurityConfiguration.class);
        } catch (ClassNotFoundException e) {
            throw new BatchAusfuehrungsException(NachrichtenSchluessel.ERR_KLASSE_NICHT_GEFUNDEN, e);
        }

        ConfigurableApplicationContext rahmen =
                new SpringApplicationBuilder()
                        .sources(configs.toArray(new Class[0]))
                        .bannerMode(Banner.Mode.OFF)
                        .properties("isy.batchrahmen.batch-context=true")
                        .web(WebApplicationType.NONE)
                        .registerShutdownHook(true)
                        .profiles(rahmenKonfiguration.getSpringProfiles())
                        .initializers(applicationContext -> {
                            try {
                                initialisiereLogback(rahmenKonfiguration);
                            } catch (JoranException e) {
                                System.err.println(e.getMessage());
                            }
                        })
                        .run(ermittleSpringArguments());

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

    private String[] ermittleSpringArguments() {
        List<String> args = new ArrayList<>();

        String configDirectory =
                rahmenKonfiguration.getAsString(
                        KonfigurationSchluessel.KOMMANDO_PARAM_KONFIGURATION_PFAD,
                        null);

        if (StringUtils.isNotBlank(configDirectory)) {
            args.add("--spring.config.additional-location=file:" + configDirectory);
        }

        return args.toArray(new String[0]);
    }
}
