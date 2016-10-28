package org.slf4j.impl;

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

import org.slf4j.spi.MDCAdapter;

import de.bund.bva.isyfact.log4jbridge.Log4jDiagnosticContextAdapter;

/**
 * StaticMDCBinder zum Zugriff auf den MDC-Adapter der Bridge. Sie wird direkt von der SLF4J-API genutzt um
 * eine Instanz des MDC-Adapters zu erhalten.
 */
public class StaticMDCBinder {

    /**
     * Singleton der Klasse, die SLF4J bereitgestellt wird. SLf4J greift direkt auf das Attribut zu - nicht den
     * getter.
     */
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();
    
    /** Der zu verwendende MDCAdapter. */
    private final MDCAdapter mdcAdapter = new Log4jDiagnosticContextAdapter();

    /**
     * Liefert den MDC-Adapter.
     * 
     * @return der verwendete MDC-Adapter.
     */
    public MDCAdapter getMDCA() {
        return mdcAdapter;
    }

    /**
     * Liefert den Klassennamen des Adapters.
     * 
     * @return den Klassennamen.
     */
    public String getMDCAdapterClassStr() {
        return Log4jDiagnosticContextAdapter.class.getName();
    }

    /**
     * Liefert den Wert des Attributs 'singleton'.
     * 
     * @return Wert des Attributs.
     */
    public static StaticMDCBinder getSingleton() {
        return SINGLETON;
    }
}
