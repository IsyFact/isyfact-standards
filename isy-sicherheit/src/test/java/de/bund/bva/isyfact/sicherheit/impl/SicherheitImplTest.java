package de.bund.bva.isyfact.sicherheit.impl;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.isyfact.sicherheit.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;
import de.bund.bva.isyfact.sicherheit.common.exception.InitialisierungsException;
import de.bund.bva.isyfact.sicherheit.config.IsySicherheitConfigurationProperties;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
public class SicherheitImplTest extends AbstractSicherheitTest {

    @Test
    public void testeGetBerechtigungsManagerRollenErmittelt() throws Exception {
        AufrufKontext aufrufKontext = aufrufKontextFactory.erzeugeAufrufKontext();

        aufrufKontext.setDurchfuehrenderBenutzerKennung("nutzer");
        aufrufKontext.setDurchfuehrenderSachbearbeiterName("name");
        aufrufKontext.setDurchfuehrendeBehoerde("behoerde");
        aufrufKontext.setRolle(new String[] { "Rolle_A", "Rolle_B" });
        aufrufKontext.setRollenErmittelt(true);
        aufrufKontextVerwalter.setAufrufKontext(aufrufKontext);
        Berechtigungsmanager berechtigungsManager = sicherheit.getBerechtigungsManager();
        Assert.assertEquals(0, testAccessManager.getCountAuthentifziere());
        assertEquals("Nicht alle Rechte wurden zugewiesen", 2, berechtigungsManager.getRechte().size());
    }

    @Test
    public void testeAuthentifziereMitKontext() throws Exception {
        AufrufKontext aufrufKontext = aufrufKontextFactory.erzeugeAufrufKontext();
        aufrufKontext.setDurchfuehrenderBenutzerKennung("nutzer");
        aufrufKontext.setDurchfuehrenderSachbearbeiterName("name");
        aufrufKontext.setDurchfuehrendeBehoerde("behoerde");
        aufrufKontext.setDurchfuehrenderBenutzerPasswort("passwort");
        aufrufKontext.setRollenErmittelt(false);
        aufrufKontext.setRolle(new String[] { "Rolle_XYZ" });
        sicherheit.getBerechtigungsManagerUndAuthentifiziere(aufrufKontext);

        Assert.assertEquals(1, testAccessManager.getCountAuthentifziere());
        Assert.assertEquals("nutzer", testAccessManager.getParamAuthentifiziereAufrufKontext()
            .getDurchfuehrenderBenutzerKennung());
        Assert.assertEquals("passwort", testAccessManager.getParamAuthentifiziereAufrufKontext()
            .getDurchfuehrenderBenutzerPasswort());
        assertTrue(sicherheit.getBerechtigungsManager().hatRecht("Recht_A"));
        assertTrue(sicherheit.getBerechtigungsManager().hatRecht("Recht_B"));
        assertEquals(2, sicherheit.getBerechtigungsManager().getRollen().size());
    }

    @Test(expected = AuthentifizierungFehlgeschlagenException.class)
    public void testeAuthentifiziereMitKontextException() {
        testAccessManager.setAuthentifiziereException(new AuthentifizierungFehlgeschlagenException(
            "Testfehler"));
        sicherheit.getBerechtigungsManagerUndAuthentifiziere(aufrufKontextFactory
            .erzeugeAufrufKontext());
    }

    @Test(expected = AuthentifizierungTechnicalException.class)
    public void testeAuthentifiziereMitKontextException2() {
        testAccessManager.setAuthentifiziereException(new AuthentifizierungTechnicalException(
            "Testfehler"));
        sicherheit.getBerechtigungsManagerUndAuthentifiziere(aufrufKontextFactory
            .erzeugeAufrufKontext());
    }

    @Test
    public void testeGetBerechtigungsManagerRollenNichtErmittelt() throws Exception {
        AufrufKontext aufrufKontext = aufrufKontextFactory.erzeugeAufrufKontext();
        aufrufKontext.setDurchfuehrenderBenutzerKennung("nutzer");
        aufrufKontext.setDurchfuehrenderSachbearbeiterName("name");
        aufrufKontext.setDurchfuehrendeBehoerde("behoerde");
        aufrufKontext.setDurchfuehrenderBenutzerPasswort("passwort");
        aufrufKontext.setRollenErmittelt(false);
        aufrufKontext.setRolle(new String[] { "Rolle_XYZ" });
        aufrufKontextVerwalter.setAufrufKontext(aufrufKontext);
        sicherheit.getBerechtigungsManager();

        Assert.assertEquals(1, testAccessManager.getCountAuthentifziere());
        Assert.assertEquals("nutzer", testAccessManager.getParamAuthentifiziereAufrufKontext()
            .getDurchfuehrenderBenutzerKennung());
        Assert.assertEquals("passwort", testAccessManager.getParamAuthentifiziereAufrufKontext()
            .getDurchfuehrenderBenutzerPasswort());
        assertTrue(sicherheit.getBerechtigungsManager().hatRecht("Recht_A"));
        assertTrue(sicherheit.getBerechtigungsManager().hatRecht("Recht_B"));
        assertEquals(2, sicherheit.getBerechtigungsManager().getRollen().size());
    }

    @Test(expected = InitialisierungsException.class)
    public void testeRollenrechteNichtGesetzt() throws Exception {
        AufrufKontextVerwalterImpl aufrufKontextVerwalterImpl = new AufrufKontextVerwalterImpl();
        AufrufKontextImpl aufrufKontext = new AufrufKontextImpl();
        aufrufKontext.setDurchfuehrenderBenutzerKennung("nutzer");
        aufrufKontext.setDurchfuehrenderSachbearbeiterName("name");
        aufrufKontext.setDurchfuehrendeBehoerde("behoerde");
        aufrufKontext.setRolle(new String[] { "Rolle_A", "Rolle_B" });
        aufrufKontext.setRollenErmittelt(true);
        aufrufKontextVerwalterImpl.setAufrufKontext(aufrufKontext);
        SicherheitImpl sicherheit = new SicherheitImpl(null, aufrufKontextVerwalterImpl, null, null, isySicherheitConfigurationProperties);
        sicherheit.afterPropertiesSet();
    }

    @Test(expected = InitialisierungsException.class)
    public void testeFehlerAufrufKontextVerwalterNichtGesetzt() throws Exception {
        SicherheitImpl sicherheit = new SicherheitImpl("/resources/sicherheit/rollenrechte.xml", null, aufrufKontextFactory, testAccessManager, isySicherheitConfigurationProperties);
        sicherheit.afterPropertiesSet();
    }

    @Test(expected = AuthentifizierungTechnicalException.class)
    public void testeFehlerAufrufKontextNichtGesetzt() throws Exception {
        sicherheit.getBerechtigungsManager();
    }

    @Test(expected = AuthentifizierungTechnicalException.class)
    public void testGetBerechtigungsmanagerBeiAufrufKontextVerwalterGleichNull(){
        SicherheitImpl sich = new SicherheitImpl("/resources/sicherheit/rollenrechte.xml", null, aufrufKontextFactory, testAccessManager, isySicherheitConfigurationProperties);
        sich.getBerechtigungsManager();
    }

    @Test(expected = AuthentifizierungTechnicalException.class)
    public void testGetBerechtigungsmanagerUndAuthentifiziereBeiAufrufKontextGleichNull(){
        SicherheitImpl sicherheit = new SicherheitImpl("/resources/sicherheit/rollenrechte.xml", aufrufKontextVerwalter, aufrufKontextFactory, testAccessManager, isySicherheitConfigurationProperties);
        sicherheit.getBerechtigungsManagerUndAuthentifiziere(null);
    }


    @Test(expected = InitialisierungsException.class)
    public void testAfterPropertiesSetKeinAufrufKontextVerwalter() throws Exception{
        SicherheitImpl sicherheit = new SicherheitImpl("/resources/sicherheit/rollenrechte.xml", null, null, null, new IsySicherheitConfigurationProperties());
        sicherheit.afterPropertiesSet();
    }
}
