package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.exception.service.PlisToException;

/**
 * Defines how exceptions of the application core are mapped to TO exceptions in a service component.
 * This annotation must be used in the implementation package
 * of the service component (package name = package name of the RemoteBean interface + ".impl").
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionMapping {

    /**
     * The technical TO exception to which all {@link TechnicalRuntimeException} and all unchecked exceptions are mapped.
     */
    Class<? extends PlisTechnicalToException> technicalToException();

    /**
     * Images from {@link BaseException Exceptions} at {@link PlisToException PlisToExceptions}.
     */
    Mapping[] mappings();
}
