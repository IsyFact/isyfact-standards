package de.bund.bva.isyfact.logging.autoconfigure;


import de.bund.bva.isyfact.logging.config.AbstractBoundaryLoggerProperties;
import de.bund.bva.isyfact.logging.config.IsyLoggingApplicationLoggerProperties;
import de.bund.bva.isyfact.logging.config.IsyLoggingBoundaryLoggerProperties;
import de.bund.bva.isyfact.logging.config.IsyLoggingComponentLoggerProperties;
import de.bund.bva.isyfact.logging.util.LogApplicationListener;
import de.bund.bva.isyfact.logging.util.LoggingMethodInterceptor;
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

/**
 * Spring-Autokonfiguration für das Einbinden der Bibliothek isy-logging in den Spring-Kontext.
 */
@Configuration
@ConditionalOnProperty(name = "isy.logging.autoconfiguration.enabled", matchIfMissing = true)
@EnableAspectJAutoProxy
@EnableConfigurationProperties
public class IsyLoggingAutoConfiguration {

    /**
     * Erzeugt eine Bean für die Konfigurationsparameter des Application-Loggers.
     *
     * @return Bean isyLoggingApplicationLoggerProperties.
     */
    @Bean
    @ConfigurationProperties(prefix = "isy.logging.anwendung")
    public IsyLoggingApplicationLoggerProperties isyLoggingApplicationLoggerProperties() {
        return new IsyLoggingApplicationLoggerProperties();
    }

    /**
     * Erzeugt eine Bean für die Konfigurationsparameter des Boundary-Loggers.
     *
     * @return Bean isyLoggingBoundaryLoggerProperties.
     */
    @Bean
    @ConfigurationProperties(prefix = "isy.logging.boundary")
    public IsyLoggingBoundaryLoggerProperties isyLoggingBoundaryLoggerProperties() {
        return new IsyLoggingBoundaryLoggerProperties();
    }

    /**
     * Erzeugt eine Bean für die Konfigurationsparameter des Component-Loggers.
     *
     * @return Bean isyMessageKafkaHealthIndicatorProperties.
     */
    @Bean
    @ConfigurationProperties(prefix = "isy.logging.component")
    public IsyLoggingComponentLoggerProperties isyLoggingComponentLoggerProperties() {
        return new IsyLoggingComponentLoggerProperties();
    }

    /**
     * Listener zum Loggen beim Hochfahren/Herunterfahren.
     *
     * @param properties Parameter für die Konfiguration des Status-Loggers.
     * @return Listener zum Loggen beim Hochfahren/Herunterfahren.
     */
    @Bean
    public LogApplicationListener statusLogger(IsyLoggingApplicationLoggerProperties properties) {
        return new LogApplicationListener(properties.getTyp(), properties.getName(), properties.getVersion());
    }

    /**
     * Interceptor zum Loggen an Systemgrenzen.
     *
     * @param properties Parameter für die Konfiguration des Interceptors.
     * @return Interceptor zum Loggen an Systemgrenzen.
     */
    @Bean
    public LoggingMethodInterceptor boundaryLogInterceptor(IsyLoggingBoundaryLoggerProperties properties) {
        return createLoggingMethodInterceptor(properties);
    }

    /**
     * Interceptor zum Loggen an Komponentengrenzen.
     *
     * @param properties Parameter für die Konfiguration des Interceptors.
     * @return Interceptor zum Loggen an Komponentengrenzen.
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
     * Advisor für das Loggen an Systemgrenzen.
     *
     * @param properties             Properties für die Konfiguration des Pointcuts.
     * @param boundaryLogInterceptor Interceptor für das Logging.
     * @return Advisor für das Loggen an Systemgrenzen.
     */
    @Bean
    public Advisor boundaryLogAdvisorByAnnotation(IsyLoggingBoundaryLoggerProperties properties,
        @Qualifier("boundaryLogInterceptor") LoggingMethodInterceptor boundaryLogInterceptor) {
        return setupAdvisor(properties, boundaryLogInterceptor);
    }

    /**
     * Advisor für das Loggen an Komponentengrenzen.
     *
     * @param properties              Properties für die Konfiguration des Pointcuts.
     * @param componentLogInterceptor Interceptor für das Logging.
     * @return Advisor für das Loggen an Komponentengrenzen.
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
