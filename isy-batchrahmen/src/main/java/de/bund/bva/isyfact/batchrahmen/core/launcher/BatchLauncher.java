package de.bund.bva.isyfact.batchrahmen.core.launcher;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
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
 * This class starts a batch (see {@link Batchrahmen} with the transferred configuration.
 * The configuration is done via command line arguments und a property file.
 * <p>
 * The processing logic is divided into a Batchrahmen and a Ausführungsbean.
 * See the Detailkonzept Batch of the Migrationsstufe 1.
 * <p>
 * Any further command line parameters and properties can be entered.
 * The command line parameters are added to the properties and overwrite them, if necessary, before they are passed on to the Batchrahmen-Bean.
 * The Batchrahmen-Bean forwards the complete configuration to the Ausfuehrungsbean, which can use it for configuration.
 */
public class BatchLauncher {
    /**
     * The configuration for Batch-Rahmen.
     */
    private BatchKonfiguration rahmenKonfiguration;

    /**
     * The protocol, for storing messages und statistics of Batch-Ausfuehrung.
     */
    private BatchErgebnisProtokoll protokoll;

    /**
     * Main method for starting batch. This method calls the method {@link #run(String[])} und return its ReturnCode via System.exit().
     *
     * @param args command line parameters.
     */
    public static void main(String[] args) {
        System.exit(BatchLauncher.run(args));
    }

    /**
     * Starts batch. For configuration see class comment.
     *
     * @param args command line parameters. For description see class comment.
     * @return return code of batch.
     */
    public static int run(final String[] args) {
        IsyLogger log = null;
        BatchKonfiguration rahmenKonfiguration = null;
        DefaultBatchErgebnisProtokoll protokoll = null;
        String ergebnisDatei = null;
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
            if (protokoll.isBatchAbgebrochen()) {
                returnCode = BatchReturnCode.FEHLER_MANUELLER_ABBRUCH;
            }
            if (protokoll.isMaximaleLaufzeitUeberschritten()) {
                returnCode = BatchReturnCode.FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN;
            } else if (protokoll.enthaeltFehlerNachrichten()) {
                returnCode = BatchReturnCode.FEHLER_AUSGEFUEHRT;
            } else {
                returnCode = protokoll.getReturnCode();
                if (returnCode == null) {
                    returnCode = BatchReturnCode.OK;
                }
            }
        } catch (BatchAusfuehrungsException ex) {
            protokolliereFehler(log, protokoll, ex);
            if (ex.getReturnCode() != null) {
                returnCode = ex.getReturnCode();
            }
        } catch (BatchrahmenException ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = ex.getReturnCode();
        } catch (AuthenticationException | AccessDeniedException | ClientAuthorizationException ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = BatchReturnCode.FEHLER_KONFIGURATION;
        } catch (Throwable ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = BatchReturnCode.FEHLER_ABBRUCH;
        } finally {
            if (protokoll != null) {
                protokoll.setReturnCode(returnCode);
                protokoll.batchEnde();
            }
        }
        System.out.print(returnCode.getWert() + ": " + returnCode.getText());
        return returnCode.getWert();
    }

    /**
     * Logs an error. If possible, this is done via the {@link IsyLogger} log. In any case,
     * there is an output on System.Err. In addition, it is recorded in ErgebnisProtokoll.
     *
     * @param log       Logger.
     * @param protokoll DefaultBatchErgebnisProtokoll.
     * @param ex        Occurred exception.
     */
    private static void protokolliereFehler(IsyLogger log, BatchErgebnisProtokoll protokoll, Throwable ex) {
        String nachricht = exceptionToString(ex);
        System.err.println(nachricht);
        if (log != null) {
            log.error(BatchRahmenEreignisSchluessel.EPLBAT00001, "Fehler bei der Batchausführung.", ex);
        } else {
            ex.printStackTrace();
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
                System.err.println("Die Fehlermeldung " + protokollEx.toString()
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
    private static String exceptionToString(Throwable t) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String exceptionString = "";
        try {
            t.printStackTrace(new PrintStream(out, false, StandardCharsets.UTF_8.name()));
            exceptionString = out.toString(StandardCharsets.UTF_8.name()).replaceAll("\\r{0,1}\\n", " | ");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return exceptionString;
    }

    /**
     * Initializes Logback with the log configuration which was specified in the property file with key
     * {@link KonfigurationSchluessel # PROPERTY_BATCHRAHMEN_LOGBACK_CONF}.
     * /config/logback-batch.xml is used as default.
     *
     * @param konf batch configuration.
     * @throws JoranException If the Logger could not be configured.
     */
    private static void initialisiereLogback(BatchKonfiguration konf) throws JoranException {
        String propertyFile =
                konf.getAsString(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_LOGBACK_CONF,
                        "/config/logback-batch.xml");
        URL configLocation = BatchLauncher.class.getResource(propertyFile);
        if (configLocation == null) {
            throw new BatchrahmenInitialisierungException(NachrichtenSchluessel.ERR_KONF_DATEI_LESEN,
                    propertyFile);
        }
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset(); // override default configuration
        context.putProperty("BatchId", konf.getAsString(KonfigurationSchluessel.PROPERTY_BATCH_ID));
        jc.doConfigure(configLocation);
    }

    /**
     * Creates a new instance and sets the configuration.
     *
     * @param rahmenKonfiguration configuration for Batch-Rahmen.
     * @param protokoll           ErgebinsProtokoll.
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

        List<Class> configs = new ArrayList<>();
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

        ConfigurableApplicationContext
                rahmen = new SpringApplicationBuilder().sources(configs.toArray(new Class[0]))
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
                .run();

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
}
