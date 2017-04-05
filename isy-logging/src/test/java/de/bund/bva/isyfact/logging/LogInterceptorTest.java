package de.bund.bva.isyfact.logging;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import de.bund.bva.isyfact.logging.hilfsklassen.DefaultMethodInvocation;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielKlasse;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielKlasse2;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielParameterPerson;
import de.bund.bva.isyfact.logging.util.BeanToMapConverter;
import de.bund.bva.isyfact.logging.util.LoggingMethodInterceptor;

/**
 * Testfälle des LogInterceptors.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/interceptor-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class LogInterceptorTest extends AbstractLogTest {

    /**
     * MethodInterceptor bei dem alle Flags auf "true" gesetzt sind (max. Ausgabe).
     */
    @Autowired
    @Qualifier("alleFlagsTrueInterceptor")
    private LoggingMethodInterceptor alleFlagsTrueInterceptor;

    /**
     * MethodInterceptor bei dem alle Flags auf "true" gesetzt sind. Das Flag 'loggeDaten' ist nicht gesetzt
     * und damit 'false'.
     */
    @Autowired
    @Qualifier("alleFlagsTrueOhneLoggeDatenInterceptor")
    private LoggingMethodInterceptor alleFlagsTrueOhneLoggeDatenInterceptor;

    /**
     * MethodInterceptor bei dem alle Flags auf "false" gesetzt sind (min. Ausgabe).
     */
    @Autowired
    @Qualifier("alleFlagsFalseInterceptor")
    private LoggingMethodInterceptor alleFlagsFalseInterceptor;

    /**
     * MethodInterceptor bei dem alle Flags auf "true" gesetzt sind, der Converter aber so konfiguriert wurde,
     * dass das Package 'de.bund.bva' auf Exlude steht.
     */
    @Autowired
    @Qualifier("individuellerInterceptor")
    private LoggingMethodInterceptor individuellerInterceptor;

    /**
     * Zielklasse bei der ein Log-Interceptor per Aspekt so konfiguriert ist, dass er Logeinträge für
     * Systemgrenzen erzeugt.
     */
    @Autowired
    private TestZielKlasse boundaryZielKlasse;

    /**
     * Zielklasse bei der ein Log-Interceptor per Aspekt so konfiguriert ist, dass er Logeinträge für
     * Komponentengrenzen erzeugt.
     */
    @Autowired
    private TestZielKlasse2 componentZielKlasse;

    /**
     * Test des Loggings eines erfolgreichen Methodenaufrufs mit maximaler Logausgabe. Interceptor wird direkt
     * und nicht über Spring aufgerufen.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testAufrufErfolgreichDirekt() throws Throwable {

        Method methode = TestZielKlasse.class.getMethod("setzeName", TestZielParameterPerson.class,
                String.class);

        MethodInvocation invocation = new DefaultMethodInvocation(new TestZielKlasse(), methode,
                new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"), "TestParameter 2");

        alleFlagsTrueOhneLoggeDatenInterceptor.invoke(invocation);

        pruefeLogdatei("testAufrufErfolgreichInterceptor");

    }

    /**
     * Test des Loggings eines nicht erfolgreichen Methodenaufrufs (Exception) mit maximaler Logausgabe.
     * Interceptor wird direkt und nicht über Spring aufgerufen.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testAufrufMitExceptionDirekt() throws Throwable {

        Method methode = TestZielKlasse.class.getMethod("setzeNameException", TestZielParameterPerson.class,
                String.class);

        MethodInvocation invocation = new DefaultMethodInvocation(new TestZielKlasse(), methode,
                new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"), "TestParameter 2");

        try {
            alleFlagsTrueOhneLoggeDatenInterceptor.invoke(invocation);
            Assert.fail("Es wurde eine Exception erwartet - der Aufruf war aber erfolgreich.");
        } catch (InvocationTargetException e) {
            // Diese Exception wird erwartet.
            e.printStackTrace();
        }

        pruefeLogdatei("testAufrufMitExceptionInterceptorDirekt", true);

    }

    /**
     * Test des Loggings eines erfolgreichen Methodenaufrufs mit minimaler Logausgabe. Interceptor wird direkt
     * und nicht über Spring aufgerufen.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testAufrufErfolgreichParameterFalseDirekt() throws Throwable {

        Method methode = TestZielKlasse.class.getMethod("setzeName", TestZielParameterPerson.class,
                String.class);

        MethodInvocation invocation = new DefaultMethodInvocation(new TestZielKlasse(), methode,
                new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"), "TestParameter 2");

        alleFlagsFalseInterceptor.invoke(invocation);

        pruefeLogdatei("testAufrufErfolgreichParameterFalse");

    }

    /**
     * Test des Loggings eines nicht erfolgreichen Methodenaufrufs (Exception) mit minimaler Logausgabe.
     * Interceptor wird direkt und nicht über Spring aufgerufen.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testAufrufMitExceptionParameterFalseDirekt() throws Throwable {

        Method methode = TestZielKlasse.class.getMethod("setzeNameException", TestZielParameterPerson.class,
                String.class);

        MethodInvocation invocation = new DefaultMethodInvocation(new TestZielKlasse(), methode,
                new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"), "TestParameter 2");

        try {
            alleFlagsFalseInterceptor.invoke(invocation);
            Assert.fail("Es wurde eine Exception erwartet - der Aufruf war aber erfolgreich.");
        } catch (InvocationTargetException e) {
            // Diese Exception wird erwartet.
            pruefeLogdatei("testAufrufMitExceptionParameterFalse");
        }

    }

    /**
     * Test des Loggings eines erfolgreichen Systemschnittstellenaufrufs. Es wird dabei ein per
     * Spring-Konfigurierter Interceptor getestet.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testBoundaryAufrufErfolgreich() throws Throwable {

        boundaryZielKlasse.setzeName(new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"),
                "TestParameter 2");
        // Analog zu direktem Aufruf (alle Parameter sind "true")
        pruefeLogdatei("testAufrufErfolgreichInterceptor");

    }

    /**
     * Test des Loggings eines nicht erfolgreichen Systemschnittstellenaufrufs (Exception). Es wird dabei ein
     * per Spring-Konfigurierter Interceptor getestet.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testBoundaryAufrufMitException() throws Throwable {

        try {
            boundaryZielKlasse.setzeNameException(new TestZielParameterPerson("Mustermann", "Max", "Peter",
                    "Hans"), "TestParameter 2");
        } catch (Exception e) {
            // Diese Exception wird erwartet.
            // Analog zu direktem Aufruf (alle Parameter sind "true")
            pruefeLogdatei("testAufrufMitExceptionInterceptor", true);
        }

    }

    /**
     * Test des Loggings eines erfolgreichen Komponentenschnittstellenaufrufs. Es wird dabei ein per
     * Spring-Konfigurierter Interceptor getestet.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testComponentAufrufErfolgreich() throws Throwable {

        componentZielKlasse.setzeName(new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"),
                "TestParameter 2");
        pruefeLogdatei("testAufrufErfolgreichComponentInterceptor");

    }

    /**
     * Test des Loggings eines nicht erfolgreichen Komponentenschnittstellenaufrufs (Exception). Es wird dabei
     * ein per Spring-Konfigurierter Interceptor getestet.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testComponentAufrufMitException() throws Throwable {

        try {
            componentZielKlasse.setzeNameException(new TestZielParameterPerson("Mustermann", "Max", "Peter",
                    "Hans"), "TestParameter 2");
        } catch (Exception e) {
            // Diese Exception wird erwartet.
            pruefeLogdatei("testAufrufMitExceptionComponentInterceptor");
        }

    }

    /**
     * Testet die Initialisierung von Includes und Excludes des BeanMappers, wenn nicht der Standardmapper
     * verwendet werden soll.
     * 
     * @throws Exception
     *             falls bei der Testdurchführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testInitialisierungIncludesExcludes() throws Exception {
        List<String> includes = Arrays.asList("de.bund.bva.xyz");
        List<String> excludes = Arrays.asList("java.util.xyz");
        LoggingMethodInterceptor loggingMethodInterceptor = new LoggingMethodInterceptor(includes, excludes);
        loggingMethodInterceptor.afterPropertiesSet();
        BeanToMapConverter converter = (BeanToMapConverter) loggingMethodInterceptor.getLogHelper()
                .getKonverter();
        Assert.assertEquals(includes, converter.getIncludes());
        Assert.assertEquals(excludes, converter.getExcludes());
    }

    /**
     * Test des Loggings eines nicht erfolgreichen Methodenaufrufs (Exception) mit maximaler Logausgabe.
     * Interceptor wird direkt und nicht über Spring aufgerufen. Der Interceptor ist dabei so konfiguriert,
     * dass der BeanConverter alle Beans unter de.bund.bva bei Serialisierung nicht berücksichtigt.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testAufrufMitExceptionDirektIndividuell() throws Throwable {

        Method methode = TestZielKlasse.class.getMethod("setzeNameException", TestZielParameterPerson.class,
                String.class);

        MethodInvocation invocation = new DefaultMethodInvocation(new TestZielKlasse(), methode,
                new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"), "TestParameter 2");

        try {
            individuellerInterceptor.invoke(invocation);
            Assert.fail("Es wurde eine Exception erwartet - der Aufruf war aber erfolgreich.");
        } catch (InvocationTargetException e) {
            // Diese Exception wird erwartet.
            e.printStackTrace();
        }

        pruefeLogdatei("testAufrufMitExceptionInterceptorIndividuell", true);

    }
    
    /**
     * Test des Loggings eines erfolgreichen Methodenaufrufs mit maximaler Logausgabe. Interceptor wird direkt
     * und nicht über Spring aufgerufen.
     * 
     * @throws Throwable
     *             wenn bei der Testausführung ein Fehler aufgetreten ist.
     */
    @Test
    public void testAufrufErfolgreichLoggeDatenDirekt() throws Throwable {

        Method methode = TestZielKlasse.class.getMethod("setzeName", TestZielParameterPerson.class,
                String.class);

        MethodInvocation invocation = new DefaultMethodInvocation(new TestZielKlasse(), methode,
                new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"), "TestParameter 2");

        // Erst ohne Loggedaten aufrufen
        alleFlagsTrueOhneLoggeDatenInterceptor.invoke(invocation);

        // Dann mit Loggedaten aufrufen
        alleFlagsTrueInterceptor.invoke(invocation);

        pruefeLogdatei("testAufrufErfolgreichLoggeDatenDirekt");

    }
}
