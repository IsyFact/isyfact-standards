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
package de.bund.bva.pliscommon.util.monitoring;

import java.lang.reflect.Method;

/**
 * Interface, welches zu einer (gerade ausgeführten) Methode den Performance-Tag-Namen liefert.
 * 
 *
 */
public interface PerformanceTagSource {
    
    /**
     * Liefert den Namen des Performance-Tags für die angegebene Methode.
     * @param method Gemessene Methode.
     * @param targetClass Klasse, für den die Methode ausgeführt wird.
     * @return Name des Tags.
     */
    public String getTag(Method method, Class<? extends Object> targetClass);
}
