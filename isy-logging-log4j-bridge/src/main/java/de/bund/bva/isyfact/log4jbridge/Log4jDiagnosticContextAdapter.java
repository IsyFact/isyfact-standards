package de.bund.bva.isyfact.log4jbridge;

/*
 * #%L
 * isy-logging-log4j-bridge
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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.slf4j.spi.MDCAdapter;

import de.bund.bva.isyfact.logging.util.MdcHelper;

/**
 * DiagnosticContextAdapter der die Verbindung des MDC von SLF4J und log4j herstellt und zudem eine
 * Sonderbehandlung für die Korrelations-ID durchführt, so dass diese sowohl bei der Nutzung des NDC als auch
 * des MDC korrekt gesetzt wird.
 */
public class Log4jDiagnosticContextAdapter implements MDCAdapter {

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.MDCAdapter#put(java.lang.String, java.lang.String)
     */
    @Override
    public void put(String key, String val) {
        if (MdcHelper.MDC_KORRELATIONS_ID.equals(key)) {
            // Falls bereits eine Korrelations-ID gesetzt wurde, entspricht das put einem "Ersetzen". Im NDC
            // führt das erneute pushen jedoch dazu, dass die Einträge doppelt existieren. Aus diesem Grund
            // muss zunächst ein "pop" gemacht werden.
            if (MdcHelper.liesKorrelationsId() != null) {
                NDC.pop();
            }
            NDC.push(val);
        }
        org.apache.log4j.MDC.put(key, val);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.MDCAdapter#get(java.lang.String)
     */
    @Override
    public String get(String key) {
        Object object = org.apache.log4j.MDC.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return (String) object;
        } else {
            return object.toString();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.MDCAdapter#remove(java.lang.String)
     */
    @Override
    public void remove(String key) {

        if (MdcHelper.MDC_KORRELATIONS_ID.equals(key)) {
            NDC.pop();
        }
        org.apache.log4j.MDC.remove(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.MDCAdapter#clear()
     */
    @Override
    public void clear() {
        org.apache.log4j.MDC.clear();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.MDCAdapter#getCopyOfContextMap()
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Map getCopyOfContextMap() {
        Map contextMap = org.apache.log4j.MDC.getContext();
        if (contextMap != null) {
            return new HashMap(contextMap);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.MDCAdapter#setContextMap(java.util.Map)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void setContextMap(Map contextMap) {

        // String/String ist durch die Beschreibung des Interface vorgegeben.
        @SuppressWarnings("unchecked")
        Map<String, String> contextMapString = (Map<String, String>) contextMap;
        org.apache.log4j.MDC.clear();

        for (String key : contextMapString.keySet()) {
            MDC.put(key, contextMap.get(key));
        }

    }

}
