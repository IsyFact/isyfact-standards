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
package de.bund.bva.pliscommon.ueberwachung.common.jmx;

import java.util.Date;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Diese MBean zeigt den Status des Systems an. Sie wird durch den Watchdog
 * regelmäßig aktualisiert.
 * 
 * 
 */
@ManagedResource(description = "Zeigt den Status der Anwendung an.")
public class StatusMonitorMBean {
    /** Gibt an ob die letzte Prüfung des Systems erfolgreich war oder nicht. */
    private boolean letztePruefungErfolgreich;

    /** Gibt den Zeitpunkt an, an dem die letzte Prüfung durchgeführt wurde. */
    private Date zeitpunktLetztePruefung;

    /**
     * Aktualisiert den Status der letzten Systemprüfung. Dabei wird das
     * Übergebene Ergebnis gespeichert und der Zeitstempel der Prüfung
     * aktualisiert.
     * @param erfolgreich
     *            Gibt an, ob die Pr�fung erfolgreich war (true) oder nicht
     *            (false).
     */
    public void registrierePruefung(boolean erfolgreich) {
        this.letztePruefungErfolgreich = erfolgreich;
        zeitpunktLetztePruefung = new Date(System.currentTimeMillis());
    }

    /**
     * Management Methode, stellt über JMX das Ergebnis der letzten
     * Systemprüfung bereit.
     * @return <code>true</code> falls, die letzte Pr�fung erfolgreich war,
     *         <code>false</code> falls nicht.
     */
    @ManagedAttribute(description = "Gibt an, ob die letzte Systemprüfung erfolgreich war (true) "
            + "oder dabei ein Fehler aufgetreten ist (false).")
    public boolean isLetztePruefungErfolgreich() {
        return letztePruefungErfolgreich;
    }

    /**
     * Management Methode, stellt über JMX den Zeitpunkt der letzten
     * Systemprüfung bereit.
     * 
     * @return Der Zeitpunkt der letzten Prüfung.
     */
    @ManagedAttribute(description = "Zeitpunkt der letzten Prüfung.")
    public Date getZeitpunktLetztePruefung() {
        return zeitpunktLetztePruefung;
    }

}
