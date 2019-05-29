package de.bund.bva.isyfact.serviceapi.core.aufrufkontext;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextFactoryImpl;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.helper.DebugAufrufKontextVerwalter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import de.bund.bva.isyfact.serviceapi.core.aop.test.AufrufKontextSstTestBean;

@Configuration
@EnableAutoConfiguration
public class TestConfig {

    @Bean
    public AufrufKontextVerwalter aufrufKontextVerwalter() {
        return new DebugAufrufKontextVerwalter();
    }

    @Bean
    public AufrufKontextFactory aufrufKontextFactory() {
        return new AufrufKontextFactoryImpl();
    }

    @Bean
    public AufrufKontextSstTestBean bereitgestellteSchnittstelle() {
        return new AufrufKontextSstTestBean();
    }
}