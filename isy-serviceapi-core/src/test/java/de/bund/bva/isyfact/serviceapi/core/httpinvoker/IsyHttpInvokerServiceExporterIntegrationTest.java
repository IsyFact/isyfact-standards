package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Proxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.isyfact.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.isyfact.security.autoconfigure.IsySecurityAutoConfiguration;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.user.User;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.user.UserImpl;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.user.UserInvocationHandler;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.DummyServiceImpl;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.DummyServiceRemoteBean;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Tests for {@link IsyHttpInvokerServiceExporter} that require a Spring Context.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IsyHttpInvokerServiceExporterIntegrationTest.TestConfig.class,
        properties = "isy.logging.autoconfiguration.enabled=false",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ImportAutoConfiguration(value = IsySecurityAutoConfiguration.class, exclude = SecurityAutoConfiguration.class)
public class IsyHttpInvokerServiceExporterIntegrationTest {

    public static final String TEST_BEARER_TOKEN = "TEST_BEARER_TOKEN";

    @LocalServerPort
    private int port;

    @Autowired
    @Qualifier("dummyService")
    @SpyBean
    private DummyServiceImpl userService;

    @Qualifier("invoker")
    @Autowired
    private IsyHttpInvokerProxyFactoryBean serviceProxy;

    @Autowired
    @Qualifier("invoker")
    private DummyServiceRemoteBean serviceRemoteBean;

    @Autowired
    @Qualifier("aufrufKontextVerwalter")
    private AufrufKontextVerwalter aufrufKontextVerwalter;

    @Autowired
    @SpyBean
    private RemoteInvocationFactory remoteInvocationFactory;

    @Autowired
    @SpyBean
    private CreateAufrufKontextToStrategy createAufrufKontextToStrategy;

    @Before
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test(expected = RemoteAccessException.class)
    public void testAddUserNotAllowedProxyObject() {
        serviceProxy.setServiceUrl("http://localhost:" + port + "/isyDummyServiceBean_v1_0_0");
        userService.setWaitTime(0);
        User b = new UserImpl();
        UserInvocationHandler uih = new UserInvocationHandler(b);
        User a = (User) Proxy.newProxyInstance(UserImpl.class.getClassLoader(), new Class[]{User.class}, uih);
        serviceRemoteBean.addUser(a);
    }

    @Test
    public void testAddUserAllowedProxyObject() {
        serviceProxy.setServiceUrl("http://localhost:" + port + "/dummyServiceBean_v1_0_0");
        userService.setWaitTime(0);
        User b = new UserImpl();
        UserInvocationHandler uih = new UserInvocationHandler(b);
        User a = (User) Proxy.newProxyInstance(UserImpl.class.getClassLoader(), new Class[]{User.class}, uih);
        assertEquals("Added user successful.", serviceRemoteBean.addUser(a));
    }

    @Test
    public void testIsBearerToken() {
        serviceProxy.setServiceUrl("http://localhost:" + port + "/isyBearerTokenServiceBean_v1_0_0");
        userService.setWaitTime(0);
        User b = new UserImpl();
        UserInvocationHandler uih = new UserInvocationHandler(b);
        User a = (User) Proxy.newProxyInstance(UserImpl.class.getClassLoader(), new Class[]{User.class}, uih);
        assertEquals("Added user successful.", serviceRemoteBean.addUser(a));

        assertEquals(TEST_BEARER_TOKEN, aufrufKontextVerwalter.getBearerToken());
    }

    @Test
    public void testFillsAufrufKontextTo() {
        serviceProxy.setServiceUrl("http://localhost:" + port + "/dummyServiceBean_v1_0_0");

        assertThat(remoteInvocationFactory)
                .isNotNull()
                .isInstanceOf(AufrufKontextToRemoteInvocationFactory.class);

        assertThat(createAufrufKontextToStrategy)
                .isNotNull()
                .isInstanceOf(DefaultCreateAufrufKontextToStrategy.class);

        assertEquals("Hello", serviceRemoteBean.ping(null, "Hello"));
        verify(remoteInvocationFactory, times(1)).createRemoteInvocation(any());
        verify(createAufrufKontextToStrategy, times(1)).create();

        ArgumentCaptor<AufrufKontextTo> captor1 = ArgumentCaptor.forClass(AufrufKontextTo.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);

        verify(userService, times(1)).ping(captor1.capture(), captor2.capture());

        assertThat(captor1.getValue())
                .isNotNull();

        assertThat(captor2.getValue()).isEqualTo("Hello");
    }

    @Configuration
    @EnableAutoConfiguration
    public static class TestConfig {

        @Bean(name = "/isyDummyServiceBean_v1_0_0")
        IsyHttpInvokerServiceExporter userService(DummyServiceImpl dummyService) {
            IsyHttpInvokerServiceExporter exporter = new IsyHttpInvokerServiceExporter(new AufrufKontextVerwalterImpl<>());
            exporter.setService(dummyService);
            exporter.setServiceInterface(DummyServiceRemoteBean.class);
            return exporter;
        }

        @Bean(name = "/dummyServiceBean_v1_0_0")
        HttpInvokerServiceExporter userServiceWithProxy(DummyServiceImpl dummyService) {
            HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
            exporter.setService(dummyService);
            exporter.setServiceInterface(DummyServiceRemoteBean.class);

            return exporter;
        }

        @Bean(name = "/isyBearerTokenServiceBean_v1_0_0")
        IsyHttpInvokerServiceExporter userServiceWithProxy(DummyServiceImpl dummyService, AufrufKontextVerwalter aufrufKontextVerwalter) {
            IsyHttpInvokerServiceExporter exporter = new IsyHttpInvokerServiceExporter(aufrufKontextVerwalter);
            exporter.setService(dummyService);
            exporter.setServiceInterface(DummyServiceRemoteBean.class);
            exporter.setAcceptProxyClasses(true);

            return exporter;
        }

        @Bean
        public IsyHttpInvokerProxyFactoryBean invoker() {
            IsyHttpInvokerProxyFactoryBean invoker = new IsyHttpInvokerProxyFactoryBean();
            invoker.setServiceUrl("http://localhost:8080/dummyServiceBean_v1_0_0");
            invoker.setServiceInterface(DummyServiceRemoteBean.class);
            invoker.setRemoteSystemName("DummyService");

            AufrufKontextVerwalterStub<AufrufKontext> aufrufKontextVerwalterStub = new AufrufKontextVerwalterStub<>();
            aufrufKontextVerwalterStub.setBearerToken(TEST_BEARER_TOKEN);

            TimeoutWiederholungHttpInvokerRequestExecutor reqExecutor = new TimeoutWiederholungHttpInvokerRequestExecutor(aufrufKontextVerwalterStub);
            reqExecutor.setAnzahlWiederholungen(5);
            reqExecutor.setTimeout(20000);
            reqExecutor.setWiederholungenAbstand(200);

            invoker.setHttpInvokerRequestExecutor(reqExecutor);

            return invoker;
        }

        @Bean
        public DummyServiceImpl dummyService() {
            return new DummyServiceImpl();
        }

        @Bean(name = "aufrufKontextVerwalter")
        public AufrufKontextVerwalter aufrufKontextVerwalter() {
            return new AufrufKontextVerwalterImpl();
        }

    }
}
