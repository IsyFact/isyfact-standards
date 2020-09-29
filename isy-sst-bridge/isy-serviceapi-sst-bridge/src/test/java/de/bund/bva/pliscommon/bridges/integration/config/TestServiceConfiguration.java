package de.bund.bva.pliscommon.bridges.integration.config;

import de.bund.bva.pliscommon.bridges.integration.sst.PlisTestRemoteBean;
import de.bund.bva.pliscommon.bridges.integration.sst.impl.PlisTestExceptionFassade;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

@Configuration
@EnableAutoConfiguration
public class TestServiceConfiguration {

    @Bean
    public PlisTestExceptionFassade dummyServiceFassade() {
        return new PlisTestExceptionFassade();
    }

    @Bean("/TestService")
    public HttpInvokerServiceExporter testService(PlisTestRemoteBean dummyServiceFassade) {
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService(dummyServiceFassade);
        exporter.setServiceInterface(PlisTestRemoteBean.class);
        return exporter;
    }

}
