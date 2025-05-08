package de.bund.bva.isyfact.sicherheit.annotation.bean;

import de.bund.bva.isyfact.sicherheit.annotation.Gesichert;
import de.bund.bva.isyfact.sicherheit.annotation.NutzerAuthentifizierung;

/**
 * Ein Bean, welches abgesicherte Methoden ohne Funktionalität bereitstellt.
 * <p>
 * Es dient zum Testen, dass die korrekten Exceptions geworfen werden.
 *
 *
 */
public class ServiceImpl implements ServiceIntf {

    @Override
    @NutzerAuthentifizierung(benutzer = "testBenutzer")
    @Gesichert({})
    public void gesichertDurch_Nichts() {
        // noop
    }

    @Override
    @NutzerAuthentifizierung(benutzer = "testBenutzer")
    @Gesichert("Recht_A")
    public void gesichertDurch_RechtA() {
        // noop
    }

    @Override
    @NutzerAuthentifizierung(benutzer = "testBenutzer")
    @Gesichert("Recht_B")
    public void gesichertDurch_RechtB() {
        // noop
    }

    @Override
    @NutzerAuthentifizierung(benutzer = "testBenutzer")
    @Gesichert({ "Recht_A", "Recht_B" })
    public void gesichertDurch_RechtAundB() {
        // noop
    }

    @Override
    @NutzerAuthentifizierung(benutzer = "testBenutzer")
    @Gesichert("")
    public void gesichertDurch_leeresRecht() {
        // noop
    }

    @NutzerAuthentifizierung(benutzer = "testBenutzer")
    public void methodeGesichertDurchNutzerAnnotation(){
        // noop
    }

    @Override
    public void nichtGesichert(){
        // noop
    }

    @Override
    public void gesichertAmInterface() {
        // noop
    }
}
