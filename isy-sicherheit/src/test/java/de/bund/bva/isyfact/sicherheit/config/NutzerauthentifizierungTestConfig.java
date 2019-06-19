package de.bund.bva.isyfact.sicherheit.config;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.isyfact.sicherheit.annotation.NutzerAuthentifizierungInterceptor;
import de.bund.bva.isyfact.sicherheit.annotation.SicherheitStub;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
public class NutzerauthentifizierungTestConfig extends SicherheitTestConfig {

    @Bean
    public AufrufKontextVerwalter aufrufKontextVerwalter() {
        return new AufrufKontextVerwalterImpl();
    }

    @Bean
    public SicherheitStub sicherheitStub(AufrufKontextVerwalter aufrufKontextVerwalter) {
        SicherheitStub sicherheitStub = new SicherheitStub();
        sicherheitStub.setAufrufKontextVerwalter(aufrufKontextVerwalter);
        return sicherheitStub;
    }

    @Bean
    public NutzerAuthentifizierungInterceptor nutzerAuthentifizierungInterceptor(SicherheitStub sicherheitStub, AufrufKontextVerwalter aufrufKontextVerwalter, NutzerAuthentifizierungProperties nutzerAuthentifizierungProperties) {
        return new NutzerAuthentifizierungInterceptor(aufrufKontextVerwalter, nutzerAuthentifizierungProperties, sicherheitStub);
    }

    @Bean
    public Advisor NutzerAuthentifizierungAdvisor(NutzerAuthentifizierungInterceptor interceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(de.bund.bva.isyfact.sicherheit.annotation.NutzerAuthentifizierung) || @within(de.bund.bva.isyfact.sicherheit.annotation.NutzerAuthentifizierung)");
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, interceptor);
        return advisor;
    }

    @Bean
    @ConfigurationProperties(prefix = "isy.sicherheit.nutzerauthentifizierung")
    public NutzerAuthentifizierungProperties nutzerAuthentifizierungProperties() {
        return new NutzerAuthentifizierungProperties();
    }
}
