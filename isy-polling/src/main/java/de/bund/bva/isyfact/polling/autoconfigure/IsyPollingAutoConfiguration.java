package de.bund.bva.isyfact.polling.autoconfigure;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import de.bund.bva.isyfact.polling.PollingVerwalter;
import de.bund.bva.isyfact.polling.annotation.PollingAktionInterceptor;
import de.bund.bva.isyfact.polling.config.IsyPollingProperties;
import de.bund.bva.isyfact.polling.impl.PollingVerwalterImpl;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties
public class IsyPollingAutoConfiguration {


    @Bean
    @ConfigurationProperties(prefix = "isy.polling")
    public IsyPollingProperties isyPollingProperties() {
        return new IsyPollingProperties();
    }

    @Bean
    public PollingVerwalter pollingVerwalter(IsyPollingProperties isyPollingProperties) {
        PollingVerwalterImpl pollingVerwalter = new PollingVerwalterImpl();
        pollingVerwalter.setIsyPollingProperties(isyPollingProperties);
        return pollingVerwalter;
    }

    @Bean
    public Advisor pollingAktionInterceptor(PollingVerwalter pollingVerwalter) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(
            "@annotation(de.bund.bva.isyfact.polling.annotation.PollingAktion) || @within(de.bund.bva.isyfact.polling.annotation.PollingAktion)");
        return new DefaultPointcutAdvisor(pointcut, new PollingAktionInterceptor(pollingVerwalter));
    }

}
