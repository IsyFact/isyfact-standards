package de.bund.bva.isyfact.logging.hilfsklassen;

import java.util.Arrays;
import java.util.List;

/**
 * Test-Bean für die Serialisierungstests mit einfachen Attributen.
 */
public class TestBeanEinfach {

    /** Referenz auf irgend ein Objekt in de/bund/bva. */
    private TestBeanKomplex inneresBean;

    /** Referenz auf einen String. */
    private String einString;

    /** Referenz auf eine Liste. */
    private List<TestBeanKomplex> eineListe;

    /**
     * Konstruktor der Klasse.
     */
    public TestBeanEinfach() {
        this.inneresBean = new TestBeanKomplex(false);
        this.einString = "einString";
        this.eineListe = Arrays.asList(new TestBeanKomplex(false), new TestBeanKomplex(false));
    }

    /**
     * Liefert den Wert des Attributs 'inneresBean'.
     * 
     * @return Wert des Attributs.
     */
    public TestBeanKomplex getInneresBean() {
        return inneresBean;
    }

    /**
     * Liefert den Wert des Attributs 'einString'.
     * 
     * @return Wert des Attributs.
     */
    public String getEinString() {
        return einString;
    }

    /**
     * Liefert den Wert des Attributs 'eineListe'.
     * 
     * @return Wert des Attributs.
     */
    public List<TestBeanKomplex> getEineListe() {
        return eineListe;
    }

    /**
     * Setzt den Wert des Attributs 'inneresBean'.
     *
     * @param inneresBean Neuer Wert des Attributs.
     */
    public void setInneresBean(TestBeanKomplex inneresBean) {
        this.inneresBean = inneresBean;
    }

    /**
     * Setzt den Wert des Attributs 'einString'.
     *
     * @param einString Neuer Wert des Attributs.
     */
    public void setEinString(String einString) {
        this.einString = einString;
    }

    /**
     * Setzt den Wert des Attributs 'eineListe'.
     *
     * @param eineListe Neuer Wert des Attributs.
     */
    public void setEineListe(List<TestBeanKomplex> eineListe) {
        this.eineListe = eineListe;
    }

}
