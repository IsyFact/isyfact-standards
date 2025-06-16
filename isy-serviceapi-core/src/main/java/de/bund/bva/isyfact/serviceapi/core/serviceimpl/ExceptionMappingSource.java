package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.Method;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.exception.service.PlisToException;

/**
 * This interface can be used to determine the mapping rules for exceptions in a service implementation.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
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
