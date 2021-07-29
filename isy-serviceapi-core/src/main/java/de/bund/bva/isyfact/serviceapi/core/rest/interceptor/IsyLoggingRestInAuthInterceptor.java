package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Required;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.LogHelperRest;
import de.bund.bva.isyfact.serviceapi.common.konstanten.EreignisschluesselRest;

/**
 * Isy-Interceptor zum Loggen der Identifikationsdaten bei einem REST-Request.
 */
public final class IsyLoggingRestInAuthInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IsyLoggingRestInAuthInterceptor.class);

    private LogHelperRest logHelperRest = new LogHelperRest(true, false, false);

    private AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    public IsyLoggingRestInAuthInterceptor() {
        super(Phase.RECEIVE);
    }

    public IsyLoggingRestInAuthInterceptor(String phase) {
        super(phase);
    }

    public IsyLoggingRestInAuthInterceptor(String id, String phase) {
        super(id, phase);
    }

    /**
     * Loggt die Authentifizierungsdaten und Behoerdenkennzeichen bei eingehenden Requests.
     */
    @Override
    public void handleMessage(Message message) {
        if (isRequestor(message) || !LOG.isInfoEnabled()) {
            // Eingehende Responses werden ignoriert.
            return;
        }

        loggeAuthentifizierungsdaten();
    }

    private void loggeAuthentifizierungsdaten() {
        AufrufKontext aufrufKontext = this.aufrufKontextVerwalter.getAufrufKontext();
        if (aufrufKontext == null) {
            LOG.warn(EreignisschluesselRest.LOGGING_REST_FEHLER, "Es ist kein Aufrufkontext vorhanden.");
            return;
        }
        String benutzername = aufrufKontext.getDurchfuehrenderSachbearbeiterName();
        String login = aufrufKontext.getDurchfuehrenderBenutzerKennung();
        String interneKennung = aufrufKontext.getDurchfuehrenderBenutzerInterneKennung();
        String behoerde = aufrufKontext.getDurchfuehrendeBehoerde();
        this.logHelperRest.logBenutzer(LOG, benutzername, login, interneKennung, behoerde);
    }

    public void setLogHelperRest(LogHelperRest logHelperRest) {
        this.logHelperRest = logHelperRest;
    }

    @Required
    public void setAufrufKontextVerwalter(AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }
}
