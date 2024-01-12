package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Strategy is used to generate {@link AufrufKontextTo} based on a targeted logic.
 *
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
@FunctionalInterface
public interface CreateAufrufKontextToStrategy {
    /**
     * The {@link AufrufKontextTo} is generated ad hoc using this method implementation.
     *
     * @return the newly created {@link AufrufKontextTo}
     */
    public abstract AufrufKontextTo create();
}
