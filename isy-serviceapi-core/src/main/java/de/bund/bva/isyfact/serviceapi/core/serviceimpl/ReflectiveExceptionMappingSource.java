package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.exception.service.PlisToException;

/**
 * Determines the mapping of exceptions by reflection.
 *
 * <p>
 * This class expects that for each AWK exception exactly one associated TO exception is declared in the
 * RemoteBean operation, differing only in the name suffix "ToException" (vs. "Exception").
 * Furthermore, it expects that in the RemoteBean operation exactly one TechnicalToException
 * is declared in the RemoteBean operation, which acts as a generic technical exception.
 * </p>
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class ReflectiveExceptionMappingSource implements ExceptionMappingSource {

    @Override
    public Class<? extends PlisToException> getToExceptionClass(Method remoteBeanMethod,
                                                                Class<? extends BaseException> exceptionClass) {

        String coreExceptionName = removeEnd(exceptionClass.getSimpleName(), "Exception");
        if (coreExceptionName != null) {
            for (Class<?> toExceptionClass : remoteBeanMethod.getExceptionTypes()) {
                if (PlisToException.class.isAssignableFrom(toExceptionClass)) {
                    String toExceptionName = strip(toExceptionClass.getSimpleName(), "Plis", "ToException");
                    if (coreExceptionName.equals(toExceptionName)) {
                        @SuppressWarnings("unchecked")
                        Class<? extends PlisToException> castToExceptionClass =
                                (Class<? extends PlisToException>) toExceptionClass;
                        return castToExceptionClass;
                    }
                }
            }
        }
        throw new IllegalStateException("Keine TO-Exception für die AWK-Exception " + exceptionClass
                + " in Serviceoperation " + getMethodSignatureString(remoteBeanMethod));
    }

    @Override
    public Class<? extends PlisTechnicalToException> getGenericTechnicalToException(Method remoteBeanMethod) {
        Class<? extends PlisTechnicalToException> genericTechnicalToException = null;
        for (Class<?> toExceptionClass : remoteBeanMethod.getExceptionTypes()) {
            if (PlisTechnicalToException.class.isAssignableFrom(toExceptionClass)) {
                if (genericTechnicalToException != null) {
                    throw new IllegalStateException(
                            "Mehr als eine Technical-TO-Exception in Serviceoperation "
                                    + getMethodSignatureString(remoteBeanMethod));
                }
                @SuppressWarnings("unchecked")
                Class<? extends PlisTechnicalToException> castToExceptionClass =
                        (Class<? extends PlisTechnicalToException>) toExceptionClass;
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
     * @param method the method
     * @return the method signature as a string.
     */
    protected String getMethodSignatureString(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    private static String strip(@Nullable String str, @Nullable String prefix, @Nullable String suffix) {
        return removeEnd(removeStart(str, prefix), suffix);
    }

    private static String removeStart(@Nullable String str, @Nullable String remove) {
        if (str == null || str.isEmpty() || remove == null || remove.isEmpty()) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    private static String removeEnd(@Nullable String str, @Nullable String remove) {
        if (str == null || str.isEmpty() || remove == null || remove.isEmpty()) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }
}
