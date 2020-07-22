package de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.bridge.util;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo;

/**
 * Utility-Klasse zum Mappen von AufrufKontextTo-Objekten vom 1.8-Namespace zum 2.1-Namespace
 * Nutzt für bessere Performance BoundedMapperFacade, da die zu mappenden Klassen vorher bekannt sind.
 */
public class ServiceApiMapper {

    /** Der DozerBeanMapper. **/
    private final Mapper mapper;

    public ServiceApiMapper() {
        // initialisieren der Mapper
        mapper = DozerBeanMapperBuilder.buildDefault();
    }

    public AufrufKontextTo map(de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo plisAufrufKontextTo) {
        if (plisAufrufKontextTo == null) return null;
        return mapper.map(plisAufrufKontextTo, AufrufKontextTo.class);
    }

    public ClientAufrufKontextTo map(de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo plisAufrufKontextTo) {
        if (plisAufrufKontextTo == null) return null;
        return mapper.map(plisAufrufKontextTo, ClientAufrufKontextTo.class);
    }

}
