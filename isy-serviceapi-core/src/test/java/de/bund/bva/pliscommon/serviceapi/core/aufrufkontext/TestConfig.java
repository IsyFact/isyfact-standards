package de.bund.bva.pliscommon.serviceapi.core.aufrufkontext;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextFactoryImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import de.bund.bva.pliscommon.serviceapi.core.aop.test.AufrufKontextSstTestBean;
import de.bund.bva.pliscommon.serviceapi.core.aufrufkontext.helper.DebugAufrufKontextVerwalter;

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