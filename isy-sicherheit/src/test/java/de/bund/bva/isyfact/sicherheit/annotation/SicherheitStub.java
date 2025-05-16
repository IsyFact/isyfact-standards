package de.bund.bva.isyfact.sicherheit.annotation;

import java.util.HashSet;
import java.util.Set;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.sicherheit.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.Rolle;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;

public class SicherheitStub implements Sicherheit<AufrufKontext> {

    private AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    private AufrufKontext letzterAufrufKontext;

    public void setAufrufKontextVerwalter(AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    public AufrufKontext getLetzterAufrufKontext() {
        return this.letzterAufrufKontext;
    }

    public void reset() {
        this.letzterAufrufKontext = null;
    }

    @Override
    public Berechtigungsmanager getBerechtigungsManager() throws AuthentifizierungFehlgeschlagenException,
        AuthentifizierungTechnicalException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziere(
        AufrufKontext unauthentifizierterAufrufKontext) throws AuthentifizierungFehlgeschlagenException,
        AuthentifizierungTechnicalException {

        this.aufrufKontextVerwalter.setAufrufKontext(unauthentifizierterAufrufKontext);
        this.letzterAufrufKontext = unauthentifizierterAufrufKontext;
        return null;
    }

    @Override
    public Set<Rolle> getAlleRollen() {
        return new HashSet<>();
    }

    @Override
    public void leereCache() {
        throw new UnsupportedOperationException();
    }
}
