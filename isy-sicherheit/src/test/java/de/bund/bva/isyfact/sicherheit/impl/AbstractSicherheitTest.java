package de.bund.bva.isyfact.sicherheit.impl;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.accessmgr.test.TestAccessManager;
import de.bund.bva.isyfact.sicherheit.config.IsySicherheitConfigurationProperties;
import de.bund.bva.isyfact.sicherheit.config.SicherheitTestConfig;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SicherheitTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test", "isy.logging.anwendung.version=test",
    "isy.sicherheit.cache.ttl=1"})
@DirtiesContext
public abstract class AbstractSicherheitTest {

    @Autowired
    protected Sicherheit sicherheit;

    @Autowired
    protected TestAccessManager testAccessManager;

    @Autowired
    protected AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    @Autowired
    protected AufrufKontextFactory<AufrufKontext> aufrufKontextFactory;

    @Autowired
    protected IsySicherheitConfigurationProperties isySicherheitConfigurationProperties;

    @Before
    public void setup() {
        this.aufrufKontextVerwalter.setAufrufKontext(null);
        this.testAccessManager.reset();
        this.sicherheit.leereCache();
        this.testAccessManager.setResultAuthentifiziere("Rolle_A", "Rolle_B");
    }

}
