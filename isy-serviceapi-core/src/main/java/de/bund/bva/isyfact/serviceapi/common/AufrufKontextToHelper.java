package de.bund.bva.isyfact.serviceapi.common;

import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.DefaultAufrufKontextToResolver;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Helperclass to resolve AufrufKontextTo from service parameters.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class AufrufKontextToHelper {

    /**
     * Loads the first {@link AufrufKontextTo} found from the parameters of the called function.
     *
     * @param args
     *            arguments of the service call
     *
     * @return the AufrufKontextTo object
     */
    public static AufrufKontextTo leseAufrufKontextTo(Object[] args) {
        return new DefaultAufrufKontextToResolver().leseAufrufKontextTo(args).orElse(null);
    }

}
