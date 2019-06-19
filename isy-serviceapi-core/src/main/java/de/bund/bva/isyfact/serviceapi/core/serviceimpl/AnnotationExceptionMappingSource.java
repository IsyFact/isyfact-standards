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
 * Ermittelt Abbildungsregeln für Exceptions, die über Annotationen definiert sind.
 * 
 * <p>
 * Diese Klasse erwartet, dass ein Implementierungspackage für die Service-Schnittstelle existiert
 * (Packagename = Packagename der RemoteBean-Schnittstelle + ".impl"), und dass dieses Package mit der
 * Annotation {@link ExceptionMapping} versehen ist.
 * </p>
 * 
 */
public class AnnotationExceptionMappingSource implements ExceptionMappingSource {

    /**
     * {@inheritDoc}
     */
    public Class<? extends ToException> getToExceptionClass(Method remoteBeanMethod,
        Class<? extends BaseException> exceptionClass) {

        Class<?> remoteBeanInterface = remoteBeanMethod.getDeclaringClass();

        ExceptionMapping exceptionMapping = getExceptionMapping(remoteBeanInterface);

        for (Mapping mapping : exceptionMapping.mappings()) {
            if (mapping.exception().equals(exceptionClass)) {
                return mapping.toException();
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends TechnicalToException> getGenericTechnicalToException(Method remoteBeanMethod) {

        Class<?> remoteBeanInterface = remoteBeanMethod.getDeclaringClass();

        ExceptionMapping exceptionMapping = getExceptionMapping(remoteBeanInterface);

        return exceptionMapping.technicalToException();
    }

    /**
     * Liest die {@link ExceptionMapping}-Annotation aus dem Implementierungspackage des Services.
     * 
     * @param remoteBeanInterface
     *            das RemoteBean-Interface
     * @return die Annotation
     */
    private ExceptionMapping getExceptionMapping(Class<?> remoteBeanInterface) {
        String implPackageName = remoteBeanInterface.getPackage().getName() + ".impl";
        Package implPackage = Package.getPackage(implPackageName);
        if (implPackage == null) {
            throw new IllegalStateException("Für den Service " + remoteBeanInterface.getName()
                + " fehlt das erwartete Implementierungspackage " + implPackageName);
        }
        ExceptionMapping exceptionMapping = implPackage.getAnnotation(ExceptionMapping.class);
        return exceptionMapping;
    }

}
