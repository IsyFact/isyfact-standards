package de.bund.bva.isyfact.serviceapi.core.aop;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.serviceapi.core.aop.test.LoggingKontextAdvisorService;

/**
 * Klasse zum testen der Standardanwendungsreihenfolge des StelltLoggingKontextBereit Advisors
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StelltLoggingKontextBereitAdvisorTest.TestConfig.class,
        properties = {"isy.logging.autoconfiguration.enabled=false"})
public class StelltLoggingKontextBereitAdvisorTest {

    private static final List<String> executionOrder = new ArrayList<>(2);

    @Autowired
    private LoggingKontextAdvisorService service;

    @Autowired
    @Qualifier("CustomStelltLoggingKontextBereitInterceptor")
    private MethodInterceptor stelltLoggingKontextBereitInterceptor;

    @Autowired
    @Qualifier("UnorderedDummyAnnotationInterceptor")
    private MethodInterceptor dummyInterceptor;

    @Before
    public void init() {
        executionOrder.clear();
    }

    @After
    public void tearDown() {
        executionOrder.clear();
    }

    @Test
    public void testStelltLoggingKontextBereitBevorUnordered() {
        service.stelltLoggingKontextBereitBevorUnordered();
        Assert.assertEquals(stelltLoggingKontextBereitInterceptor.toString(), executionOrder.get(0));
        Assert.assertEquals(dummyInterceptor.toString(), executionOrder.get(1));
    }

    @Test
    public void testStelltLoggingKontextBereitNachUnordered() {
        service.stelltLoggingKontextBereitNachUnordered();
        Assert.assertEquals(stelltLoggingKontextBereitInterceptor.toString(), executionOrder.get(0));
        Assert.assertEquals(dummyInterceptor.toString(), executionOrder.get(1));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class TestConfig {

        @Bean
        public LoggingKontextAdvisorService loggingKontextAdvisorService() {
            return new LoggingKontextAdvisorService();
        }

        /**
         * Erstellet einen angepassten {@link StelltLoggingKontextBereitInterceptor}, der nicht weiter tut, als
         * sich selbst in die executionOrder einträgt. Diese Bean ist mit @Primary versehen, so dass sie
         * standardmäßig von dem stelltAufrufKontextBereit Advisor in der Auto Configuration benutzt wird.
         * @return Ein angepasster {@link StelltLoggingKontextBereitInterceptor}
         */
        @Bean(name = "CustomStelltLoggingKontextBereitInterceptor")
        @Primary
        public StelltLoggingKontextBereitInterceptor stelltLoggingKontextBereitInterceptorMock() {
            return new StelltLoggingKontextBereitInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    StelltLoggingKontextBereitAdvisorTest.executionOrder
                            .add(this.toString());
                    return invocation.proceed();
                }

                @Override
                public String toString() {
                    return "LoggingInterceptor";
                }
            };
        }

        /**
         * Erstellt einen DummyInterceptor, der nicht weiter tut, als sich selbst in die executionOrder einträgt.
         * Dieser Interceptor wird in dem Dummy Advisor eingesetzt.
         * @return Einen DummyInterceptor
         */
        @Bean(name = "UnorderedDummyAnnotationInterceptor")
        public MethodInterceptor unorderedDummyAnnotationInterceptor() {
            return new MethodInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    StelltLoggingKontextBereitAdvisorTest.executionOrder
                            .add(this.toString());
                    return invocation.proceed();
                }

                @Override
                public String toString() {
                    return "DummyInterceptor";
                }
            };
        }

        /**
         * Erstellet einen Advisor der einen Pointcut für die Annotation {@link de.bund.bva.isyfact.serviceapi.core.aop.test.LoggingKontextAdvisorService.UnorderedDummyAnnotation}
         * macht und den DummyInterceptor ausführt.
         * @return Ein DummyAdvisor für die Annotation {@link de.bund.bva.isyfact.serviceapi.core.aop.test.LoggingKontextAdvisorService.UnorderedDummyAnnotation}
         */
        @Bean
        public Advisor unorderedDummyAnnotationAdvisor() throws Throwable {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("@annotation(de.bund.bva.isyfact.serviceapi.core.aop.test.LoggingKontextAdvisorService.UnorderedDummyAnnotation) || @within(de.bund.bva.isyfact.serviceapi.core.aop.test.LoggingKontextAdvisorService.UnorderedDummyAnnotation)");
            return new DefaultPointcutAdvisor(pointcut, unorderedDummyAnnotationInterceptor());
        }
    }
}
