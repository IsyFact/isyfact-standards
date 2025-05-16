package de.bund.bva.isyfact.sicherheit.web;

import java.util.Arrays;
import java.util.Collection;

import de.bund.bva.isyfact.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.isyfact.sicherheit.config.SicherheitTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testet, dass DelegatingAccessDecisionManagerTest Anfragen korrekt an {@link BerechtigungsManager}
 * weiterleitet.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SicherheitTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test",
    "isy.logging.anwendung.version=test" })
public class DelegatingAccessDecisionManagerTest {

    @SuppressWarnings("rawtypes")
    @Autowired
    private AufrufKontextVerwalterStub aufrufKontextVerwalter;

    @Autowired
    private AccessDecisionManager accessDecisionManager;

    @Test
    public void testDecideRechtPositiv() {
        Collection<ConfigAttribute> attributes =
            Arrays.asList((ConfigAttribute) new StringConfigAttribute("Recht_A"));
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_A" });
        this.accessDecisionManager.decide(null, null, attributes);
    }

    @Test(expected = AccessDeniedException.class)
    public void testDecideRechtNegativ() {
        Collection<ConfigAttribute> attributes =
            Arrays.asList((ConfigAttribute) new StringConfigAttribute("Recht_B"));
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_A" });
        this.accessDecisionManager.decide(null, null, attributes);
    }

    @SuppressWarnings("serial")
    private static class StringConfigAttribute implements ConfigAttribute {
        private String attribute;

        public StringConfigAttribute(String attribute) {
            this.attribute = attribute;
        }

        @Override
        public String getAttribute() {
            return this.attribute;
        }
    }

}
