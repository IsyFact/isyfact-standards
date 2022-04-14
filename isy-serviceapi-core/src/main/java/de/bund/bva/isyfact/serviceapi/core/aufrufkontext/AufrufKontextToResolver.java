package de.bund.bva.isyfact.serviceapi.core.aufrufkontext;

import java.util.Optional;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Resolver for reading AufrufKontextTo from a parameter List.
 */
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
