package de.bund.bva.isyfact.logging.hilfsklassen;

/*
 * #%L
 * isy-logging
 * %%
 * 
 * %%
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * #L%
 */

import de.bund.bva.isyfact.logging.annotation.Systemgrenze;

/**
 * Testklasse, als Ziel eines zu loggenden Aufrufs.
 * 
 */
@Systemgrenze
public class TestZielKlasse {

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
