package de.bund.bva.isyfact.serviceapi.core.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kennzeichnet Operationen einer Exception-Fassade, an die zwingend ein AufrufKontextTo übermittelt wird, aus
 * dem die Korrelations-ID als Logging-Kontext bereitgestellt wird. Ist kein AufrufKontextTo vorhanden, wird
 * eine Exception geworfen. Alternativ können auch Operationen ohne AufrufKontextTo gekennzeichnet werden.
 * Dann muss dies mit dem Flag nutzeAufrufKontext=false kenntlich gemacht werden.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
public @interface StelltLoggingKontextBereit {
    boolean nutzeAufrufKontext() default true;
}
