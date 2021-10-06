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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Marker;

import de.bund.bva.isyfact.logging.IsyMarker;
import de.bund.bva.isyfact.logging.exceptions.FehlerhafterLogeintrag;
import de.bund.bva.isyfact.logging.exceptions.LoggingTechnicalRuntimeException;
import de.bund.bva.isyfact.logging.impl.Ereignisschluessel;
import de.bund.bva.isyfact.logging.impl.FachdatenMarker;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;
import de.bund.bva.isyfact.logging.util.LoggingKonstanten;
import de.bund.bva.isyfact.logging.util.MdcHelper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.JsonFormatter;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import ch.qos.logback.core.CoreConstants;

/**
 * Logback layout for preparing the log entries in JSON format.
 * In particular, the layout also takes over the above-mentioned markers.
 *
 */
public class IsyJsonLayout extends JsonLayout {

    /** Constant for an empty correlation ID. */
    private static final String LEERE_KORRELATIONSID = "none";

    /** Attribute name of the time stamp. */
    private static final String ZEITSTEMPEL_ATTR_NAME = "zeitstempel";

    /** Attribute name of the parameters of a log message. */
    private static final String PARAMETER_ATTR_NAME = "parameter";

    /** Attribute name of the log message. */
    private static final String NACHRICHT_ATTR_NAME = "nachricht";

    /** Attribute name of the exception. */
    private static final String EXCEPTION_ATTR_NAME = "exception";

    /** Attribute name of the correlation ID. */
    private static final String KORRELATIONSID_ATTR_NAME = "korrelationsid";

    /** Attribute name for general markers. */
    private static final String MARKER_ATTR_NAME = "marker";

    /** Attribute name for shortened log messages. */
    private static final String GEKUERZT_ATTR_NAME = "gekuerzt";

    /** Maximum size of a log entry in bytes. */
    private int maxLength = 32000;


    /**
     * Class constructor.
     */
    public IsyJsonLayout() {
        super();

        includeLevel = true;
        includeThreadName = true;
        // MDC is not output because we include it as a 'correlation ID'.
        includeMDC = false;
        includeLoggerName = true;
        // Message is recorded neither raw nor formatted. We output the formatted message as a 'message'.
        includeFormattedMessage = false;
        includeMessage = false;
        includeException = true;
        includeContextName = true;

        // Timestamp is output manually as a 'timestamp'.
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
            Map<String, Object> stringMap = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (MDC_ATTR_NAME.equals(entry.getKey())) {
                    stringMap.put(entry.getKey(), entry.getValue());
                } else {
                    stringMap.put(entry.getKey(), entry.getValue().toString());
                }
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
        // Creates a map of JSON attributes. Only the slf4j standard attributes are filled in the super class.
        // In particular, no markers are evaluated.
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        // The attributes are sorted in the log according to the order in which they were added.
        String zeitstempel = formatTimestamp(event.getTimeStamp());
        if (zeitstempel != null) {
            jsonMap.put(ZEITSTEMPEL_ATTR_NAME, zeitstempel);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> defaultMap = super.toJsonMap(event);
        jsonMap.putAll(defaultMap);

        // Accept message
        String msg = event.getFormattedMessage();
        if (msg != null) {
            jsonMap.put(NACHRICHT_ATTR_NAME, msg);
        }

        // Correlation ID
        String korrelationsId = MdcHelper.liesKorrelationsId();
        if (korrelationsId == null) {
            korrelationsId = LEERE_KORRELATIONSID;
        }
        jsonMap.put(KORRELATIONSID_ATTR_NAME, korrelationsId);

        // Evaluating the markers
        Marker marker = event.getMarker();

        // Run through and process markers. IsyFact markers are transferred directly to the jsonMap.
        List<String> standardMarker = new ArrayList<>();
        processMarker(marker, jsonMap, standardMarker);

        // Accept the parameters of the message (placeholders) as separate attributes.
        Object[] parameter = event.getArgumentArray();
        if (parameter != null) {
            for (int i = 0; i < parameter.length; i++) {
                jsonMap.put(PARAMETER_ATTR_NAME + (i + 1), parameter[i]);
            }
        }

        // Accept standard markers in a single list.
        if (!standardMarker.isEmpty()) {
            jsonMap.put(MARKER_ATTR_NAME, standardMarker);
        }

        // Technical data in MDC: This allows the value of the "Technical data" marker to be overwritten again.
        boolean enthaeltFachlicheDaten = MdcHelper.liesMarkerFachdaten();
        if (enthaeltFachlicheDaten) {
            // This overwrites the previous data type of the log entry!
            processMarker(new FachdatenMarker(), jsonMap, standardMarker);
        }

        return jsonMap;
    }

    /**
     * This method processes the passed marker and recursively iterates through its references.
     * IsyFact markers are transferred to the jsonMap as name / value pairs.
     * All other "StandardMarkers" are collected in the "StandardMarker" list.
     *
     * @param marker
     *            the marker to be processed.
     * @param jsonMap
     *            Map with JSON attributes.
     * @param standardMarker
     *            List in which the standard markers are included.
     */
    private void processMarker(Marker marker, Map<String, Object> jsonMap, List<String> standardMarker) {

        if (marker == null) {
            return;
        }

        if (marker instanceof IsyMarker) {
            IsyMarker isyMarker = (IsyMarker) marker;
            // Values of root markers are not taken over
            if (!isyMarker.isRootMarker()) {
                // Markers with "NULL values" are also accepted.
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
     * This method checks whether the transferred string of the log entry exceeds
     * the maximum size and must be shortened. If the log entry is too large,
     * the log entry is shortened as follows:
     *  1. Remove all parameters from the log entry.
     *  2. Shorten the Exception field, if available
     *  3. Shorten the Message field
     *
     * If the log entry is still too long after it has been shortened, an exception is thrown.
     *
     * @param map        the map with the raw data of the log event
     * @param logeintrag converted the map into a string
     * @param event      the log event
     * @return the checked and possibly shortened log entry as a string
     */
    private String pruefeGroesse(Map<String, Object> map, String logeintrag, ILoggingEvent event) {
        if (maxLength > 0 && // Check whether a maximum length has been defined (0 = any length)
            event.getLevel().isGreaterOrEqual(Level.INFO) && // Consider only log messages that have level INFO or higher
            logeintrag.length() >= (maxLength / 2.0)) { // Check whether the log message can reach the maximum size in bytes at all with its length

            byte[] zeichen = logeintrag.getBytes(StandardCharsets.UTF_8);
            int tatsaechlicheLaenge = zeichen.length;
            if (tatsaechlicheLaenge > maxLength) {
                // First remove all parameters of the log entry
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
                        // First shorten exception if present, then message if still too long
                        ueberhang = feldKuerzen(EXCEPTION_ATTR_NAME, map, ueberhang);
                        if (ueberhang > 0) {
                            feldKuerzen(NACHRICHT_ATTR_NAME, map, ueberhang);
                        }
                    } else {
                        // Otherwise directly shorten the message
                        feldKuerzen(NACHRICHT_ATTR_NAME, map, ueberhang);
                    }
                }
                return getStringFromFormatter(map);
            }
        }

        return logeintrag;
    }

    /**
     * This method truncates the contents of a field of a map based on the submitted overhang,
     * calculates the overhang after truncation, and returns it.
     *
     * @param schluessel the key of the field in the map to be shortened
     * @param map        the map with the raw data of the log event
     * @param ueberhang  the overhang as number of characters
     * @return the updated overhang after the cut
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
     * This method calculates the overhang of a log entry that is transmitted as a map.
     * The overhang is first calculated in bytes and then converted into a number of characters.
     *
     * @param map the map with the raw data of the log event
     * @return the calculated overhang
     */
    private int berechneUeberhang(Map<String, Object> map) {
        String logeintrag = getStringFromFormatter(map);
        int tatsaechlicheLaenge = logeintrag.getBytes(StandardCharsets.UTF_8).length;

        // Determines the number of characters to be cut off
        // Calculates bytes per character
        float byteZeichen = (float) tatsaechlicheLaenge / (float) logeintrag.length();

        // Overhang indicates the number of characters to be removed
        return (int) ((tatsaechlicheLaenge - maxLength) / byteZeichen) + 1;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
