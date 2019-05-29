package de.bund.bva.isyfact.serviceapi.autoconfigure;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereitInterceptor;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.StelltAufrufKontextBereitInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IsyServiceApiCoreAutoConfiguration {

    @Bean
    public StelltLoggingKontextBereitInterceptor stelltLoggingKontextBereitInterceptor() {
        return new StelltLoggingKontextBereitInterceptor();
    }

    @Bean
    public Advisor stelltLoggingKontextBereitAdvisor(StelltLoggingKontextBereitInterceptor interceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereit) || @within(de.bund.bva.isyfact.serviceapi.core.aop.StelltLoggingKontextBereit)");
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    @Bean
    @ConditionalOnBean({AufrufKontextFactory.class, AufrufKontextVerwalter.class})
    public StelltAufrufKontextBereitInterceptor stelltAufrufKontextBereitInterceptor(AufrufKontextFactory factory, AufrufKontextVerwalter verwalter) {
        return new StelltAufrufKontextBereitInterceptor(factory, verwalter);
    }

    @Bean
    @ConditionalOnBean(StelltAufrufKontextBereitInterceptor.class)
    public Advisor stelltAufrufKontextBereitAdvisor(StelltAufrufKontextBereitInterceptor interceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(de.bund.bva.isyfact.serviceapi.core.aufrufkontext.StelltAufrufKontextBereit) || @within(de.bund.bva.isyfact.serviceapi.core.aufrufkontext.StelltAufrufKontextBereit)");
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }
}
