package de.bund.bva.pliscommon.bridges.integration.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.serviceapi.core.httpinvoker.IsyHttpInvokerServiceExporter;
import de.bund.bva.pliscommon.bridges.integration.sst.PlisTestRemoteBean;
import de.bund.bva.pliscommon.bridges.integration.sst.impl.PlisTestExceptionFassade;

@Configuration
@EnableAutoConfiguration
public class TestServiceConfiguration {

    @Bean
    public PlisTestExceptionFassade dummyServiceFassade() {
        return new PlisTestExceptionFassade();
    }

    @Bean("/TestService")
    public IsyHttpInvokerServiceExporter testService(PlisTestRemoteBean dummyServiceFassade) {
        IsyHttpInvokerServiceExporter exporter = new IsyHttpInvokerServiceExporter();
        exporter.setService(dummyServiceFassade);
        exporter.setServiceInterface(PlisTestRemoteBean.class);
        return exporter;
    }

}
