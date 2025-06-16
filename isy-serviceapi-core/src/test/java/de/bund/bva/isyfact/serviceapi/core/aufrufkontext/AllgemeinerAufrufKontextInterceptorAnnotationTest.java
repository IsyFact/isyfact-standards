package de.bund.bva.isyfact.serviceapi.core.aufrufkontext;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextFactoryImpl;
import de.bund.bva.isyfact.serviceapi.core.aop.test.AufrufKontextSstTestBean;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.helper.DebugAufrufKontextVerwalter;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

import junit.framework.AssertionFailedError;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Tests functionality of {@link StelltAufrufKontextBereitInterceptor}.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AllgemeinerAufrufKontextInterceptorAnnotationTest.TestConfig.class)
public class AllgemeinerAufrufKontextInterceptorAnnotationTest {

    /**
     * Access to the AufrufKontextVerwalter to check if the call context is set / not set correctly.
     */
    @Autowired
    public DebugAufrufKontextVerwalter aufrufKontextVerwalter;

    /** Simulation of a service interface which is called. */
    @Autowired
    public AufrufKontextSstTestBean sst;

    /**
     * Create a AufrufKontext transport object.
     *
     * @return AufrufKontextTo containing dummy data
     */
    private static AufrufKontextTo createAufrufKontextTo() {
        AufrufKontextTo aufrufKontextTo = new AufrufKontextTo();
        aufrufKontextTo.setDurchfuehrendeBehoerde("TEST");
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung("TEST");
        aufrufKontextTo.setDurchfuehrenderBenutzerPasswort("TEST");
        aufrufKontextTo.setDurchfuehrenderSachbearbeiterName("TEST");
        aufrufKontextTo.setKorrelationsId("TEST");
        aufrufKontextTo.setRolle(new String[] { "TEST" });
        aufrufKontextTo.setRollenErmittelt(true);
        return aufrufKontextTo;
    }

    /**
     * Compares an AufrufKontextTo with a AufrufKontext.
     * <p>
     * Throws {@link AssertionFailedError}, if the parameters don't match.
     *
     * @param to      transport object
     * @param kontext AufrufKontext
     */
    private static void assertEqualData(AufrufKontextTo to, AufrufKontext kontext) {
        assertEquals(to.getDurchfuehrendeBehoerde(), kontext.getDurchfuehrendeBehoerde());
        assertEquals(to.getDurchfuehrenderBenutzerKennung(), kontext.getDurchfuehrenderBenutzerKennung());
        assertEquals(to.getDurchfuehrenderSachbearbeiterName(),
            kontext.getDurchfuehrenderSachbearbeiterName());
        assertEquals(to.getDurchfuehrenderBenutzerPasswort(), kontext.getDurchfuehrenderBenutzerPasswort());
        assertEquals(to.getKorrelationsId(), kontext.getKorrelationsId());
        assertArrayEquals(to.getRolle(), kontext.getRolle());
        assertEquals(to.isRollenErmittelt(), kontext.isRollenErmittelt());
    }

    /**
     * reset AufrufKontext before every test case
     */
    @Before
    public void leereAufrufKontext() {
        aufrufKontextVerwalter.resetAufrufKontext();
    }

    @After
    public void assertAufrufKontextLeer() {
        // the Aufrufkontext shouldn't be set after a test case
        assertNull(aufrufKontextVerwalter.getAufrufKontext());
    }

    @Test
    public void stelltAufrufKontextNichtBereitOhneParameter() {
        sst.stelltAufrufKontextNichtBereitOhneParameter();
        assertNull(aufrufKontextVerwalter.getLetzterAufrufKontext());
    }

    @Test
    public void stelltAufrufKontextNichtBereitMitParameter() {
        sst.stelltAufrufKontextNichtBereitMitParameter(createAufrufKontextTo());
        assertNull(aufrufKontextVerwalter.getLetzterAufrufKontext());
    }

    @Test
    public void stelltAufrufKontextBereitMitParameter() {
        AufrufKontextTo aufrufKontextTo = createAufrufKontextTo();
        sst.stelltAufrufKontextBereitMitParameter(aufrufKontextTo);
        assertEqualData(aufrufKontextTo, aufrufKontextVerwalter.getLetzterAufrufKontext());
    }

    @Test
    public void stelltAufrufKontextBereitMitMehrerenParametern() {
        AufrufKontextTo aufrufKontextTo = createAufrufKontextTo();
        sst.stelltAufrufKontextBereitMitMehrerenParametern(aufrufKontextTo, "42");
        assertEqualData(aufrufKontextTo, aufrufKontextVerwalter.getLetzterAufrufKontext());
    }

    @Test
    public void stelltAufrufKontextBereitMitMehrerenParameterMehrereKontexte() {
        AufrufKontextTo aufrufKontextTo = createAufrufKontextTo();
        sst.stelltAufrufKontextBereitMitMehrerenParameterMehrereKontexte(aufrufKontextTo,
            createAufrufKontextTo());
        assertEqualData(aufrufKontextTo, aufrufKontextVerwalter.getLetzterAufrufKontext());
    }

    @Test
    public void stelltNullAufrufKontextBereitOhneParameter() {
        sst.stelltAufrufKontextBereitOhneParameter();
        assertNull(aufrufKontextVerwalter.getAufrufKontext());
    }

    @Configuration
    @EnableAspectJAutoProxy
    public static class TestConfig {

        @Bean
        public AufrufKontextToResolver aufrufKontextToResolver() {
            return new DefaultAufrufKontextToResolver();
        }

        @Bean
        public MapperFacade mapperFacade() {
            return new DefaultMapperFactory.Builder().build().getMapperFacade();
        }

        @Bean
        public StelltAllgemeinenAufrufKontextBereitInterceptor interceptor(AufrufKontextFactory factory,
            AufrufKontextVerwalter verwalter, MapperFacade mapper, AufrufKontextToResolver aufrufKontextToResolver) {
            return new StelltAllgemeinenAufrufKontextBereitInterceptor(mapper, factory, verwalter, aufrufKontextToResolver);
        }

        @Bean
        public Advisor stelltAufrufKontextBereitAdvisor(
            StelltAllgemeinenAufrufKontextBereitInterceptor interceptor) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression(
                "@annotation(de.bund.bva.isyfact.serviceapi.core.aufrufkontext.StelltAufrufKontextBereit)");
            return new DefaultPointcutAdvisor(pointcut, interceptor);
        }

        @Bean
        public AufrufKontextVerwalter aufrufKontextVerwalter() {
            return new DebugAufrufKontextVerwalter();
        }

        @Bean
        public AufrufKontextFactory aufrufKontextFactory() {
            return new AufrufKontextFactoryImpl();
        }

        @Bean
        public AufrufKontextSstTestBean bereitgestellteSchnittstelle() {
            return new AufrufKontextSstTestBean();
        }
    }

}
