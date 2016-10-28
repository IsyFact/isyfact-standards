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
import java.util.List;
import java.util.Map;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import de.bund.bva.isyfact.logging.IsyMarker;
import de.bund.bva.isyfact.logging.impl.MarkerSchluessel;
import de.bund.bva.isyfact.logging.util.LoggingKonstanten;
import de.bund.bva.isyfact.logging.util.MdcHelper;

/**
 * Logback-Layout zum Aufbereiten der Logeinträge in JSON-Format. Das Layout übernimmt dabei insbesondere auch
 * die übergenenen Marker.
 * 
 */
public class IsyJsonLayout extends JsonLayout {

    /** Kosntante für eine leere Korrelations-ID. */
    private static final String LEERE_KORRELATIONSID = "none";

    /** Attributname des Zeitstempels. */
    private static final String ZEITSTEMPEL_ATTR_NAME = "zeitstempel";

    /** Attributname der Parameter einer Log-Nachricht. */
    private static final String PARAMETER_ATTR_NAME = "parameter";

    /** Attributname der Log-Nachricht. */
    private static final String NACHRICHT_ATTR_NAME = "nachricht";

    /** Attributname der Korrelations-Id. */
    private static final String KORRELATIONSID_ATTR_NAME = "korrelationsid";

    /** Attributname für allgemeine Marker. */
    private static final String MARKER_ATTR_NAME = "marker";

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

    /**
     * {@inheritDoc}
     * 
     * @see ch.qos.logback.contrib.json.classic.JsonLayout#toJsonMap(ch.qos.logback.classic.spi.ILoggingEvent)
     */
    @Override
    protected Map<String, Object> toJsonMap(ILoggingEvent event) {

        // Erstellen einer Map von JSON-Attributen. In der Superklasse werden nur die sl4j-Standardattribute
        // gefüllt. Es werden insbesondere keine Marker ausgewertet.
        @SuppressWarnings("unchecked")
        Map<String, Object> jsonMap = super.toJsonMap(event);

        String zeitstempel = formatTimestamp(event.getTimeStamp());
        if (zeitstempel != null) {
            jsonMap.put(ZEITSTEMPEL_ATTR_NAME, zeitstempel);
        }

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
        List<String> standardMarker = new ArrayList<String>();
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
}
