package de.bund.bva.isyfact.serviceapi.core.aufrufkontext;

import java.util.Optional;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Helper class to read AufrufKontextTo from a parameter-list.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class DefaultAufrufKontextToResolver implements AufrufKontextToResolver {

    /**
     * {@inheritDoc}
     * The first found instance of {@link AufrufKontextTo} is returned.
     */
    @Override
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
