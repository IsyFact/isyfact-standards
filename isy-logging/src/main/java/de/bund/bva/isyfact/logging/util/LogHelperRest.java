package de.bund.bva.isyfact.logging.util;

import java.util.Date;

import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.impl.EreignisschluesselRest;
import de.bund.bva.isyfact.logging.impl.IsyMarkerImpl;
import de.bund.bva.isyfact.logging.impl.MarkerSchluessel;

/**
 * Helperklasse zum Erstellen von Logeinträgen.
 * @see LogHelper
 */
public class LogHelperRest {

    // FIXME Duplikat zu KonstantenRest.java (auf Grund von Abhängigkeitskonflikt nicht importierbar).
    /** Zwischengespeicherte Log-Nachricht (bei Konfiguration Loggen Daten bei Exception). */
    public static final String KEY_STORED_LOG_MESSAGE = "isy-stored-log-message";

    // FIXME Duplikat zu KonstantenRest.java (auf Grund von Abhängigkeitskonflikt nicht importierbar).
    /** Zwischengespeicherter Logger (bei Konfiguration Loggen Daten bei Exception). */
    public static final String KEY_STORED_LOGGER = "isy-stored-logger";

    /** Logge die Profiling-Angaben zu Dauer und aufgerufenem bzw. aufgerufenes System. */
    private boolean loggeDauer;

    /** Logge die fachlichen Daten im Body der Nachricht. */
    private boolean loggeDaten;

    /** Logge die fachlichen Daten im Body der Nachricht, falls ein Fehler bei der Verarbeitung auftrat. */
    private boolean loggeDatenBeiServerFehler;

    public LogHelperRest() {
        this(false, false, false);
    }

    public LogHelperRest(boolean loggeDauer, boolean loggeDaten, boolean loggeDatenBeiServerFehler) {
        this.loggeDauer = loggeDauer;
        this.loggeDaten = loggeDaten;
        this.loggeDatenBeiServerFehler = loggeDatenBeiServerFehler;
    }

    public void logHeaderIn(IsyLogger log, String nachricht, Message message) {
        if (isRequestor(message)) {
            log.info(LogKategorie.METRIK, EreignisschluesselRest.EISYLO03007.name(),
                EreignisschluesselRest.EISYLO03007.getNachricht(), nachricht);
        } else {
            log.info(LogKategorie.METRIK, EreignisschluesselRest.EISYLO03005.name(),
                EreignisschluesselRest.EISYLO03005.getNachricht(), nachricht);
        }
    }

    public void logBodyIn(IsyLogger log, String nachricht, Message message) {
        if (!this.loggeDaten) {
            // Der eingehende Request wird zwischengespeichert. Er wird ggfs. beim Versenden der
            // ausgehenden Response geloggt.
            if (this.loggeDatenBeiServerFehler && !isRequestor(message)) {
                speichereLognachrichtInExchange(message, log, nachricht);
            }
            return;
        }

        logBodyInForce(log, nachricht, message);
    }

    public void logBodyInForce(IsyLogger log, String nachricht, Message message) {
        if (isRequestor(message)) {
            log.infoFachdaten(LogKategorie.METRIK, EreignisschluesselRest.EISYLO03003.name(),
                EreignisschluesselRest.EISYLO03003.getNachricht(), nachricht);
        } else {
            log.infoFachdaten(LogKategorie.METRIK, EreignisschluesselRest.EISYLO03001.name(),
                EreignisschluesselRest.EISYLO03001.getNachricht(), nachricht);
        }
    }

    public void logHeaderOut(IsyLogger log, String nachricht, Message message) {
        if (isRequestor(message)) {
            log.info(LogKategorie.METRIK, EreignisschluesselRest.EISYLO03006.name(),
                EreignisschluesselRest.EISYLO03006.getNachricht(), nachricht);
        } else {
            log.info(LogKategorie.METRIK, EreignisschluesselRest.EISYLO03008.name(),
                EreignisschluesselRest.EISYLO03008.getNachricht(), nachricht);
        }
    }

    public void logBodyOut(IsyLogger log, String nachricht, Message message) {
        if (!this.loggeDaten) {
            // Der ausgehende Request wird zwischengespeichert. Er wird ggfs. beim Erhalt der
            // eingehenden Response geloggt.
            if (this.loggeDatenBeiServerFehler && isRequestor(message)) {
                speichereLognachrichtInExchange(message, log, nachricht);
            }
            return;
        }

        logBodyOutForce(log, nachricht, message);
    }

    public void logBodyOutForce(IsyLogger log, String nachricht, Message message) {
        if (isRequestor(message)) {
            log.infoFachdaten(LogKategorie.METRIK, EreignisschluesselRest.EISYLO03002.name(),
                EreignisschluesselRest.EISYLO03002.getNachricht(), nachricht);
        } else {
            log.infoFachdaten(LogKategorie.METRIK, EreignisschluesselRest.EISYLO03004.name(),
                EreignisschluesselRest.EISYLO03004.getNachricht(), nachricht);
        }
    }

    public void logDauerAufruf(IsyLogger log, String aufgerufenesSystem, long dauer, Message message) {
        if (this.loggeDauer) {
            log.info(LogKategorie.PROFILING, EreignisschluesselRest.EISYLO03009.name(),
                EreignisschluesselRest.EISYLO03009.getNachricht(), aufgerufenesSystem,
                new IsyMarkerImpl(MarkerSchluessel.DAUER, "" + dauer));
        }
    }

    public void logDauerVerarbeitung(IsyLogger log, String aufrufendesSystem, long dauer, Message message) {
        if (this.loggeDauer) {
            log.info(LogKategorie.PROFILING, EreignisschluesselRest.EISYLO03010.name(),
                EreignisschluesselRest.EISYLO03010.getNachricht(), aufrufendesSystem,
                new IsyMarkerImpl(MarkerSchluessel.DAUER, "" + dauer));
        }
    }

    public void logBenutzer(IsyLogger log, String benutzername, String login, String interneKennung,
        String behoerde) {
        log.infoFachdaten(LogKategorie.SICHERHEIT, EreignisschluesselRest.EISYLO03011.name(),
            EreignisschluesselRest.EISYLO03011.getNachricht(), benutzername, login, interneKennung, behoerde);
    }

    private boolean isRequestor(Message message) {
        return MessageUtils.isRequestor(message);
    }

    /**
     * Die Lognachricht wird abhängig von der Verarbeitung (die noch nicht stattgefunden hat) geloggt.
     * Speichert die Lognachricht und den Logger in den Message-Exchange, damit die Nachricht später geloggt
     * werden kann.
     * @param message
     *            Nachricht mit Exchange zum Zwischenspeichern.
     * @param log
     *            Logger, der den Nachrichteninhalt ursprünglich loggen sollte.
     * @param nachricht
     *            Zu loggender Nachrichteninhalt.
     */
    private void speichereLognachrichtInExchange(Message message, IsyLogger log, String nachricht) {
        message.getExchange().put(KEY_STORED_LOG_MESSAGE, nachricht);
        message.getExchange().put(KEY_STORED_LOGGER, log);
    }

    /**
     * Hilfsmethode zum ermitteln des aktuellen Zeitstempels in Millisekunden. Dieser wird zur Berechnung der
     * Dauer eines Aufrufs verwendet.
     *
     * @return den aktuellen Zeitstempel in Millisekunden.
     * @see LogHelper#ermittleAktuellenZeitpunkt()
     */
    public long ermittleAktuellenZeitpunkt() {
        return new Date().getTime();
    }

    public void setLoggeDauer(boolean loggeDauer) {
        this.loggeDauer = loggeDauer;
    }

    public void setLoggeDaten(boolean loggeDaten) {
        this.loggeDaten = loggeDaten;
    }

    public void setLoggeDatenBeiServerFehler(boolean loggeDatenBeiServerFehler) {
        this.loggeDatenBeiServerFehler = loggeDatenBeiServerFehler;
    }
}
