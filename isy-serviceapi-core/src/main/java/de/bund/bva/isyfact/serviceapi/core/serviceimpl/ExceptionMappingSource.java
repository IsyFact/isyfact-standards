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

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.exception.service.PlisToException;

/**
 * Über diese Schnittstelle können die Abbildungsregeln für Exceptions in einer Serviceimplementierung
 * ermittelt werden.
 *
 */
public interface ExceptionMappingSource {

    /**
     * Ermittelt die Transport-Exceptionklasse (To-Exception) zu einer Exceptionklasse des Anwendungskerns.
     *
     * @param remoteBeanMethod
     *            die RemoteBean-Methode, in der die Exception geworfen wurde
     * @param exceptionClass
     *            die Klasse der im Anwendungskern geworfenen Exception
     * @return die Transport-Exceptionklasse
     */
    Class<? extends PlisToException> getToExceptionClass(
            Method remoteBeanMethod, Class<? extends BaseException> exceptionClass);

    /**
     * Ermittelt die generische, technische Transport-Exceptionklasse, auf die alle technischen Exceptions
     * abgebildet werden, für die keine spezifische Abbildungsregel existiert.
     *
     * @param remoteBeanMethod
     *            die RemoteBean-Methode, in der die Exception geworfen wurde
     * @return die generische, technische Transport-Exceptionklasse
     */
    Class<? extends PlisTechnicalToException> getGenericTechnicalToException(Method remoteBeanMethod);

}
