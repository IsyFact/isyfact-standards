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
 * Determines mapping rules for exceptions defined via annotations.
 *
 * <p>
 * This class expects that an implementation package for the service interface
 * exists (package name = package name of the RemoteBean interface + ".impl"), and
 * that this package is annotated with {@link ExceptionMapping}.
 * </p>
 */
@Deprecated
public class AnnotationExceptionMappingSource implements ExceptionMappingSource {

    @Override
    public Class<? extends PlisToException> getToExceptionClass(
            Method remoteBeanMethod,
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

    @Override
    public Class<? extends PlisTechnicalToException> getGenericTechnicalToException(Method remoteBeanMethod) {

        Class<?> remoteBeanInterface = remoteBeanMethod.getDeclaringClass();

        ExceptionMapping exceptionMapping = getExceptionMapping(remoteBeanInterface);

        return exceptionMapping.technicalToException();
    }

    /**
     * Reads the {@link ExceptionMapping} annotation from the service's implementation package.
     *
     * @param remoteBeanInterface the RemoteBean interface
     * @return the annotation
     */
    private static ExceptionMapping getExceptionMapping(Class<?> remoteBeanInterface) {
        String implPackageName = remoteBeanInterface.getPackage().getName() + ".impl";
        Package implPackage = Package.getPackage(implPackageName);
        if (implPackage == null) {
            throw new IllegalStateException("Für den Service " + remoteBeanInterface.getName()
                    + " fehlt das erwartete Implementierungspackage " + implPackageName);
        }
        return implPackage.getAnnotation(ExceptionMapping.class);
    }

}
