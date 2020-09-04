package test.de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import static org.junit.Assert.*;

import java.lang.reflect.Proxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.pliscommon.serviceapi.core.httpinvoker.IsyHttpInvokerServiceExporter;

import test.de.bund.bva.pliscommon.serviceapi.core.httpinvoker.user.User;
import test.de.bund.bva.pliscommon.serviceapi.core.httpinvoker.user.UserImpl;
import test.de.bund.bva.pliscommon.serviceapi.core.httpinvoker.user.UserInvocationHandler;
import test.de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.DummyServiceImpl;
import test.de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.DummyServiceRemoteBean;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IsyHttpInvokerServiceExporterTest.TestConfig.class,
        properties = "isy.logging.autoconfiguration.enabled=false",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IsyHttpInvokerServiceExporterTest {

    @LocalServerPort
    private int port;

    @Autowired
    @Qualifier("dummyService")
    private DummyServiceImpl userService;

    @Qualifier("invoker")
    @Autowired
    private HttpInvokerProxyFactoryBean serviceProxy;

    @Autowired
    @Qualifier("invoker")
    private DummyServiceRemoteBean serviceRemoteBean;

    @Test(expected = RemoteAccessException.class)
    public void testAddUserNotAllowedProxyObject() {
        serviceProxy.setServiceUrl("http://localhost:" + port + "/isyDummyServiceBean");
        userService.setWaitTime(0);
        User b = new UserImpl();
        UserInvocationHandler uih = new UserInvocationHandler(b);
        User a = (User) Proxy.newProxyInstance(UserImpl.class.getClassLoader(), new Class[]{User.class}, uih);
        serviceRemoteBean.addUser(a);
    }

    @Test
    public void testAddUserAllowedProxyObject() {
        serviceProxy.setServiceUrl("http://localhost:" + port + "/dummyServiceBean");
        userService.setWaitTime(0);
        User b = new UserImpl();
        UserInvocationHandler uih = new UserInvocationHandler(b);
        User a = (User) Proxy.newProxyInstance(UserImpl.class.getClassLoader(), new Class[]{User.class}, uih);
        assertEquals("Added user successful.", serviceRemoteBean.addUser(a));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class TestConfig {

        @Bean(name = "/isyDummyServiceBean")
        IsyHttpInvokerServiceExporter userService(DummyServiceImpl dummyService) {
            IsyHttpInvokerServiceExporter exporter = new IsyHttpInvokerServiceExporter(new AufrufKontextVerwalterImpl<>());
            exporter.setService(dummyService);
            exporter.setServiceInterface(DummyServiceRemoteBean.class);
            return exporter;
        }

        @Bean(name = "/dummyServiceBean")
        HttpInvokerServiceExporter userServiceWithProxy(DummyServiceImpl dummyService) {
            HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
            exporter.setService(dummyService);
            exporter.setServiceInterface(DummyServiceRemoteBean.class);
            return exporter;
        }

        @Bean
        public HttpInvokerProxyFactoryBean invoker() {
            HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
            invoker.setServiceUrl("http://localhost:8080/dummyServiceBean");
            invoker.setServiceInterface(DummyServiceRemoteBean.class);
            return invoker;
        }

        @Bean
        public DummyServiceImpl dummyService() {
            return new DummyServiceImpl();
        }

    }
}
