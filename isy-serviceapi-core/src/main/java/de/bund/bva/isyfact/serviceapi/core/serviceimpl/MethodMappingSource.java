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
package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.Method;

/**
 * Diese Schnittstelle bietet Operationen zum Ermitteln einer Zielmethode zu einer aufgerufenen Methode.
 * 
 */
public interface MethodMappingSource {

    /**
     * Ermittelt eine Zielmethode zu einer aufgerufenen Methode.
     * 
     * @param calledMethod
     *            die aufgerufene Methode
     * @param targetClass
     *            die Zielklasse, die die Zielmethode implementiert
     * 
     * @return die aufzurufende Methode der target-Bean
     */
    Method getTargetMethod(Method calledMethod, Class<?> targetClass);

    /**
     * Prüft, ob ein Parameter übersprungen, d.h. nicht an die Zielmethode weitergegeben werden soll.
     * 
     * @param parameterType
     *            der Parametertyp
     * 
     * @return ob der Parameter übersprungen werden soll
     */
    boolean skipParameter(Class<?> parameterType);

}
