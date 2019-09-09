package de.bund.bva.isyfact.logging;

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

import java.util.Collection;

import org.slf4j.Marker;

/**
 * Marker zum Kennzeichnen von Logeinträgen mit definierten Attributen. Der IsyFact-spezifische Marker erlaubt
 * die Angabe eines Werts zusätzlich zum Namen des Markers.
 * 
 */
public interface IsyMarker extends Marker {

    /**
     * Isy-Logging verwendet einen "Wurzel-Marker" in dem weitere Marker gesammelt werden selbst aber keine
     * relevanten Log-Informationen (Name/Wert) enthält. Dieses Flag gibt an, ob es sich um einen Root-Marker
     * handelt.
     * 
     * @return <code>true</code> falls es sich um einen Root-Marker handelt, <code>false</code> sonst.
     */
    public boolean isRootMarker();

    /**
     * Liefert den Wert des Markers.
     * 
     * @return der Wert des Markers.
     */
    public String getValue();

    /**
     * Ergänze mehrere Referenzen zu anderen Markern.
     * 
     * @param references
     *            Referenzen zu anderen Markern.
     */
    public void addAll(Collection<Marker> references);

}
