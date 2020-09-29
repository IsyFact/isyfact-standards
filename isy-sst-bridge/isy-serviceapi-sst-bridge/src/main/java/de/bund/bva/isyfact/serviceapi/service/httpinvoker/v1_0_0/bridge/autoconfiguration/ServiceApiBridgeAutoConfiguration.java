package de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.bridge.autoconfiguration;

import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.bridge.util.ServiceApiMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "isy.sst.serviceapibridge.autoconfiguration.enabled", matchIfMissing = true)
@EnableConfigurationProperties
public class ServiceApiBridgeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ServiceApiMapper.class)
    public ServiceApiMapper serviceApiMapper() {
        return new ServiceApiMapper();
    }

}
