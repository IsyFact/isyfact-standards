package de.bund.bva.isyfact.logging.hilfsklassen;



/**
 * Test-Bean throwing an error in a getter.
 */
public class TestBeanMitException {
    
    /** Simple property creating an exception for "get". */
    private String a;

    /**
     * Gets value of attribut 'a'.
     * 
     * @return value of attribut.
     */
    public String getA() {
        // throws Nullpointer
        return a.toString();
    }

    /**
     * Sets value of attribut 'a'.
     *
     * @param a new value for attribut.
     */
    public void setA(String a) {
        this.a = a;
    }
    

}
