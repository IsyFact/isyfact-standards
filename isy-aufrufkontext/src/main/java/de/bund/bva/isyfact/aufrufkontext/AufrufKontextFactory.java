package de.bund.bva.isyfact.aufrufkontext;

/**
 * Erzeugt einen neuen AufrufKontext und stellt einen Hook bereit, um diesen nach der automatischen
 * Initialisierung nachzubearbeiten.
 * 
 * @param <T>
 *            die anwendungsspezifische AufrufKontext-Klasse
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public interface AufrufKontextFactory<T extends AufrufKontext> {

    /**
     * Erzeugt einen AufrufKontext.
     * @return der AufrufKontext.
     */
    T erzeugeAufrufKontext();

    /**
     * Hook-Methode, um zusätzliche Informationen in den AufrufKontext zu schreiben.
     * 
     * @param aufrufKontext
     *            der AufrufKontext
     */
    void nachAufrufKontextVerarbeitung(T aufrufKontext);
}
