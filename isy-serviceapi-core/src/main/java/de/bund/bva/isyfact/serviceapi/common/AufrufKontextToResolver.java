package de.bund.bva.isyfact.serviceapi.common;

import java.util.Optional;

import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Helper class to read AufrufKontextTo from a parameter-list.
 */
public class AufrufKontextToResolver {
    /**
     * Resolves {@link AufrufKontextTo} from an Object array.
     * Primary use is to read AufrufKontextTo from the parameter list of a service call.
     * The first found instance is returned.
     *
     * @param args
     *            arguments of the service call
     *
     * @return an {@link Optional} containing the AufrufKontextTo. Returns empty Optional, if no AufrufKontextTo was found.
     */
    public Optional<AufrufKontextTo> leseAufrufKontextTo(Object[] args) {

        if (args != null) {
            for (Object parameter : args) {
                if (parameter instanceof AufrufKontextTo) {
                    return Optional.of((AufrufKontextTo) parameter);
                }
            }
        }

        return Optional.empty();
    }
}
