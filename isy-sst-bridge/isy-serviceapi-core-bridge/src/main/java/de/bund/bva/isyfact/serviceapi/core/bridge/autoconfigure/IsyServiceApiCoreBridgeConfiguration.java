package de.bund.bva.isyfact.serviceapi.core.bridge.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.serviceapi.autoconfigure.IsyServiceApiCoreAutoConfiguration;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.core.bridge.IF1OrIF2AufrufKontextToResolver;

@Configuration
@AutoConfigureBefore(IsyServiceApiCoreAutoConfiguration.class)
public class IsyServiceApiCoreBridgeConfiguration {
    @Bean
    @ConditionalOnMissingBean
    AufrufKontextToResolver aufrufKontextToResolver() {
        return new IF1OrIF2AufrufKontextToResolver();
    }
}
