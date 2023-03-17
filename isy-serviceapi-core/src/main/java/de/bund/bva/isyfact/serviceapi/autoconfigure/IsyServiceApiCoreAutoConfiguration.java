package de.bund.bva.isyfact.serviceapi.autoconfigure;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereitInterceptor;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.DefaultAufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.StelltAufrufKontextBereitInterceptor;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.AufrufKontextToFromAccessTokenStrategy;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.CreateAufrufKontextToStrategy;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.DefaultAufrufKontextToFromAccessTokenStrategy;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.DefaultCreateAufrufKontextToStrategy;
import de.bund.bva.isyfact.sicherheit.autoconfigure.IsySicherheitAutoConfiguration;

/**
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
@Configuration
@AutoConfigureAfter(IsySicherheitAutoConfiguration.class)
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
    @ConditionalOnBean({ AufrufKontextFactory.class, AufrufKontextVerwalter.class })
    public StelltAufrufKontextBereitInterceptor stelltAufrufKontextBereitInterceptor(
        AufrufKontextFactory factory,
        AufrufKontextVerwalter verwalter,
        AufrufKontextToResolver resolver) {
        return new StelltAufrufKontextBereitInterceptor(factory, verwalter, resolver);
    }

    @Bean
    @ConditionalOnBean(StelltAufrufKontextBereitInterceptor.class)
    public Advisor stelltAufrufKontextBereitAdvisor(StelltAufrufKontextBereitInterceptor interceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(
            "@annotation(de.bund.bva.isyfact.serviceapi.core.aufrufkontext.StelltAufrufKontextBereit) || @within(de.bund.bva.isyfact.serviceapi.core.aufrufkontext.StelltAufrufKontextBereit)");
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public AufrufKontextToResolver aufrufKontextToResolver() {
        return new DefaultAufrufKontextToResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public CreateAufrufKontextToStrategy aufrufKontextToFromAccessTokenStrategy () {
        return new DefaultCreateAufrufKontextToStrategy();
    }
}
