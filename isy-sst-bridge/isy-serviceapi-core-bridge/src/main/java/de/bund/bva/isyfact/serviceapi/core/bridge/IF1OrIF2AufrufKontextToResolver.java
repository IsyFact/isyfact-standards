package de.bund.bva.isyfact.serviceapi.core.bridge;

import java.util.Optional;

import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Helper class to read AufrufKontextTo from a parameter-list.
 * Resolves IsyFact 2.x and IsyFact 1.x contexts.
 * IsyFact 1 contexts will be wrapped in a Facade to provide the IF2 interface.
 */
public class IF1OrIF2AufrufKontextToResolver implements AufrufKontextToResolver {

    /**
     * {@inheritDoc}
     * The first found instance of {@link AufrufKontextTo} is returned.
     * If none is present, search for instances of IF1-AufrufKontextTo
     * and make an IF2-AufrufKontextTo {@link AufrufKontextTo} that delegates to the IF2-AufrufKontextTo
     * <p>
     * If neither is found return empty Optional.
     */
    @Override
    public Optional<AufrufKontextTo> leseAufrufKontextTo(Object[] args) {

        if (args != null) {
            for (Object parameter : args) {
                if (parameter instanceof AufrufKontextTo) {
                    return Optional.of((AufrufKontextTo) parameter);
                }
            }
            // we only search for IF1 if no IF2 AufrufKontextTo was found
            // that's why it's a different for-loop
            for (Object parameter : args) {
                if (parameter instanceof de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo) {
                    de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo
                        aufrufKontextTo =
                        (de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo) parameter;
                    AufrufKontextTo delegate = new AufrufKontextToIF1Facade(aufrufKontextTo);
                    return Optional.of(delegate);
                }
            }
        }

        return Optional.empty();
    }
}
