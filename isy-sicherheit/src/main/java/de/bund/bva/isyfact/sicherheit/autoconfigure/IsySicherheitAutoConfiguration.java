package de.bund.bva.isyfact.sicherheit.autoconfigure;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.web.context.request.RequestScope;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextFactoryImpl;
import de.bund.bva.isyfact.security.core.Berechtigungsmanager;
import de.bund.bva.isyfact.security.core.Security;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.SicherheitAdmin;
import de.bund.bva.isyfact.sicherheit.accessmgr.AccessManager;
import de.bund.bva.isyfact.sicherheit.annotation.GesichertInterceptor;
import de.bund.bva.isyfact.sicherheit.config.IsySicherheitConfigurationProperties;
import de.bund.bva.isyfact.sicherheit.impl.IsySecurityBerechtigungsmanagerImpl;
import de.bund.bva.isyfact.sicherheit.impl.IsySecurityImpl;
import de.bund.bva.isyfact.sicherheit.impl.SicherheitAdminImpl;

/**
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnBean(Sicherheit.class)
@Deprecated
public class IsySicherheitAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "isy.sicherheit.cache")
    public IsySicherheitConfigurationProperties isySicherheitConfigurationProperties() {
        return new IsySicherheitConfigurationProperties();
    }

    @Bean
    public GesichertInterceptor gesichertInterceptor(Sicherheit<?> sicherheit) {
        return new GesichertInterceptor(sicherheit);
    }

    @Bean
    public Advisor gesichertAdvisor(GesichertInterceptor interceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(de.bund.bva.isyfact.sicherheit.annotation.Gesichert) || @within(de.bund.bva.isyfact.sicherheit.annotation.Gesichert)");
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    @Bean
    @ConditionalOnMissingBean(AufrufKontextFactory.class)
    public AufrufKontextFactory aufrufKontextFactory() {
        return new AufrufKontextFactoryImpl<>();
    }

    @Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
        customScopeConfigurer.addScope("thread", new SimpleThreadScope());
        customScopeConfigurer.addScope("request", new RequestScope());

        return customScopeConfigurer;
    }

    @Bean
    @ConditionalOnBean(AccessManager.class)
    public SicherheitAdmin sicherheitAdmin(AccessManager accessManager) {
        return new SicherheitAdminImpl(accessManager);
    }

    @Bean
    @ConditionalOnMissingBean(Security.class)
    public Security isySecurity(Sicherheit<AufrufKontext> sicherheit) {
        return new IsySecurityImpl(sicherheit);
    }

    @Bean
    @ConditionalOnMissingBean(Berechtigungsmanager.class)
    public Berechtigungsmanager isySecurityBerechtigungsmanager(Sicherheit<AufrufKontext> sicherheit) {
        return new IsySecurityBerechtigungsmanagerImpl(sicherheit);
    }
}
