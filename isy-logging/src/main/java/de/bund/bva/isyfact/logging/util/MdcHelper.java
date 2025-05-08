package de.bund.bva.isyfact.logging.util;



import org.slf4j.MDC;

import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.IsyLoggerStandard;

/**
 * Hilfsklasse zum Zugriff auf den MDC.
 * 
 */
public final class MdcHelper {

    /** Konstante des Schlüssels der Korrelations-ID im MDC. */
    public static final String MDC_KORRELATIONS_ID = "korrelationsId";

    /** Konstante des Schlüssels der Korrelations-ID im MDC. */
    public static final String MDC_FACHDATEN = "fachdatenmdc";

    /** Trenner der Korrelations-IDs. */
    private static final String TRENNER = ";";

    /** Logger der Klasse. */
    private static final IsyLoggerStandard LOG = IsyLoggerFactory.getLogger(MdcHelper.class);

    /**
     * Privater Konstruktor der Klasse. Verhindert, dass Instanzen der Klasse angelegt werden.
     * 
     */
    private MdcHelper() {
    }

    /**
     * Hilfsmethode zum Entfernen der Korrelations-Id aus dem MDC. Sollte sich die Korrelations-ID aus
     * mehreren, durch Semikolon getrennte IDs zusammensetzen, wird nur die zuletzt hinzugefügt
     * Korrelations-Id entfernt.
     * 
     * @return die entfernte Korrelations-ID.
     */
    public static String entferneKorrelationsId() {
        String korrelationsId = MDC.get(MDC_KORRELATIONS_ID);
        if (korrelationsId == null) {
            return null;
        } else if (!korrelationsId.contains(TRENNER)) {
            MDC.remove(MDC_KORRELATIONS_ID);
            return korrelationsId;
        } else {
            int letztesSemikolon = korrelationsId.lastIndexOf(TRENNER);
            String korrelationsIdEntfernt = korrelationsId.substring(letztesSemikolon + 1);
            korrelationsId = korrelationsId.substring(0, letztesSemikolon);
            MDC.put(MDC_KORRELATIONS_ID, korrelationsId);
            return korrelationsIdEntfernt;
        }
    }

    /**
     * Hilfsmethode zum Entfernen aller Korrelations-Id aus dem MDC.
     */
    public static void entferneKorrelationsIds() {
        MDC.remove(MDC_KORRELATIONS_ID);
    }

    /**
     * Hilfsmethode zum Lesen der kompletten Korrelations-Id aus dem MDC.
     * 
     * @return die gelesen Korrelations-ID.
     */
    public static String liesKorrelationsId() {
        String korrelationsId = MDC.get(MDC_KORRELATIONS_ID);
        if (korrelationsId == null) {
            LOG.debug("Keine Korrelations-ID unter dem Schlüssel {} im MDC gesetzt.", MDC_KORRELATIONS_ID);
        }
        return korrelationsId;
    }

    /**
     * Hilfsmethode zum Hinzufügen der Korrelations-Id im Kontext. Sollte bereits eine Korrelations-ID
     * vorhanden sein, wird die übergebene Korrelations-ID, getrennt durch ein Semikolon, ergänzt.
     * 
     * @param korrelationsId
     *            die Korrelations-ID.
     * @return die komplette KorrelationsId.
     */
    public static String pushKorrelationsId(String korrelationsId) {
        String vorhandeneKorrelationsId = MDC.get(MDC_KORRELATIONS_ID);
        String neueKorrelationsId;
        if (vorhandeneKorrelationsId == null) {
            neueKorrelationsId = korrelationsId;
        } else {
            neueKorrelationsId = vorhandeneKorrelationsId + TRENNER + korrelationsId;
        }
        MDC.put(MDC_KORRELATIONS_ID, neueKorrelationsId);
        return neueKorrelationsId;
    }

    /**
     * Hilfsmethode zum Lesen des Attributs "fachdaten" aus dem MDC. Dieses gibt an, ob der MDC fachliche
     * Daten enthält, die geschützt werden müssen.
     * 
     * @return Wert des Attributs "fachdaten".
     */
    public static boolean liesMarkerFachdaten() {
        String fachdatenMdc = MDC.get(MDC_FACHDATEN);
        return (LoggingKonstanten.TRUE.equals(fachdatenMdc));
    }

    /**
     * Hilfsmethode zum Setzen des Markers "fachdaten" im MDC. Dieser gibt an, ob der MDC fachliche Daten
     * enthält, die geschützt werden müssen.
     * 
     * @param enthaeltFachdaten
     *            zu setzender Wert des Attributs.
     */
    public static void setzeMarkerFachdaten(boolean enthaeltFachdaten) {
        if (enthaeltFachdaten) {
            MDC.put(MDC_FACHDATEN, LoggingKonstanten.TRUE);
        } else {
            MDC.put(MDC_FACHDATEN, LoggingKonstanten.FALSE);
        }
    }
    
    /**
     * Hilfsmethode zum Entfernen des Markers "fachdaten" im MDC.
     */
    public static void entferneMarkerFachdaten() {
        MDC.remove(MDC_FACHDATEN);
    }
    
}
