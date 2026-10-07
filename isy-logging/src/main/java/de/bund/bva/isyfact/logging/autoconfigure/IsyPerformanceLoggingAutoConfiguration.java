package de.bund.bva.isyfact.logging.autoconfigure;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.logging.util.AspectJUtils;
import de.bund.bva.isyfact.logging.util.LoggingMethodInterceptor;
import de.bund.bva.isyfact.logging.util.PerformanceLoggingAspect;


@Configuration
public class IsyPerformanceLoggingAutoConfiguration {

    /** Pointcuts for performance logging of standard application layers. */
    private final String[] pointcuts = {"execution(public * *..gui..*Controller.*(..))",
        "execution(public * *..service..*ServiceImpl.*(..))",
        "execution(public * *..core..*Impl.*(..))",
        "execution(public * *..persistence..*DaoImpl.*(..))", // TODO: Mit Spring Data?
        "execution(@de.bund.bva.isyfact.logging.annotation.PerformanceLogging * *(..))"
    };

    @ConditionalOnProperty(value = "isy.logging.performancelogging.enabled", havingValue = "true")
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

    @ConditionalOnProperty(value = "isy.logging.performancelogging.enabled", havingValue = "true")
    @Bean
    public Advisor performanceLogAdvisor(@Qualifier("performanceLogInterceptor") LoggingMethodInterceptor performanceLogInterceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(String.join(" or ", pointcuts));
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, performanceLogInterceptor);
        advisor.setOrder(1000);

        return advisor;
    }

    @Bean
    public PerformanceLoggingAspect performanceLoggingAspect(
            @Value("${isy.logging.performancelogging.enabled:false}") boolean enabled
    ) {
        PerformanceLoggingAspect aspect = AspectJUtils.aspectOf(PerformanceLoggingAspect.class);
        aspect.setEnabled(enabled);
        return aspect;
    }

}
