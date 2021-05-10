package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InterruptedIOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.isyfact.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.DummyServiceImpl;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.DummyServiceRemoteBean;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TimeoutWiederholungHttpInvokerRequestExecutorTest.TestConfig.class,
    properties = "isy.logging.autoconfiguration.enabled=false",
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TimeoutWiederholungHttpInvokerRequestExecutorTest {

    @LocalServerPort
    private int port;

    @Autowired
    @Qualifier("dummyService")
    private DummyServiceImpl dummyService;

    @Autowired
    private IsyHttpInvokerProxyFactoryBean serviceProxy;

    @Autowired
    @Qualifier("invoker")
    private DummyServiceRemoteBean serviceRemoteBean;

    @Autowired
    private TimeoutWiederholungHttpInvokerRequestExecutor executor;

    @Before
    public void setPort() {
        serviceProxy.setServiceUrl("http://localhost:" + port + "/dummyServiceBean_v1_0_0");
    }

    @Test
    public void testTimeoutKurz() {
        dummyService.setWaitTime(0);
        executor.setTimeout(500);
        assertEquals("Hello", serviceRemoteBean.ping("Hello"));
        assertEquals(1, dummyService.getAnzahlAufrufe());
    }

    @Test
    public void testTimeoutLang() {
        dummyService.setWaitTime(30000);
        executor.setTimeout(60000);
        assertEquals("Hello", serviceRemoteBean.ping("Hello"));
        assertEquals(1, dummyService.getAnzahlAufrufe());
    }

    @Test
    public void testAufrufWiederholung() {
        dummyService.setWaitTime(200);
        executor.setTimeout(50);
        executor.setWiederholungenAbstand(500);
        executor.setAnzahlWiederholungen(10);
        long t0 = System.currentTimeMillis();
        try {
            serviceRemoteBean.ping("Hello");
            fail("Exception erwartet");
        } catch (Throwable t) {
            long t1 = System.currentTimeMillis();
            assertTrue(InterruptedIOException.class.isAssignableFrom(t.getCause().getClass()));
            assertEquals(10, dummyService.getAnzahlAufrufe());
            assertTrue("Wiederholungspausen wurde nicht eingehalten: " + (t1 - t0), (t1 - t0) > 4000);
        }
    }

    @Configuration
    @EnableAutoConfiguration
    public static class TestConfig {

        @Bean(name = "/dummyServiceBean_v1_0_0")
        HttpInvokerServiceExporter pingService(DummyServiceImpl dummyService) {
            HttpInvokerServiceExporter exporter = new IsyHttpInvokerServiceExporter(new AufrufKontextVerwalterImpl<>());
            exporter.setService(dummyService);
            exporter.setServiceInterface(DummyServiceRemoteBean.class);
            return exporter;
        }

        @Bean
        public IsyHttpInvokerProxyFactoryBean invoker(HttpInvokerRequestExecutor executor) {
            IsyHttpInvokerProxyFactoryBean invoker = new IsyHttpInvokerProxyFactoryBean();
            invoker.setServiceUrl("http://localhost:8080/dummyServiceBean_v1_0_0");
            invoker.setServiceInterface(DummyServiceRemoteBean.class);
            invoker.setRemoteSystemName("DummyService");
            invoker.setHttpInvokerRequestExecutor(executor);

            return invoker;
        }

        @Bean
        public DummyServiceImpl dummyService() {
            return new DummyServiceImpl();
        }

        @Bean
        public TimeoutWiederholungHttpInvokerRequestExecutor requestExecutor() {
            return new TimeoutWiederholungHttpInvokerRequestExecutor(new AufrufKontextVerwalterStub<>());
        }
    }
}
