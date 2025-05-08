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
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
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
