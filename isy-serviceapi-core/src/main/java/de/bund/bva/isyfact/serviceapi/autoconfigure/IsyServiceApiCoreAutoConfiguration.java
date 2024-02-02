package de.bund.bva.isyfact.serviceapi.autoconfigure;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereitInterceptor;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.DefaultAufrufKontextToResolver;

/**
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
@Configuration
public class IsyServiceApiCoreAutoConfiguration {

    @Bean
    public StelltLoggingKontextBereitInterceptor stelltLoggingKontextBereitInterceptor(AufrufKontextToResolver aufrufKontextToResolver) {
        return new StelltLoggingKontextBereitInterceptor(aufrufKontextToResolver);
    }

    @Bean
    public Advisor stelltLoggingKontextBereitAdvisor(StelltLoggingKontextBereitInterceptor interceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(
            "@annotation(de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereit) || @within(de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereit)");
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, interceptor);
        advisor.setOrder(50);
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean
    public AufrufKontextToResolver aufrufKontextToResolver() {
        return new DefaultAufrufKontextToResolver();
    }
}
