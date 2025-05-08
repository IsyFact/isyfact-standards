package de.bund.bva.isyfact.logging.layout;



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
 * Logback layout for formatting log entries as JSON.
 * <p>
 * In particular, the layout includes the passed markers.
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
        // MDC is not output because we include it as 'korrelationsid'
        includeMDC = false;
        includeLoggerName = true;
        // Message is recorded neither raw nor formatted. We output the formatted message as a 'message'.
        includeFormattedMessage = false;
        includeMessage = false;
        includeException = true;
        includeContextName = true;

        // Timestamp is output manually as 'zeitstempel'
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

        // The attributes are sorted in the log in the order in which they were added.
        String zeitstempel = formatTimestamp(event.getTimeStamp());
        if (zeitstempel != null) {
            jsonMap.put(ZEITSTEMPEL_ATTR_NAME, zeitstempel);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> defaultMap = super.toJsonMap(event);
        jsonMap.putAll(defaultMap);

        // Include the message
        String msg = event.getFormattedMessage();
        if (msg != null) {
            jsonMap.put(NACHRICHT_ATTR_NAME, msg);
        }

        // Include the correlation ID
        String korrelationsId = MdcHelper.liesKorrelationsId();
        if (korrelationsId == null) {
            korrelationsId = LEERE_KORRELATIONSID;
        }
        jsonMap.put(KORRELATIONSID_ATTR_NAME, korrelationsId);

        // Evaluate the markers
        Marker marker = event.getMarker();

        // Process the markers recursively. IsyFact markers are transferred directly to the jsonMap.
        List<String> standardMarker = new ArrayList<>();
        processMarker(marker, jsonMap, standardMarker);

        // Include the parameters of the message (placeholders) as separate attributes.
        Object[] parameter = event.getArgumentArray();
        if (parameter != null) {
            for (int i = 0; i < parameter.length; i++) {
                jsonMap.put(PARAMETER_ATTR_NAME + (i + 1), parameter[i]);
            }
        }

        // Include standard markers as a single attribute.
        if (!standardMarker.isEmpty()) {
            jsonMap.put(MARKER_ATTR_NAME, standardMarker);
        }

        // Technical data in MDC: This allows the value of the "Fachdaten" marker to be overwritten again.
        boolean enthaeltFachlicheDaten = MdcHelper.liesMarkerFachdaten();
        if (enthaeltFachlicheDaten) {
            // This overwrites the previous data type of the log entry!
            processMarker(new FachdatenMarker(), jsonMap, standardMarker);
        }

        return jsonMap;
    }

    /**
     * This method processes the passed marker and recursively iterates through its references.
     * <p>
     * IsyFact markers are transferred to the {@code jsonMap} as name / value pairs. All other "StandardMarkers" are
     * collected in the {@code standardMarker} list.
     *
     * @param marker
     *         the marker to process
     * @param jsonMap
     *         map to fill with IsyFact marker values by name
     * @param standardMarker
     *         list to fill with the standard markers
     */
    private void processMarker(Marker marker, Map<String, Object> jsonMap, List<String> standardMarker) {

        if (marker == null) {
            return;
        }

        if (marker instanceof IsyMarker) {
            IsyMarker isyMarker = (IsyMarker) marker;
            // Values of root markers are not included
            if (!isyMarker.isRootMarker()) {
                // Markers with "NULL values" are also included.
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
     * This method checks whether the transferred string of the log entry exceeds the maximum size and must be
     * shortened.
     * <p>
     * If the log entry is too large, the log entry is shortened as follows:
     * <ol>
     * <li>Remove all parameters from the log entry</li>
     * <li>Shorten the Exception field, if available</li>
     * <li>Shorten the Message field</li>
     * </ol>
     * <p>
     * If the log entry is still too long after it has been shortened, an exception is thrown.
     *
     * @param map
     *         the map with the raw data of the log event
     * @param logeintrag
     *         the map formatted as a string, in order to check the length of the log entry
     * @param event
     *         the log event
     * @return the checked and possibly shortened log entry as a string
     */
    private String pruefeGroesse(Map<String, Object> map, String logeintrag, ILoggingEvent event) {
        if (maxLength > 0 && // Check whether a maximum length has been defined (0 = any length)
            event.getLevel().isGreaterOrEqual(Level.INFO) && // Consider only log messages that have level INFO or higher
            logeintrag.length() >= (maxLength / 2.0)) { // Check whether the log message can reach the maximum size in bytes at all with its length

            byte[] zeichen = logeintrag.getBytes(StandardCharsets.UTF_8);
            int tatsaechlicheLaenge = zeichen.length;
            if (tatsaechlicheLaenge > maxLength) {
                // First remove all parameters from the log entry
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
     * This method truncates the contents of a field of a map based on the passed overhang, calculates the new overhang
     * after truncation, and returns it.
     *
     * @param schluessel
     *         the key of the field in the map to be shortened
     * @param map
     *         the map with the raw data of the log event
     * @param ueberhang
     *         the overhang as number of characters
     * @return the updated overhang after the the field was truncated
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
                    // the field can't be truncated and is replaced with a message indicating this
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
     * This method calculates the overhang (number of characters that the log entry is longer than {@link #maxLength})
     * of the log entry that is passed as a map.
     * <p>
     * The overhang is first calculated in bytes and then converted into a number of characters.
     *
     * @param map
     *         the map with the raw data of the log event
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
