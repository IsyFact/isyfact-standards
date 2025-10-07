package de.bund.bva.isyfact.logging.hilfsklassen;



/**
 * Test-Bean throwing an errer in a getter, dass innerhalb eines Getters einen Fehler wirft.
 */
public class TestBeanMitException {
    
    /** Simple property creating an exception for "get". */
    private String a;

    /**
     * gets value of attribut 'a'.
     * 
     * @return value of attribut.
     */
    public String getA() {
        // throws Nullpointer
        return a.toString();
    }

    /**
     * set value of attribut 'a'.
     *
     * @param a new value for attribut.
     */
    public void setA(String a) {
        this.a = a;
    }
    

}
