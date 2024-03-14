package de.bund.bva.isyfact.logging.autoconfigure;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;

import de.bund.bva.isyfact.logging.util.LoggingMethodInterceptor;

@Configuration
@ConditionalOnProperty(value = "isy.logging.performancelogging.enabled", havingValue = "true")
@EnableLoadTimeWeaving
public class IsyPerformanceLoggingAutoConfiguration {

    String[] pointcuts = {"execution(public * *..gui..*Controller.*(..))",
                          "execution(public * *..service..*ServiceImpl.*(..))",
                          "execution(public * *..core..*Impl.*(..))",
                          "execution(public * *..persistence..*DaoImpl.*(..))", // TODO: Mit Spring Data?
                          "execution(@de.bund.bva.isyfact.logging.annotation.PerformanceLogging * *(..))"
    };

    @Bean
    public LoggingMethodInterceptor performanceLogInterceptor() {
        LoggingMethodInterceptor interceptor = new LoggingMethodInterceptor();

        interceptor.setLoggeDauer(true);
        interceptor.setLoggeAufruf(false);
        interceptor.setLoggeErgebnis(false);
        interceptor.setLoggeDaten(false);
        interceptor.setLoggeDatenBeiException(false);

        return interceptor;
    }

    @Bean
    public Advisor performanceLogAdvisor(LoggingMethodInterceptor performanceLogInterceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(String.join(" or ", pointcuts));
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, performanceLogInterceptor);
        advisor.setOrder(1000);

        return advisor;
    }

}
