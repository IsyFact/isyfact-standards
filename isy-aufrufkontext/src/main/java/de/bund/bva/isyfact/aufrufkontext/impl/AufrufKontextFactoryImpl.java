package de.bund.bva.isyfact.aufrufkontext.impl;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.common.exception.AufrufKontextKeinDefaultKonstruktorException;

/**
 * Erzeugt einen {@link AufrufKontext}.
 * <p>
 * Wird nichts weriter konfiguriert, wird ein {@link AufrufKontextImpl} verwendet. Wird aber durch das
 * Property aufrufKontextKlasse eine Klasse definiert, wird diese verwendet.
 * 
 * @param <T>
 *            die anwendungsspezifische AufrufKontext-Klasse
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public class AufrufKontextFactoryImpl<T extends AufrufKontext> implements AufrufKontextFactory<T> {

    /** Die Ausprägung des zu erstellenden AufrufKontextes. */
    private Class<?> aufrufKontextKlasse = AufrufKontextImpl.class;

    /**
     * {@inheritDoc}
     * @return ein {@link AufrufKontextImpl} Objekt.
     */
    @SuppressWarnings("unchecked")
    public T erzeugeAufrufKontext() {
        T result;
        try {
            result = (T) aufrufKontextKlasse.newInstance();
        } catch (Exception e) {
            throw new AufrufKontextKeinDefaultKonstruktorException();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void nachAufrufKontextVerarbeitung(T aufrufKontext) {
        // es findet keine Nachbearbeitung statt.
    }

    /**
     * Setzt das Feld {@link #aufrufKontextKlasse}.
     * @param aufrufKontextKlasse
     *            Neuer Wert für aufrufKontextKlasse
     */
    public void setAufrufKontextKlasse(Class<?> aufrufKontextKlasse) {
        this.aufrufKontextKlasse = aufrufKontextKlasse;
    }

}
