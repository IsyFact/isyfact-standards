package de.bund.bva.isyfact.polling.test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import de.bund.bva.isyfact.polling.PollingMBean;
import de.bund.bva.isyfact.polling.PollingVerwalter;

@Configuration
@EnableAutoConfiguration
public class TestConfig {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("resources/isy-polling/nachrichten/fehler");

        return messageSource;
    }

    @Bean
    public PollingMBean cluster1Monitor(PollingVerwalter pollingVerwalter) {
        PollingMBean mBean = new PollingMBean();
        mBean.setClusterId("CLUSTER1");
        mBean.setPollingVerwalter(pollingVerwalter);

        return mBean;
    }

    @Bean
    public PollingMBean cluster2Monitor(PollingVerwalter pollingVerwalter) {
        PollingMBean mBean = new PollingMBean();
        mBean.setClusterId("CLUSTER2");
        mBean.setPollingVerwalter(pollingVerwalter);

        return mBean;
    }

    @Bean
    public PollingAktionAusfuehrer pollingAktionAusfuehrer() {
        return new PollingAktionAusfuehrer();
    }
}
