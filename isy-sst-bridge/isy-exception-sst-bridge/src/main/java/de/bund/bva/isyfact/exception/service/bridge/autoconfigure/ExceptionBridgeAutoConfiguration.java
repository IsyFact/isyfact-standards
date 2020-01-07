package de.bund.bva.isyfact.exception.service.bridge.autoconfigure;

import de.bund.bva.isyfact.exception.service.bridge.util.BridgeExceptionMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "isy.sst.exceptionbridge.autoconfiguration.enabled", matchIfMissing = true)
@EnableConfigurationProperties
public class ExceptionBridgeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(BridgeExceptionMapper.class)
    public BridgeExceptionMapper serviceApiMapper() {
        return new BridgeExceptionMapper();
    }

}
