package de.bund.bva.isyfact.logging.hilfsklassen;

/*
 * #%L
 * isy-logging
 * %%
 * 
 * %%
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * #L%
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import de.bund.bva.isyfact.logging.AbstractLogTest;

/**
 * Hilfsklasse, um die Vorlage-Logdateien in einzelne JSON-Dateien zu schreiben.
 * 
 */
public class LogsToJsonFiles extends AbstractLogTest {

    /** Verzeichnis, in das die JSON-Logeinträge geschrieben werden-. */
    private static final String JSON_VERZEICHNIS = LOG_VERZEICHNIS + "json/";

    /**
     * Schreibt die einzelnen Zeilen der Vorlage-Logs in JSOn-Dateien in einzelne JSON-Dateien, so dass sie
     * besser durch Plugins betrachtet werden können.
     * 
     * @param args
     *            Wird nicht verwendet.
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {

        File jsonVerzeichnis = new File(JSON_VERZEICHNIS);
        jsonVerzeichnis.mkdirs();

        File logVerzeichnis = new File(LOG_VERZEICHNIS);
        for (File logFile : logVerzeichnis.listFiles()) {
            if (logFile.isFile() && logFile.getName().contains(VORLAGE_PREFIX)) {
                BufferedReader ergebnisReader = new BufferedReader(new FileReader(logFile));

                String line;
                int zeilenindex = 0;
                while ((line = ergebnisReader.readLine()) != null) {
                    zeilenindex++;
                    String jsonDateiName = JSON_VERZEICHNIS + logFile.getName() + "_" + zeilenindex + ".json";
                    PrintWriter jsonWriter = new PrintWriter(new FileWriter(new File(jsonDateiName)));
                    jsonWriter.println(line);
                    jsonWriter.close();
                }
                ergebnisReader.close();
            }
        }
    }

}
