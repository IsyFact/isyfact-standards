package de.bund.bva.isyfact.task.test.config;

import de.bund.bva.isyfact.task.test.AccessManagerDummy;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextFactoryImpl;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.pliscommon.konfiguration.common.impl.ReloadablePropertyKonfiguration;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AccessManager;
import de.bund.bva.pliscommon.sicherheit.annotation.GesichertInterceptor;
import de.bund.bva.pliscommon.sicherheit.impl.SicherheitImpl;
import de.bund.bva.pliscommon.util.spring.MessageSourceHolder;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@EnableAutoConfiguration
public class TestConfig {

    @Bean
    public AccessManager accessManager() {
        return new AccessManagerDummy();
    }

    @Bean
    public Sicherheit sicherheit(AufrufKontextVerwalter aufrufKontextVerwalter, AufrufKontextFactory aufrufKontextFactory, AccessManager accessManager) {
        SicherheitImpl sicherheit = new SicherheitImpl();
        sicherheit.setAufrufKontextVerwalter(aufrufKontextVerwalter);
        sicherheit.setAufrufKontextFactory(aufrufKontextFactory);
        sicherheit.setKonfiguration(new ReloadablePropertyKonfiguration(new String[] {}));
        sicherheit.setAccessManager(accessManager);
        sicherheit.setRollenRechteDateiPfad("/sicherheit/rollenrechte.xml");

        return sicherheit;
    }

    @Bean
    public AufrufKontextVerwalter aufrufKontextVerwalter() {
        return new AufrufKontextVerwalterImpl();
    }

    @Bean
    public AufrufKontextFactory aufrufKontextFactory() {
        AufrufKontextFactoryImpl aufrufKontextFactory = new AufrufKontextFactoryImpl();
        aufrufKontextFactory.setAufrufKontextKlasse(AufrufKontextImpl.class);

        return aufrufKontextFactory;
    }

    // TODO: Nach Autokonfiguration isy-sicherheit entfernen
    @Bean
    public GesichertInterceptor gesichertInterceptor(Sicherheit sicherheit) {
        GesichertInterceptor gesichertInterceptor = new GesichertInterceptor();
        gesichertInterceptor.setSicherheit(sicherheit);

        return gesichertInterceptor;
    }

    // TODO: Nach Autokonfiguration isy-sicherheit entfernen
    @Bean
    public Advisor gesichertAdvisor(GesichertInterceptor gesichertInterceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(de.bund.bva.pliscommon.sicherheit.annotation.Gesichert) || @within(de.bund.bva.pliscommon.sicherheit.annotation.Gesichert)");
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, gesichertInterceptor);

        return advisor;
    }

    @Bean
    public MessageSourceHolder messageSourceHolder() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("resources/isy-task/nachrichten/fehler", "resources/isy-task/nachrichten/ereignisse", "resources/isy-task/nachrichten/hinweise");

        MessageSourceHolder messageSourceHolder = new MessageSourceHolder();
        messageSourceHolder.setMessageSource(messageSource);

        return messageSourceHolder;
    }
}
