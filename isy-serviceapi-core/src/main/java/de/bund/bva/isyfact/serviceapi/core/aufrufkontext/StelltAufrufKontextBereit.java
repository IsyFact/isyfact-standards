package de.bund.bva.isyfact.serviceapi.core.aufrufkontext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kennzeichnet Operationen einer Service-Implementierung, an die ein AufrufKontextTo übermittelt wird, das
 * als AufrufKontext bereitgestellt wird.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
public @interface StelltAufrufKontextBereit {
}
