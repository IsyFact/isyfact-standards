package de.bund.bva.isyfact.logging.layout;

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
 * #L%
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.JsonFormatter;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import ch.qos.logback.core.CoreConstants;
import de.bund.bva.isyfact.logging.IsyMarker;
import de.bund.bva.isyfact.logging.exceptions.FehlerhafterLogeintrag;
import de.bund.bva.isyfact.logging.exceptions.LoggingTechnicalRuntimeException;
import de.bund.bva.isyfact.logging.impl.Ereignisschluessel;
import de.bund.bva.isyfact.logging.impl.FachdatenMarker;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;
import de.bund.bva.isyfact.logging.impl.MarkerSchluessel;
import de.bund.bva.isyfact.logging.util.LoggingKonstanten;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import org.slf4j.Marker;

/**
 * Logback-Layout zum Aufbereiten der Logeinträge in JSON-Format. Das Layout übernimmt dabei insbesondere auch
 * die übergenenen Marker.
 *
 */
public class IsyJsonLayout extends JsonLayout {

    /** Konstante für eine leere Korrelations-ID. */
    private static final String LEERE_KORRELATIONSID = "none";

    /** Attributname des Zeitstempels. */
    private static final String ZEITSTEMPEL_ATTR_NAME = "zeitstempel";

    /** Attributname der Parameter einer Log-Nachricht. */
    private static final String PARAMETER_ATTR_NAME = "parameter";

    /** Attributname der Log-Nachricht. */
    private static final String NACHRICHT_ATTR_NAME = "nachricht";

    /** Attributname der Log-Nachricht. */
    private static final String EXCEPTION_ATTR_NAME = "exception";

    /** Attributname der Korrelations-Id. */
    private static final String KORRELATIONSID_ATTR_NAME = "korrelationsid";

    /** Attributname für allgemeine Marker. */
    private static final String MARKER_ATTR_NAME = "marker";

    /** Attributname für gekürzte Log-Nachrichten. */
    private static final String GEKUERZT_ATTR_NAME = "gekuerzt";

    /** Maximale Groesse eines Loeintrags in Byte. */
    private int maxLength = 32000;


    /**
     * Konstruktor der Klasse.
     */
    public IsyJsonLayout() {
        super();

        includeLevel = true;
        includeThreadName = true;
        // MDC wird nicht ausgegeben, da wir diesen als 'korrelationsid' aufnehmen.
        includeMDC = false;
        includeLoggerName = true;
        // Message wird weder roh noch formatiert aufgenommen. Wir geben die formatierte Nachricht als
        // 'nachricht' aus.
        includeFormattedMessage = false;
        includeMessage = false;
        includeException = true;
        includeContextName = true;

        // Zeitstempel wird mauell als 'zeitstempel' ausgegeben.
        includeTimestamp = false;
        appendLineSeparator = false;
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        Map<String, Object> map = toJsonMap(event);
        if (map == null || map.isEmpty()) {
            return "";
        }
        String result = getStringFromFormatter(map);
        if (result == null || result.isEmpty()) {
            return "";
        }
        result = pruefeGroesse(map, result, event);

        return isAppendLineSeparator() ? result + CoreConstants.LINE_SEPARATOR : result;
    }

    private String getStringFromFormatter(Map<String, Object> map) {
        JsonFormatter formatter = getJsonFormatter();
        if (formatter == null) {
            Exception fehler =
                new LoggingTechnicalRuntimeException(FehlerSchluessel.FEHLENDE_KONFIGURATION_JSON_LAYOUT,
                    getClass().getName());
            addError(fehler.getMessage(), fehler);
            return "";
        }

        try {
            return formatter.toJsonString(map);
        } catch (Exception e) {
            Map<String, String> stringMap = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                stringMap.put(entry.getKey(), entry.getValue().toString());
            }
            try {
                return formatter.toJsonString(stringMap);
            } catch (Exception ex) {
                Exception fehler =
                    new FehlerhafterLogeintrag(FehlerSchluessel.FEHLER_SERIALISIERUNG_AUFRUFPARAMETER, ex);
                addError(fehler.getMessage(), fehler);
                return "";
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ch.qos.logback.contrib.json.classic.JsonLayout#toJsonMap(ch.qos.logback.classic.spi.ILoggingEvent)
     */
    @Override
    protected Map<String, Object> toJsonMap(ILoggingEvent event) {
        // Erstellen einer Map von JSON-Attributen. In der Superklasse werden nur die sl4j-Standardattribute
        // gefüllt. Es werden insbesondere keine Marker ausgewertet.
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        // Die Attribute werden im Log nach der Reihenfolge ihrer Hinzufügung sortiert.
        String zeitstempel = formatTimestamp(event.getTimeStamp());
        if (zeitstempel != null) {
            jsonMap.put(ZEITSTEMPEL_ATTR_NAME, zeitstempel);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> defaultMap = super.toJsonMap(event);
        jsonMap.putAll(defaultMap);

        // Nachricht übernehmen
        String msg = event.getFormattedMessage();
        if (msg != null) {
            jsonMap.put(NACHRICHT_ATTR_NAME, msg);
        }

        // korrelationsid
        String korrelationsId = MdcHelper.liesKorrelationsId();
        if (korrelationsId == null) {
            korrelationsId = LEERE_KORRELATIONSID;
        }
        jsonMap.put(KORRELATIONSID_ATTR_NAME, korrelationsId);

        // Auswerten der Marker
        Marker marker = event.getMarker();

        // Marker durchlaufen und verarbeiten. IsyFact-Marker werden dabei direkt in die jsonMap übernommen.
        List<String> standardMarker = new ArrayList<>();
        processMarker(marker, jsonMap, standardMarker);

        // Parameter der Nachricht (Platzhalter) als separate Attribute übernehmen.
        Object[] parameter = event.getArgumentArray();
        if (parameter != null) {
            for (int i = 0; i < parameter.length; i++) {
                jsonMap.put(PARAMETER_ATTR_NAME + (i + 1), parameter[i]);
            }
        }

        // Standardmarker in einer einzelnen Liste übernehmen.
        if (!standardMarker.isEmpty()) {
            jsonMap.put(MARKER_ATTR_NAME, standardMarker);
        }

        // Fachdaten in MDC: Dadurch kann der Wert des Markers "Fachdaten" nochmals überschrieben werden.
        boolean enthaeltFachlicheDaten = MdcHelper.liesMarkerFachdaten();
        if (enthaeltFachlicheDaten) {
            // Hierdurch wird der bisherige Datentyp des Logeintrags überschrieben!
            processMarker(new FachdatenMarker(), jsonMap, standardMarker);
            jsonMap.put(MarkerSchluessel.FACHDATEN.getWert(), LoggingKonstanten.TRUE);
        }

        return jsonMap;
    }

    /**
     * Diese Methode verarbeitet den übergebenen Marker und durchläuft rekursiv dessen Referenzen.
     * IsyFact-Marker werden dabei als Name/Wert-Paare in die jsonMap übernommen. Alle anderen
     * "StandardMarker" werden in der Liste "standardMarker" gesammelt.
     *
     * @param marker
     *            der zu verarbeitende Marker.
     * @param jsonMap
     *            Map mit JSON-Attributen.
     * @param standardMarker
     *            Liste, in der die Standard-Marker aufgenommen werden.
     */
    private void processMarker(Marker marker, Map<String, Object> jsonMap, List<String> standardMarker) {

        if (marker == null) {
            return;
        }

        if (marker instanceof IsyMarker) {
            IsyMarker isyMarker = (IsyMarker) marker;
            // Werte von Root-Markern werden nicht übernommen
            if (!isyMarker.isRootMarker()) {
                // Es werden auch Marker mit "NULL-Werten" übernommen".
                jsonMap.put(isyMarker.getName(), isyMarker.getValue());
            }
        } else {
            standardMarker.add(marker.getName());
        }

        Iterator<Marker> iterator = marker.iterator();

        while (iterator.hasNext()) {
            processMarker(iterator.next(), jsonMap, standardMarker);
        }
    }


    /**
     * Diese Methode prüft, ob der übergebene String des Logeintrags die maximale Größe überschreitet und
     * gekürzt werden muss. Ist der Logeintrag zu groß, wird der Logeintrag nach folgendem Ablauf gekürzt:
     * 1. Alle Parameter aus dem Logeintrag entfernen.
     * 2. Feld Exception kürzen, falls vorhanden
     * 3. Feld Nachricht kürzen
     *
     * Sollte der Logeintrag nach dem Kürzen immer noch zu lang sein, so wird eine Exception geworfen.
     *
     * @param map        die Map mit den Rohdaten des Log-Events
     * @param logeintrag die Map in einen String gewandelt
     * @param event      das Log-Event
     * @return der geprüfte und eventuell gekürzte Logeintrag als String
     */

    private String pruefeGroesse(Map<String, Object> map, String logeintrag, ILoggingEvent event) {
        if (maxLength > 0 && // Prüfen, ob eine maximale Länge definiert wurde (0=beliebig lang)
            event.getLevel().isGreaterOrEqual(Level.INFO) && // Nur Lognachrichten in Betracht ziehen, die Level INFO oder höher besitzen
            logeintrag.length() >= (maxLength / 2.0)) { // Prüfen, ob die Lognachricht die maximale Größe in Bytes überhaupt mit ihrer Länge erreichen kann

            byte[] zeichen = logeintrag.getBytes();
            int tatsaechlicheLaenge = zeichen.length;
            if (tatsaechlicheLaenge > maxLength) {
                // Zunächst alle Parameter des Logeintrags entfernen
                map.replaceAll((k, v) -> {
                    if (k.startsWith(PARAMETER_ATTR_NAME)) {
                        return Ereignisschluessel.DEBUG_LOG_GEKUERZT.getNachricht();
                    } else {
                        return v;
                    }
                });

                map.put(GEKUERZT_ATTR_NAME, LoggingKonstanten.TRUE);

                int ueberhang = berechneUeberhang(map);

                if (ueberhang > 0) {
                    if (map.containsKey(EXCEPTION_ATTR_NAME)) {
                        // Zuerst Exception kürzen, falls vorhanden, dann Nachricht, falls noch zu lang
                        ueberhang = feldKuerzen(EXCEPTION_ATTR_NAME, map, ueberhang);
                        if (ueberhang > 0) {
                            feldKuerzen(NACHRICHT_ATTR_NAME, map, ueberhang);
                        }
                    } else {
                        // Ansonsten direkt die Nachricht kürzen
                        feldKuerzen(NACHRICHT_ATTR_NAME, map, ueberhang);
                    }
                }
                return getStringFromFormatter(map);
            }
        }

        return logeintrag;
    }

    /**
     * Diese Methode kürzt den Inhalt eines Feldes einer Map anhand des übermittelten Überhangs,
     * berechnet den Überhang nach der Kürzung und gibt diesen zurück.
     *
     * @param schluessel der Schluessel des Feldes in der Map, das gekürzt werden soll
     * @param map        die Map mit den Rohdaten des Log-Events
     * @param ueberhang  der Überhang als Anzahl Zeichen
     * @return der aktualisierte Überhang nach der Kürzung
     */

    private int feldKuerzen(String schluessel, Map<String, Object> map, int ueberhang) {
        int neuerUeberhang = ueberhang;
        int vorherigerUeberhang;
        if (map.containsKey(schluessel)) {
            int feldlaenge;
            do {
                vorherigerUeberhang = neuerUeberhang;
                feldlaenge = map.get(schluessel).toString().length();
                if (neuerUeberhang >= feldlaenge) {
                    map.put(schluessel, Ereignisschluessel.DEBUG_LOG_GEKUERZT.getNachricht());
                } else {
                    map.put(schluessel,
                        map.get(schluessel).toString().substring(0, feldlaenge - neuerUeberhang - 1));
                }
                neuerUeberhang = berechneUeberhang(map);
            } while (neuerUeberhang > 0 && !map.get(schluessel).toString()
                .equals(Ereignisschluessel.DEBUG_LOG_GEKUERZT.getNachricht()) && neuerUeberhang != vorherigerUeberhang);
        }
        return neuerUeberhang;
    }

    /**
     * Diese Methode berechnet den Überhang eines Logeintrags, der als Map übermittelt wird.
     * Der Überhang wird zunächst in Bytes berechnet und anschließend in eine Anzahl von Zeichen umgewandelt.
     *
     * @param map die Map mit den Rohdaten des Log-Events
     * @return der berechnete Ueberhang
     */
    private int berechneUeberhang(Map<String, Object> map) {
        String logeintrag = getStringFromFormatter(map);
        int tatsaechlicheLaenge = logeintrag.getBytes().length;

        // Anzahl der abzuschneidenden Zeichen ermitteln (
        // Berechne Bytes pro Zeichen
        float byteZeichen = (float) tatsaechlicheLaenge / (float) logeintrag.length();

        // Ueberhang gibt die Anzahl zu entfernender Zeichen an
        return (int) ((tatsaechlicheLaenge - maxLength) / byteZeichen) + 1;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
