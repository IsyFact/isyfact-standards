package de.bund.bva.isyfact.logging.hilfsklassen;

import de.bund.bva.isyfact.logging.annotation.Komponentengrenze;

/**
 * Testklasse, als Ziel eines zu loggenden Aufrufs.
 * 
 */
@Komponentengrenze
public class TestZielKlasse2 {

    /**
     * Testmethode (erfolg). Die Methode setzt den Namen der übergenen Person und pausiert den Thread für 10
     * ms.
     * 
     * @param person
     *            die Person deren Namen angepasst werden soll.
     * @param name
     *            der Name der Person.
     * @return die geänderte Person
     * @throws Throwable
     *             wenn ein Fehler beim Pausierend es Threads auftreten sollte.
     */
    public TestZielParameterPerson setzeName(TestZielParameterPerson person, String name) throws Throwable {
        person.setName(name);
        Thread.sleep(10);
        return person;
    }

    /**
     * Testmethode zum Test des Loggings einer Exception. Es wird eine java.lang.ArithmeticException geworfen.
     * 
     * @param person
     *            die Person deren Namen angepasst werden soll.
     * @param str
     *            der Name der Person.
     * @return die geänderte Person
     * @throws Throwable
     *             java.lang.ArithmeticException: wird immer geworfen.
     */
    public TestZielParameterPerson setzeNameException(TestZielParameterPerson person, String str)
            throws Throwable {

        person.setName(str);

        Thread.sleep(125);
        
        // Exception provozieren
        int i = 1 / 0;
        person.setName("Name " + i);

        return person;
        
    }

}
