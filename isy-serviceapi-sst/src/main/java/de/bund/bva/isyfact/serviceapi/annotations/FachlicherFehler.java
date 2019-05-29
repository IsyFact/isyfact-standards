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
package de.bund.bva.isyfact.serviceapi.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kennzeichnet einen fachlichen Fehler. Wenn ein solches Objekt nicht <code>null</code> ist, oder eine
 * Collection vom Typ eines solchen Objekts Einträge enthält, gilt ein Methodenaufruf als fachlich fehlerhaft.
 * Die plis-ueberwachung kann diese Annotation dann auswerten, und die Fehler per JMX ausgeben.
 *
 * Die Annotation wird folgendermaßen verwendet:
 *
 * <ul>
 * <li>Für eine Datenklasse (z.B. einer TO-Klasse). Es wird die <strong>Fehlerklasse</strong> annotiert, nicht
 * deren Vorkommen in Attributen.</li>
 * </ul>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface FachlicherFehler {

}
