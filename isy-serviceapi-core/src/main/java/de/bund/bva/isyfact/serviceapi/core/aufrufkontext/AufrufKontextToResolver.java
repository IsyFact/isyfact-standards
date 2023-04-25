package de.bund.bva.isyfact.serviceapi.core.aufrufkontext;

import java.util.Optional;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Resolver for reading AufrufKontextTo from a parameter List.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
@FunctionalInterface
public interface AufrufKontextToResolver {

    /**
     * Resolves {@link AufrufKontextTo} from an Object array.
     * Primary use is to read AufrufKontextTo from the parameter list of a service call.
     *
     * @param args
     *            arguments of the service call
     *
     * @return an {@link Optional} containing the AufrufKontextTo. Returns empty Optional, if no AufrufKontextTo was found.
     */
    Optional<AufrufKontextTo> leseAufrufKontextTo(Object[] args);
}
