package de.bund.bva.isyfact.aufrufkontext.helper;

import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextVerwalterImpl;

/**
 * Speichert den AufrufKontext zum späteren Abruf für die Testfälle.
 * 
 * 
 */
public class DebugAufrufKontextVerwalter extends AufrufKontextVerwalterImpl<AufrufKontextImpl> {

    /** der letzte gesetzte AufrufKontext, der nicht {@code null} war. */
    private AufrufKontextImpl letzterAufrufKontext;

    @Override
    public void setAufrufKontext(AufrufKontextImpl aufrufKontext) {
        if (aufrufKontext != null) {
            letzterAufrufKontext = aufrufKontext;
        }
        super.setAufrufKontext(aufrufKontext);
    }

    /**
     * Liefert das Feld {@link #letzterAufrufKontext} zurück.
     * @return Wert von letzterAufrufKontext
     */
    public AufrufKontextImpl getLetzterAufrufKontext() {
        return letzterAufrufKontext;
    }
    
    /**
     * Setzt den AufrufKontext und den letztenAufrufKontext auf null.
     */
    public void resetAufrufKontext() {
        letzterAufrufKontext = null;
        super.setAufrufKontext(null);
    }

}
