package de.bund.bva.isyfact.serviceapi.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.support.RemoteInvocationFactory;

import de.bund.bva.isyfact.security.core.Berechtigungsmanager;
import de.bund.bva.isyfact.security.autoconfigure.IsySecurityAutoConfiguration;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.AufrufKontextToRemoteInvocationFactory;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.CreateAufrufKontextToStrategy;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.DefaultCreateAufrufKontextToStrategy;

/**
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
@Configuration
@ConditionalOnClass(IsySecurityAutoConfiguration.class)
@AutoConfigureAfter(IsySecurityAutoConfiguration.class)
public class IsyServiceApiSecurityAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public CreateAufrufKontextToStrategy aufrufKontextToFromAccessTokenStrategy(Berechtigungsmanager berechtigungsmanager) {
        return new DefaultCreateAufrufKontextToStrategy(berechtigungsmanager);
    }

    @Bean
    @ConditionalOnMissingBean
    public RemoteInvocationFactory aufrufKontextToRemoteInvocationFactory(CreateAufrufKontextToStrategy createAufrufKontextToStrategy) {
        return new AufrufKontextToRemoteInvocationFactory(createAufrufKontextToStrategy);
    }
}
