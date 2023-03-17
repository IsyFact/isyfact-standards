package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Strategy is used to generate {@link AufrufKontextTo} based on a targeted logic.
 */
@FunctionalInterface
public interface CreateAufrufKontextToStrategy {
    /**
     * The {@link AufrufKontextTo} is generated ad hoc using this method implementation.
     *
     * @return the newly created {@link AufrufKontextTo}
     */
    public abstract AufrufKontextTo create();
}
