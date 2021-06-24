package de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.bridge.util;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Utility class for mapping AufrufKontextTo between 2.x and 1.x namespaces.
 * Uses BoundedMapperFacade for better performance as the classes are known beforehand.
 */
public class ServiceApiMapper {


    /** Mapper for AufrufKontextTo */
    private final BoundMapperFacade<de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo, AufrufKontextTo> aufrufKontextToMapper;
    /** Mapper for ClientAufrufKontextTo */
    private final BoundMapperFacade<de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo, ClientAufrufKontextTo> clientAufrufKontextToMapper;

    public ServiceApiMapper() {

        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        // initialize mappers
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

    public de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo map( AufrufKontextTo plisAufrufKontextTo) {
        return aufrufKontextToMapper.mapReverse(plisAufrufKontextTo);
    }

    public ClientAufrufKontextTo map(de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo isyAufrufKontextTo) {
        return clientAufrufKontextToMapper.map(isyAufrufKontextTo);
    }

    public de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo map(ClientAufrufKontextTo plisAufrufKontextTo) {
        return clientAufrufKontextToMapper.mapReverse(plisAufrufKontextTo);
    }

}
