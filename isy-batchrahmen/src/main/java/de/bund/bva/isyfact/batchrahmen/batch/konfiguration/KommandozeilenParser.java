package de.bund.bva.isyfact.batchrahmen.batch.konfiguration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.bund.bva.isyfact.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenParameterException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;

/**
 * Encapsulates the command line parser for the {@link de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen}.
 */
class KommandozeilenParser {
    /**
     * Options for command line parser.
     * Generated in {@link #initialisiereKommandozeilenOptionen()}.
     */
    private Options options;

    /**
     * Default constructor.
     */
    public KommandozeilenParser() {
        initialisiereKommandozeilenOptionen();
    }

    /**
     * Creates the command line argument criteria for parsing the command line.
     */
    private void initialisiereKommandozeilenOptionen() {
        this.options = new Options();

        OptionBuilder.withArgName("Property-Datei");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("Name der Property-Datei des Batches");

        this.options.addOption(OptionBuilder.create(KonfigurationSchluessel.KOMMANDO_PARAM_PROP_DATEI));
        this.options.addOption(KonfigurationSchluessel.KOMMANDO_PARAM_START, false,
                "Checkpoint/Restart Methode: START");
        this.options.addOption(KonfigurationSchluessel.KOMMANDO_PARAM_RESTART, false,
                "Checkpoint/Restart Methode: RESTART");
        this.options.addOption(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_LAUF, false,
                "Checkpoint/Restart Methode: Ignorieren, dass Batch laut DB bereits läuft.");
        this.options.addOption(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART, false,
                "Checkpoint/Restart Methode: START erzwingen, obwohl Batch abgebrochen ist.");

        OptionBuilder.withArgName("Maximale Laufzeit in Minuten");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Maximale Laufzeit des Batches");
        OptionBuilder.withType(Long.class);

        this.options.addOption(OptionBuilder.create(KonfigurationSchluessel.KOMMANDO_PARAM_LAUFZEIT));
    }

    /**
     * Parses the command line.
     *
     * @param kommandoZeile The command line to parse.
     * @return The parsed command line.
     */
    private CommandLine parseKommandoZeile(String[] kommandoZeile) {
        try {
            CommandLineParser parser = new GnuParser();
            CommandLine line = parser.parse(this.options, kommandoZeile, true);
            return line;
        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java de.bund.bva.common.batchrahmen.core.launcher.BatchLauncher",
                    this.options);
            throw new BatchrahmenParameterException(NachrichtenSchluessel.ERR_KOMMANDO_PARAMETER_UNGUELTIG,
                    exp, exp.getMessage());
        }
    }

    /**
     * Stores all command line arguments into a hashmap.
     *
     * @param kommandoZeile the input values to parse.
     * @return a map with all command line parameters.
     */
    public Map<String, String> parse(String[] kommandoZeile) {
        Map<String, String> cmdArgs = new HashMap<String, String>();
        CommandLine line = parseKommandoZeile(kommandoZeile);

        cmdArgs.put(KonfigurationSchluessel.KOMMANDO_PARAM_PROP_DATEI,
                line.getOptionValue(KonfigurationSchluessel.KOMMANDO_PARAM_PROP_DATEI));

        boolean start = line.hasOption(KonfigurationSchluessel.KOMMANDO_PARAM_START);
        boolean restart = line.hasOption(KonfigurationSchluessel.KOMMANDO_PARAM_RESTART);
        if ((!start && !restart) || (start && restart)) {
            throw new BatchrahmenParameterException(NachrichtenSchluessel.ERR_KOMMANDO_PARAMETER_NOETIG,
                    KonfigurationSchluessel.KOMMANDO_PARAM_START, KonfigurationSchluessel.KOMMANDO_PARAM_RESTART);
        }

        cmdArgs.put(KonfigurationSchluessel.KOMMANDO_PARAM_START,
                String.valueOf(line.hasOption(KonfigurationSchluessel.KOMMANDO_PARAM_START)));
        cmdArgs.put(KonfigurationSchluessel.KOMMANDO_PARAM_RESTART,
                String.valueOf(line.hasOption(KonfigurationSchluessel.KOMMANDO_PARAM_RESTART)));
        cmdArgs.put(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_LAUF,
                String.valueOf(line.hasOption(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_LAUF)));
        cmdArgs.put(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART,
                String.valueOf(line.hasOption(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART)));
        String optionaleLaufzeit = line.getOptionValue(KonfigurationSchluessel.KOMMANDO_PARAM_LAUFZEIT);
        if (optionaleLaufzeit != null) {
            cmdArgs.put(KonfigurationSchluessel.KOMMANDO_PARAM_LAUFZEIT, optionaleLaufzeit);
        }

        String[] andereParameter = line.getArgs();
        if (andereParameter != null) {
            for (int i = 0; i < andereParameter.length; i++) {
                if (andereParameter[i].startsWith("-")) {
                    String andererParameter = andereParameter[i].substring(1);
                    if (i < andereParameter.length - 1 && !andereParameter[i + 1].startsWith("-")) {
                        cmdArgs.put(andererParameter, andereParameter[i + 1]);
                        i++;
                    } else if (KonfigurationSchluessel.KOMMANDO_PARAM_LAUFZEIT.equals(andererParameter)) {
                        /*
                         * The runtime parameter is defined as an option and should be specified before the rest of the parameters.
                         * If the runtime parameter is given after the options without the minutes, we still abort with a linked error message.
                         * Otherwise the runtime is taken over in cmdArgs and "true" is written in as minute.
                         */
                        throw new BatchrahmenParameterException(
                                NachrichtenSchluessel.ERR_KOMMANDO_PARAMETER_WERT_NOETIG,
                                KonfigurationSchluessel.KOMMANDO_PARAM_LAUFZEIT);
                    } else {
                        cmdArgs.put(andererParameter, "true");
                    }
                } else {
                    throw new BatchrahmenParameterException(
                            NachrichtenSchluessel.ERR_KOMMANDO_PARAMETER_PRAEFIX, andereParameter[i]);
                }
            }
        }
        return cmdArgs;
    }

}
