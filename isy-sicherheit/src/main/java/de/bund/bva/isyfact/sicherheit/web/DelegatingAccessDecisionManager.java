package de.bund.bva.isyfact.sicherheit.web;

import java.util.Collection;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.sicherheit.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

/**
 * Ein AccessDecisionManager zur Verwendung mit spring-security.
 * 
 * Der AccessDecisionManager delegiert die Entscheidung über die Berechtigung an den in der Komponente
 * {@link Sicherheit} hinterlegten {@link Berechtigungsmanager}.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class DelegatingAccessDecisionManager implements AccessDecisionManager {

    /** Zugriff auf die Komponente Sicherheit. */
    private final Sicherheit<? extends AufrufKontext> sicherheit;

    public DelegatingAccessDecisionManager(Sicherheit<? extends AufrufKontext> sicherheit) {
        this.sicherheit = sicherheit;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void decide(Authentication authentication, Object object,
        Collection<ConfigAttribute> configAttributes) throws AccessDeniedException,
        InsufficientAuthenticationException {
        Berechtigungsmanager bm;
        try {
            bm = sicherheit.getBerechtigungsManager();
        } catch (AuthentifizierungTechnicalException e) {
            throw new InsufficientAuthenticationException(
                SicherheitFehlerSchluessel.MSG_BERECHTIGUNGSMANAGER_NICHT_VERFUEGBAR, e);
        } catch (AuthentifizierungFehlgeschlagenException e) {
            throw new InsufficientAuthenticationException(
                SicherheitFehlerSchluessel.MSG_BERECHTIGUNGSMANAGER_NICHT_VERFUEGBAR, e);
        }

        for (Object obj : configAttributes) {
            ConfigAttribute attribute = (ConfigAttribute) obj;

            String gefordertesRecht = attribute.getAttribute();
            if (!bm.hatRecht(gefordertesRecht)) {
                throw new AccessDeniedException("Keine Berechtigung für: " + gefordertesRecht + " ("
                    + SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_FEHLGESCHLAGEN + ")");
            }
        }
    }
}