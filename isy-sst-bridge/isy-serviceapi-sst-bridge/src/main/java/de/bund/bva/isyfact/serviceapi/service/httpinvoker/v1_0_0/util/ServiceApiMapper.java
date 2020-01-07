package de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.util;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Utility-Klasse zum Mappen von AufrufKontextTo-Objekten vom 2.1-Namespace zum 1.8-Namespace
 * Nutzt für bessere Performance BoundedMapperFacade, da die zu mappenden Klassen vorher bekannt sind.
 */
public class ServiceApiMapper {


    /** Mapper für den AufrufKontextTo */
    private final BoundMapperFacade<de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo, AufrufKontextTo> aufrufKontextToMapper;
    /** Mapper für den ClientAufrufKontextTo */
    private final BoundMapperFacade<de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo, ClientAufrufKontextTo> clientAufrufKontextToMapper;

    public ServiceApiMapper() {

        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        // initialisieren der Mapper
        aufrufKontextToMapper = mapperFactory.getMapperFacade(
                        de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo.class,
                        AufrufKontextTo.class);
        clientAufrufKontextToMapper = mapperFactory.getMapperFacade(
                        de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo.class,
                        ClientAufrufKontextTo.class);
    }

    public AufrufKontextTo map(de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo isyAufrufKontextTo) {
        return aufrufKontextToMapper.map(isyAufrufKontextTo);
    }

    public ClientAufrufKontextTo map(de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo isyAufrufKontextTo) {
        return clientAufrufKontextToMapper.map(isyAufrufKontextTo);
    }

}
