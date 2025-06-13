package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.pliscommon.exception.service.PlisToException;

/**
 * Defines a mapping from a {@link BaseException} of the application
 * core to a {@link PlisToException} of the service interface.
 *@deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
@Target(ElementType.LOCAL_VARIABLE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {

    /**
     * The {@link BaseException} of the application core.
     */
    Class<? extends BaseException> exception();

    /**
     * The {@link PlisToException} of the service interface.
     */
    Class<? extends PlisToException> toException();

}
