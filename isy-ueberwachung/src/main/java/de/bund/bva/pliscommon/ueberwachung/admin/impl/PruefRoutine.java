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
package de.bund.bva.pliscommon.ueberwachung.admin.impl;

import java.util.concurrent.Callable;

/**
 * Kapselt eine Pruefroutine
 *
 * @author Capgemini, Simon Spielmann
 * @version $Id: PruefRoutine.java 128443 2015-01-20 14:50:43Z sdm_sspielmann $
 */
public class PruefRoutine {
    /** Beschreibung der Prüfung. */
    private String beschreibung;

    /** Implementierung der Prüfung. */
    private Callable<Boolean> pruefung;

    public PruefRoutine(String beschreibung, Callable<Boolean> pruefung) {
        this.beschreibung = beschreibung;
        this.pruefung = pruefung;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public Callable<Boolean> getPruefung() {
        return this.pruefung;
    }

}
