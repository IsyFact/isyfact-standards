package de.bund.bva.isyfact.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import de.bund.bva.isyfact.logging.hilfsklassen.DefaultMethodInvocation;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielKlasse;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielKlasse2;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielParameterPerson;
import de.bund.bva.isyfact.logging.util.BeanToMapConverter;
import de.bund.bva.isyfact.logging.util.LoggingMethodInterceptor;

/**
 * Testing the LogInterceptors.
 *
 */
@SpringJUnitConfig(classes = LogInterceptorTest.LogInterceptorTestConfig.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class LogInterceptorTest extends AbstractLogTest {

    /**
     * method intercepter with all flags "true" (maximum output)
     */
    @Autowired
    @Qualifier("alleFlagsTrueInterceptor")
    private LoggingMethodInterceptor alleFlagsTrueInterceptor;

    /**
     * method intercepter with all flags "true". flag 'loggeDaten' is false and not set
     */
    @Autowired
    @Qualifier("alleFlagsTrueOhneLoggeDatenInterceptor")
    private LoggingMethodInterceptor alleFlagsTrueOhneLoggeDatenInterceptor;

    /**
     * method intercepter with all flags "false" (minimum output)
     */
    @Autowired
    @Qualifier("alleFlagsFalseInterceptor")
    private LoggingMethodInterceptor alleFlagsFalseInterceptor;

    /**
     * method intercepter with all flags "true" (maximum output), but converter is configured,
     * so that package 'de.bund.bva' is exclueded
     */
    @Autowired
    @Qualifier("individuellerInterceptor")
    private LoggingMethodInterceptor individuellerInterceptor;

    /**
     * target class for which the log intercepter should create log entries for system boundary
     */
    @Autowired
    private TestZielKlasse boundaryZielKlasse;

    /**
     * target class for which the log intercepter should create log entries for component boundary
     */
    @Autowired
    private TestZielKlasse2 componentZielKlasse;

    /**
     * Test logging with a successful method call on maximal log output. The intercepter is called direct
     * and not with Spring.
     *
     * @throws Throwable
     *             if an expception is thrown during the test.
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
     * Test logging with a not successful method call (exception) on maximal log output.
     * The intercepter is called direct and not with Spring.
     *
     * @throws Throwable
     *             if an expception is thrown during the test.
     */
    @Test
    public void testAufrufMitExceptionDirekt() throws Throwable {

        Method methode = TestZielKlasse.class.getMethod("setzeNameException", TestZielParameterPerson.class,
                String.class);

        MethodInvocation invocation = new DefaultMethodInvocation(new TestZielKlasse(), methode,
                new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"), "TestParameter 2");

        try {
            alleFlagsTrueOhneLoggeDatenInterceptor.invoke(invocation);
            Assertions.fail("Es wurde eine Exception erwartet - der Aufruf war aber erfolgreich.");
        } catch (InvocationTargetException e) {
            // exception is expected.
            e.printStackTrace();
        }

        pruefeLogdatei("testAufrufMitExceptionInterceptorDirekt", true);

    }

    /**
     * Test logging with a successful method call on minimal log output. The intercepter is called direct
     * and not with Spring.
     *
     * @throws Throwable
     *             if an expception is thrown during the test.
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
     * Test logging with a not successful method call (exception) on minimal log output.
     * The intercepter is called direct and not with Spring.
     *
     * @throws Throwable
     *             if an expception is thrown during the test.
     */
    @Test
    public void testAufrufMitExceptionParameterFalseDirekt() throws Throwable {

        Method methode = TestZielKlasse.class.getMethod("setzeNameException", TestZielParameterPerson.class,
                String.class);

        MethodInvocation invocation = new DefaultMethodInvocation(new TestZielKlasse(), methode,
                new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"), "TestParameter 2");

        try {
            alleFlagsFalseInterceptor.invoke(invocation);
            Assertions.fail("Es wurde eine Exception erwartet - der Aufruf war aber erfolgreich.");
        } catch (InvocationTargetException e) {
            // exception is expected.
            pruefeLogdatei("testAufrufMitExceptionParameterFalse");
        }

    }

    /**
     * Test logging with a successful system interface call. The intercepter is configured by Spring and tested.
     *
     * @throws Throwable
     *             if an expception is thrown during the test.
     */
    @Test
    public void testBoundaryAufrufErfolgreich() throws Throwable {

        boundaryZielKlasse.setzeName(new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"),
                "TestParameter 2");
        // analogue to direct call (all parameter are "true")
        pruefeLogdatei("testAufrufErfolgreichInterceptor");

    }

    /**
     * Test logging with a not successful system interface call (exception).
     * The intercepter is configured by Spring and tested.
     *
     * @throws Throwable
     *             if an expception is thrown during the test.
     */
    @Test
    public void testBoundaryAufrufMitException() throws Throwable {

        try {
            boundaryZielKlasse.setzeNameException(new TestZielParameterPerson("Mustermann", "Max", "Peter",
                    "Hans"), "TestParameter 2");
        } catch (Exception e) {
            // exception is expected.
            // analogue to direct call (all parameter are "true")
            pruefeLogdatei("testAufrufMitExceptionInterceptor", true);
        }

    }

    /**
     * Test logging with a successful component interface call. The intercepter is configured by Spring and tested.
     *
     * @throws Throwable
     *             if an expception is thrown during the test.
     */
    @Test
    public void testComponentAufrufErfolgreich() throws Throwable {

        componentZielKlasse.setzeName(new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"),
                "TestParameter 2");
        pruefeLogdatei("testAufrufErfolgreichComponentInterceptor");

    }

    /**
     * Test logging with a not successful component interface call (exception).
     * The intercepter is configured by Spring and tested.
     *
     * @throws Throwable
     *             if an expception is thrown during the test.
     */
    @Test
    public void testComponentAufrufMitException() throws Throwable {

        try {
            componentZielKlasse.setzeNameException(new TestZielParameterPerson("Mustermann", "Max", "Peter",
                    "Hans"), "TestParameter 2");
        } catch (Exception e) {
            // exception is expected.
            pruefeLogdatei("testAufrufMitExceptionComponentInterceptor");
        }

    }

    /**
     * Testing initializing of includes and excludes of the BeanMapper, if not a standard mapper should be used.
     *
     * @throws Exception
     *             if an expception is thrown during the test.
     */
    @Test
    public void testInitialisierungIncludesExcludes() throws Exception {
        List<String> includes = Collections.singletonList("de.bund.bva.xyz");
        List<String> excludes = Collections.singletonList("java.util.xyz");
        LoggingMethodInterceptor loggingMethodInterceptor = new LoggingMethodInterceptor(includes, excludes);
        loggingMethodInterceptor.afterPropertiesSet();
        BeanToMapConverter converter = (BeanToMapConverter) loggingMethodInterceptor.getLogHelper()
                .getKonverter();
        Assertions.assertEquals(includes, converter.getIncludes());
        Assertions.assertEquals(excludes, converter.getExcludes());
    }

    /**
     * Test logging with a not successful method (exception) call on maximal log output.
     * The intercepter is called direct and not with Spring. The interceptor is configured,
     * so that the beanconverter ignores all beans in package 'de.bund.bva' for serialization
     *
     * @throws Throwable
     *             if an expception is thrown during the test.
     */
    @Test
    public void testAufrufMitExceptionDirektIndividuell() throws Throwable {

        Method methode = TestZielKlasse.class.getMethod("setzeNameException", TestZielParameterPerson.class,
                String.class);

        MethodInvocation invocation = new DefaultMethodInvocation(new TestZielKlasse(), methode,
                new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"), "TestParameter 2");

        try {
            individuellerInterceptor.invoke(invocation);
            Assertions.fail("Es wurde eine Exception erwartet - der Aufruf war aber erfolgreich.");
        } catch (InvocationTargetException e) {
            // exception is expected.
            e.printStackTrace();
        }

        pruefeLogdatei("testAufrufMitExceptionInterceptorIndividuell", true);

    }

    /**
     * Test logging with a successful method call on maximal log output. The intercepter is called direct
     * and not with Spring.
     *
     * @throws Throwable
     *             if an expception is thrown during the test.
     */
    @Test
    public void testAufrufErfolgreichLoggeDatenDirekt() throws Throwable {

        Method methode = TestZielKlasse.class.getMethod("setzeName", TestZielParameterPerson.class,
                String.class);

        MethodInvocation invocation = new DefaultMethodInvocation(new TestZielKlasse(), methode,
                new TestZielParameterPerson("Mustermann", "Max", "Peter", "Hans"), "TestParameter 2");

        // first call without logging
        alleFlagsTrueOhneLoggeDatenInterceptor.invoke(invocation);

        // second call with logging
        alleFlagsTrueInterceptor.invoke(invocation);

        pruefeLogdatei("testAufrufErfolgreichLoggeDatenDirekt");

    }

    @Configuration
    @EnableAspectJAutoProxy
    static class LogInterceptorTestConfig {
        @Bean
        LoggingMethodInterceptor alleFlagsTrueInterceptor() {
            LoggingMethodInterceptor loggingMethodInterceptor = new LoggingMethodInterceptor();
            loggingMethodInterceptor.setLoggeErgebnis(true);
            loggingMethodInterceptor.setLoggeDaten(true);
            loggingMethodInterceptor.setLoggeDatenBeiException(true);
            loggingMethodInterceptor.setLoggeAufruf(true);
            loggingMethodInterceptor.setLoggeDauer(true);
            loggingMethodInterceptor.setLoggeMaximaleParameterGroesse(1000000);

            return loggingMethodInterceptor;
        }

        @Bean
        LoggingMethodInterceptor alleFlagsTrueOhneLoggeDatenInterceptor() {
            LoggingMethodInterceptor loggingMethodInterceptor = new LoggingMethodInterceptor();
            loggingMethodInterceptor.setLoggeErgebnis(true);
            loggingMethodInterceptor.setLoggeDatenBeiException(true);
            loggingMethodInterceptor.setLoggeAufruf(true);
            loggingMethodInterceptor.setLoggeDauer(true);

            return loggingMethodInterceptor;
        }

        @Bean
        LoggingMethodInterceptor alleFlagsFalseInterceptor() {
            LoggingMethodInterceptor loggingMethodInterceptor = new LoggingMethodInterceptor();
            loggingMethodInterceptor.setLoggeErgebnis(false);
            loggingMethodInterceptor.setLoggeDatenBeiException(false);
            loggingMethodInterceptor.setLoggeAufruf(false);
            loggingMethodInterceptor.setLoggeDauer(false);

            return loggingMethodInterceptor;
        }

        @Bean
        LoggingMethodInterceptor boundaryLogInterceptor() {
            return new LoggingMethodInterceptor();
        }

        @Bean
        LoggingMethodInterceptor componentLogInterceptor() {
            LoggingMethodInterceptor loggingMethodInterceptor = new LoggingMethodInterceptor();
            loggingMethodInterceptor.setLoggeErgebnis(false);
            loggingMethodInterceptor.setLoggeDatenBeiException(false);

            return loggingMethodInterceptor;
        }

        @Bean
        TestZielKlasse zielKlasse() {
            return new TestZielKlasse();
        }

        @Bean
        TestZielKlasse2 zielKlasse2() {
            return new TestZielKlasse2();
        }

        @Bean
        LoggingMethodInterceptor individuellerInterceptor() {
            LoggingMethodInterceptor loggingMethodInterceptor = new LoggingMethodInterceptor(Collections.singletonList("irgendein.package"),
                Collections.singletonList("de.bund.bva"));

            loggingMethodInterceptor.setLoggeErgebnis(true);
            loggingMethodInterceptor.setLoggeDatenBeiException(true);
            loggingMethodInterceptor.setLoggeAufruf(true);
            loggingMethodInterceptor.setLoggeDauer(true);

            return loggingMethodInterceptor;
        }

        @Bean
        Advisor boundaryLogAdvice(@Qualifier("boundaryLogInterceptor") MethodInterceptor boundaryLogInterceptor) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("@within(de.bund.bva.isyfact.logging.annotation.Systemgrenze)");
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, boundaryLogInterceptor);
            advisor.setOrder(1000);
            return advisor;
        }

        @Bean
        Advisor componentLogAdvice(@Qualifier("componentLogInterceptor") MethodInterceptor componentLogInterceptor) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("@within(de.bund.bva.isyfact.logging.annotation.Komponentengrenze)");
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, componentLogInterceptor);
            advisor.setOrder(1000);
            return advisor;
        }
    }
}
