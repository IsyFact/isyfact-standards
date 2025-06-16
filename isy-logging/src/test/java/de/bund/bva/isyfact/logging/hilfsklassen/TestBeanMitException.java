package de.bund.bva.isyfact.logging.hilfsklassen;

/**
 * Test-Bean, dass innerhalb eines Getters einen Fehler wirft.
 */
public class TestBeanMitException {
    
    /** Einfache Property bei "get" eine Exception erzeugt. */
    private String a;

    /**
     * Liefert den Wert des Attributs 'a'.
     * 
     * @return Wert des Attributs.
     */
    public String getA() {
        // Wirft Nullpointer
        return a.toString();
    }

    /**
     * Setzt den Wert des Attributs 'a'.
     *
     * @param a Neuer Wert des Attributs.
     */
    public void setA(String a) {
        this.a = a;
    }
    

}
