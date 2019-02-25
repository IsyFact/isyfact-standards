package de.bund.bva.pliscommon.sicherheit.autoconfigure;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextFactoryImpl;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.annotation.GesichertInterceptor;
import de.bund.bva.pliscommon.sicherheit.config.IsySicherheitConfigurationProperties;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.web.context.request.RequestScope;

@Configuration
@EnableConfigurationProperties
@ConditionalOnBean(Sicherheit.class)
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
        pointcut.setExpression("@annotation(de.bund.bva.pliscommon.sicherheit.annotation.Gesichert) || @within(de.bund.bva.pliscommon.sicherheit.annotation.Gesichert)");
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    @Bean
    public AufrufKontextFactory aufrufKontextFactory() {
        return new AufrufKontextFactoryImpl<>();
    }

    @Bean
    public CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
        customScopeConfigurer.addScope("thread", new SimpleThreadScope());
        customScopeConfigurer.addScope("request", new RequestScope());

        return customScopeConfigurer;
    }

}
