package de.bund.bva.isyfact.logging.hilfsklassen;

import de.bund.bva.isyfact.logging.annotation.Systemgrenze;

/**
 * Test class as target for a method call to be logged.
 * 
 */
@Systemgrenze
public class TestZielKlasse {

    /**
     * Test method (success).
     * The method sets the name for a given person and pauses the thread for 10 ms.
     * 
     * @param person
     *            the person whose name to be changed.
     * @param name
     *            name of a person.
     * @return the changed person
     * @throws Throwable
     *             if an error occurs during pausing a threads.
     */
    public TestZielParameterPerson setzeName(TestZielParameterPerson person, String name) throws Throwable {
        person.setName(name);
        Thread.sleep(10);
        return person;
    }

    /**
     * Test method for logging an exception. A java.lang.ArithmeticException will be thrown.
     * 
     * @param person
     *            the person whose name to be changed.
     * @param str
     *            name of a person.
     * @return the changed person
     * @throws Throwable
     *             java.lang.ArithmeticException: always thrown.
     */
    public TestZielParameterPerson setzeNameException(TestZielParameterPerson person, String str)
            throws Throwable {

        person.setName(str);

        Thread.sleep(125);
        
        // provoke exception
        int i = 1 / 0;
        person.setName("Name " + i);

        return person;
    }

}
