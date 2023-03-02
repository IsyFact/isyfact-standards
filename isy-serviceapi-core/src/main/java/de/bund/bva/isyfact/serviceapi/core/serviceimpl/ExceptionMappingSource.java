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
 * This interface can be used to determine the mapping rules for exceptions in a service implementation.
 */
@Deprecated
public interface ExceptionMappingSource {

    /**
     * Determines the transport exception class (To-Exception) for an exception class of the application core.
     *
     * @param remoteBeanMethod the RemoteBean method in which the exception was thrown
     * @param exceptionClass   the class of the exception thrown in the application core.
     * @return the transport exception class
     */
    Class<? extends PlisToException> getToExceptionClass(
            Method remoteBeanMethod, Class<? extends BaseException> exceptionClass);

    /**
     * Determines the generic, technical transport exception class to which all technical exceptions are
     * mapped for which no specific mapping rule exists.
     *
     * @param remoteBeanMethod the RemoteBean method in which the exception was thrown
     * @return the generic, technical transport exception class
     */
    Class<? extends PlisTechnicalToException> getGenericTechnicalToException(Method remoteBeanMethod);

}
