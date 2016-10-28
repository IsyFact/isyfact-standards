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
package de.bund.bva.pliscommon.exception.common.konstanten;

/**
 * Diese Klasse enthält Ereignisschlüssel für das Logging.
 */
public class EreignisSchluessel {

    /** Die TransportException implementiert nicht den benoetigten Konstruktor mit den Parametern... */
    public static final String KONSTRUKTOR_NICHT_IMPLEMENTIERT = "EPLEXC00001";

    /**
     * Die TransportException {} konnte nicht erzeugt werden. Sie ist entweder ein Interface oder aber eine
     * abstrakte Klasse. Sie TransportException muss aber eine konkrete Implementierung sein
     */
    public static final String TRANSPORT_EXCEPTION_INTERFACE_ABSTRAKT = "EPLEXC00002";

    /**
     * Die TransportException {} konnte nicht erzeugt werden. Die Parameterwerte ({}, {}, {}) entsprechen
     * nicht den benoetigten Werten: String message, String ausnahmeId, String uniqueId.
     */
    public static final String PARAMETER_FALSCH = "EPLEXC00003";

    /**
     * Der Zugriff auf den Konstruktur der TransportException {} verstoesst gegen die
     * Java-Sicherheitsrichtlinien.
     */
    public static final String KONSTRUKTOR_SICHERHEITSRICHTLINIEN = "EPLEXC00004";

    /**
     * Der Aufruf des Konstruktors der TransportException {} fuehrte innerhalb des aufgerufenen Konstruktors
     * zu einer Exception.
     */
    public static final String KONSTRUKTOR_EXCEPTION = "EPLEXC00005";
}
