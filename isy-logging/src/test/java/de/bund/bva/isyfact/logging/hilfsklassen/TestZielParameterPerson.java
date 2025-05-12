package de.bund.bva.isyfact.logging.hilfsklassen;

/**
 * Hilfsklasse als Testparameter einer Methodinvocation.
 * 
 */
public class TestZielParameterPerson {

    /** Die Vornamen des Paramaters. */
    private String[] vornamen;

    /** Der Nachname des Parameters. */
    private String name;

    /**
     * Konstruktor der Klasse. Initialisiert die übergebenen Parameter.
     * 
     * @param vornamen die Vornamen.
     * @param name der Name.
     */
    public TestZielParameterPerson(String name, String... vornamen) {
        super();
        this.vornamen = vornamen;
        this.name = name;
    }

    /**
     * Liefert den Wert des Attributs 'vornamen'.
     * 
     * @return Wert des Attributs.
     */
    public String[] getVornamen() {
        return vornamen;
    }

    /**
     * Setzt den Wert des Attributs 'vornamen'.
     * 
     * @param vornamen
     *            Neuer Wert des Attributs.
     */
    public void setVornamen(String[] vornamen) {
        this.vornamen = vornamen;
    }

    /**
     * Liefert den Wert des Attributs 'name'.
     * 
     * @return Wert des Attributs.
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Wert des Attributs 'name'.
     * 
     * @param name
     *            Neuer Wert des Attributs.
     */
    public void setName(String name) {
        this.name = name;
    }

}
