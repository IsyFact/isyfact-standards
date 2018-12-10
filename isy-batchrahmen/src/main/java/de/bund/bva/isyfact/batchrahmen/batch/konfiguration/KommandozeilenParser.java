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
package de.bund.bva.isyfact.batchrahmen.batch.konfiguration;

import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenParameterException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;

/**
 * Kapselt den Kommandozeilenparser für den Batchrahmen.
 *
 *
 */
class KommandozeilenParser {
    /**
     * Optionen für Kommandozeilen parser. Wird in {@link #initialisiereKommandozeilenOptionen()} erzeugt.
     */
    private Options options;

    /**
     * Standardkonstruktor.
     */
    public KommandozeilenParser() {
        initialisiereKommandozeilenOptionen();
    }

    /**
     * erzeugt die Kommandozeilen-Argument-Kriterien fuer das Parsen der Kommandozeile.
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
     * Parst die Kommandozeile.
     * @param kommandoZeile
     *            Die Kommandozeile zum Parsen.
     * @return Die geparste Kommandozeile
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
     * Speichert alle Kommandozeilen-Argumente in eine Hashmap.
     *
     * @param kommandoZeile
     *            die zu parsenden Eingabe-Werte.
     *
     * @return eine Map mit allen Kommandozeilen-Parametern.
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
            "" + line.hasOption(KonfigurationSchluessel.KOMMANDO_PARAM_START));
        cmdArgs.put(KonfigurationSchluessel.KOMMANDO_PARAM_RESTART,
            "" + line.hasOption(KonfigurationSchluessel.KOMMANDO_PARAM_RESTART));
        cmdArgs.put(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_LAUF,
            "" + line.hasOption(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_LAUF));
        cmdArgs.put(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART,
            "" + line.hasOption(KonfigurationSchluessel.KOMMANDO_PARAM_IGNORIERE_RESTART));
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
                         * Der Laufzeit-Parameter ist als Option definiert und sollte vor den restlichen
                         * Parametern angegeben werden. Falls der Laufzeit-Parameter fälchlicherweise nach den
                         * Optionen ohne die Minuten agegeben wird, brechen wir trotzdem mit einer
                         * verknüpftigen Fehlermeldung ab. Sonst wird die Laufzeit in cmdArgs übernommen und
                         * als Minute wird "true" reingeschrieben.
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
