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
import de.bund.bva.isyfact.exception.service.TechnicalToException;
import de.bund.bva.isyfact.exception.service.ToException;

/**
 * Determines the mapping of exceptions via reflection.
 *
 * <p>
 * This class expects that for each AWK-Exception there is exactly one corresponding TO-Exception declared
 * in the RemoteBean operation, which only differs in the name suffix "ToException" (vs. "Exception").
 * Furthermore, it expects that exactly one TechnicalToException is declared in the RemoteBean operation,
 * which acts as a generic technical exception.
 * </p>
 *
 */
public class ReflectiveExceptionMappingSource implements ExceptionMappingSource {

    /**
     * {@inheritDoc}
     */
    public Class<? extends ToException> getToExceptionClass(Method remoteBeanMethod, Class<? extends BaseException> exceptionClass) {
        if (remoteBeanMethod == null || exceptionClass == null) {
            throw new IllegalArgumentException("Methoden- und Exception-Parameter dürfen nicht null sein.");
        }

        String coreExceptionName = removeEnd(exceptionClass.getSimpleName(), "Exception");
        if (coreExceptionName == null || coreExceptionName.isEmpty()) {
            throw new IllegalStateException("Die Ausnahme " + exceptionClass.getSimpleName() +
                    " hat keinen gültigen Kernnamen nach dem Entfernen von 'Exception'.");
        }

        for (Class<?> toExceptionClass : remoteBeanMethod.getExceptionTypes()) {
            if (ToException.class.isAssignableFrom(toExceptionClass)) {
                String toExceptionName = removeEnd(toExceptionClass.getSimpleName(), "ToException");
                if (coreExceptionName.equals(toExceptionName)) {
                    // Ensure that toExceptionClass really is a ToException
                    return toExceptionClass.asSubclass(ToException.class); // Safer than the cast and no warning
                }
            }
        }

        throw new IllegalStateException("Keine TO-Exception für die AWK-Exception " + exceptionClass
                + " in der Serviceoperation " + getMethodSignatureString(remoteBeanMethod));
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends TechnicalToException> getGenericTechnicalToException(Method remoteBeanMethod) {
        Class<? extends TechnicalToException> genericTechnicalToException = null;
        for (Class<?> toExceptionClass : remoteBeanMethod.getExceptionTypes()) {
            if (TechnicalToException.class.isAssignableFrom(toExceptionClass)) {
                if (genericTechnicalToException != null) {
                    throw new IllegalStateException(
                        "Mehr als eine Technical-TO-Exception in Serviceoperation "
                            + getMethodSignatureString(remoteBeanMethod));
                }
                @SuppressWarnings("unchecked")
                Class<? extends TechnicalToException> castToExceptionClass =
                    (Class<? extends TechnicalToException>) toExceptionClass;
                genericTechnicalToException = castToExceptionClass;
            }
        }

        if (genericTechnicalToException != null) {
            return genericTechnicalToException;
        }

        throw new IllegalStateException("Keine Technical-TO-Exception in Serviceoperation "
            + getMethodSignatureString(remoteBeanMethod));
    }

    /**
     * Returns the method signature as a string.
     *
     * @param method
     *            the method
     * @return the method signature as a string.
     */
    protected String getMethodSignatureString(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    private static String removeEnd(String str, String remove) {
        if (str == null || str.length() == 0 || remove == null || remove.length() == 0) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

}
