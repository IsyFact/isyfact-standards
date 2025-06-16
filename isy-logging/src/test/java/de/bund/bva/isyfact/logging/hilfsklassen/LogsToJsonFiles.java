package de.bund.bva.isyfact.logging.hilfsklassen;

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
