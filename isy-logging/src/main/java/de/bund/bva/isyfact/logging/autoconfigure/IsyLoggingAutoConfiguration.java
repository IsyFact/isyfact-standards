package de.bund.bva.isyfact.logging.autoconfigure;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import de.bund.bva.isyfact.logging.config.AbstractBoundaryLoggerProperties;
import de.bund.bva.isyfact.logging.config.IsyLoggingApplicationLoggerProperties;
import de.bund.bva.isyfact.logging.config.IsyLoggingBoundaryLoggerProperties;
import de.bund.bva.isyfact.logging.config.IsyLoggingComponentLoggerProperties;
import de.bund.bva.isyfact.logging.util.LogApplicationListener;
import de.bund.bva.isyfact.logging.util.LoggingMethodInterceptor;

/**
 * Spring autoconfiguration for including the isy-logging library in the Spring context.
 */
@Configuration
@ConditionalOnProperty(name = "isy.logging.autoconfiguration.enabled", matchIfMissing = true)
@EnableAspectJAutoProxy
@EnableConfigurationProperties
public class IsyLoggingAutoConfiguration {

    /**
     * Creates a bean for the configuration parameters of the application logger.
     *
     * @return Bean isyLoggingApplicationLoggerProperties.
     */
    @Bean
    @ConfigurationProperties(prefix = "isy.logging.anwendung")
    public IsyLoggingApplicationLoggerProperties isyLoggingApplicationLoggerProperties() {
        return new IsyLoggingApplicationLoggerProperties();
    }

    /**
     * Creates a bean for the boundary logger configuration parameters.
     *
     * @return Bean isyLoggingBoundaryLoggerProperties.
     */
    @Bean
    @ConfigurationProperties(prefix = "isy.logging.boundary")
    public IsyLoggingBoundaryLoggerProperties isyLoggingBoundaryLoggerProperties() {
        return new IsyLoggingBoundaryLoggerProperties();
    }

    /**
     * Creates a bean for the configuration parameters of the component logger.
     *
     * @return Bean isyMessageKafkaHealthIndicatorProperties.
     */
    @Bean
    @ConfigurationProperties(prefix = "isy.logging.component")
    public IsyLoggingComponentLoggerProperties isyLoggingComponentLoggerProperties() {
        return new IsyLoggingComponentLoggerProperties();
    }

    /**
     * Listener for logging during startup/shutdown.
     *
     * @param properties Parameters for the configuration of the status logger.
     * @return Listener for logging during startup/shutdown.
     */
    @Bean
    public LogApplicationListener statusLogger(IsyLoggingApplicationLoggerProperties properties) {
        return new LogApplicationListener(properties.getName(), properties.getTyp(), properties.getVersion());
    }

    /**
     * Interceptor for logging at system boundaries.
     *
     * @param properties Parameters for the configuration of the interceptor.
     * @return Interceptor for logging at system boundaries.
     */
    @Bean
    public LoggingMethodInterceptor boundaryLogInterceptor(IsyLoggingBoundaryLoggerProperties properties) {
        return createLoggingMethodInterceptor(properties);
    }

    /**
     * Interceptor for logging at component boundaries.
     *
     * @param properties Parameters for the configuration of the interceptor.
     * @return Interceptor for logging at component boundaries.
     */
    @Bean
    public LoggingMethodInterceptor componentLogInterceptor(IsyLoggingComponentLoggerProperties properties) {
        return createLoggingMethodInterceptor(properties);
    }

    private LoggingMethodInterceptor createLoggingMethodInterceptor(AbstractBoundaryLoggerProperties properties) {
        LoggingMethodInterceptor interceptor;

        if (properties.getConverterExcludes().isEmpty() && properties.getConverterIncludes().isEmpty()) {
            interceptor = new LoggingMethodInterceptor();
        } else {
            interceptor = new LoggingMethodInterceptor(properties.getConverterIncludes(), properties.getConverterExcludes());
        }

        interceptor.setLoggeDauer(properties.isLoggeDauer());
        interceptor.setLoggeAufruf(properties.isLoggeAufruf());
        interceptor.setLoggeErgebnis(properties.isLoggeErgebnis());
        interceptor.setLoggeDaten(properties.isLoggeDaten());
        interceptor.setLoggeDatenBeiException(properties.isLoggeDatenBeiException());
        return interceptor;
    }

    /**
     * Advisor for logging at system boundaries.
     *
     * @param properties             Properties for the configuration of the pointcut.
     * @param boundaryLogInterceptor Interceptor for logging.
     * @return Advisor for logging at system boundaries.
     */
    @Bean
    public Advisor boundaryLogAdvisorByAnnotation(IsyLoggingBoundaryLoggerProperties properties,
        @Qualifier("boundaryLogInterceptor") LoggingMethodInterceptor boundaryLogInterceptor) {
        return setupAdvisor(properties, boundaryLogInterceptor);
    }

    /**
     * Advisor for logging at component boundaries.
     *
     * @param properties              Properties for the configuration of the pointcut.
     * @param componentLogInterceptor Interceptor for logging.
     * @return Advisor for logging at component boundaries.
     */
    @Bean
    public Advisor componentLogAdvisorByAnnotation(IsyLoggingComponentLoggerProperties properties,
        @Qualifier("componentLogInterceptor") LoggingMethodInterceptor componentLogInterceptor) {
        return setupAdvisor(properties, componentLogInterceptor);
    }

    private Advisor setupAdvisor(AbstractBoundaryLoggerProperties properties, MethodInterceptor interceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(properties.getPointcut());
        DefaultPointcutAdvisor adv = new DefaultPointcutAdvisor(pointcut, interceptor);
        adv.setOrder(1000);
        return adv;
    }

}
