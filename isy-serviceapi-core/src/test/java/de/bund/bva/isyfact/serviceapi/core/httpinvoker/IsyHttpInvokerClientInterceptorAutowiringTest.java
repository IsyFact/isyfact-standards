package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.DefaultAufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.DummyServiceRemoteBean;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { IsyHttpInvokerClientInterceptorAutowiringTest.TestContext.class })
public class IsyHttpInvokerClientInterceptorAutowiringTest {

    @Autowired
    IsyHttpInvokerClientInterceptor isyHttpInvokerClientInterceptor;

    /**
     * Tests that AufrufKontextToResolver gets autowired into isyHttpInvokerClientInterceptor
     * successfully, without setting it explicitly.
     */
    @Test
    public void autowiringTest() {
        // did Spring Context load and AufrufKontextToResolver autowire successfully?
        assertNotNull(isyHttpInvokerClientInterceptor.getAufrufKontextToResolver());
    }

    @Configuration
    public static class TestContext {
        @Bean
        AufrufKontextToResolver aufrufKontextToResolver() {
            return new DefaultAufrufKontextToResolver();
        }

        @Bean
        IsyHttpInvokerClientInterceptor isyHttpInvokerClientInterceptor() {
            IsyHttpInvokerClientInterceptor interceptor = new IsyHttpInvokerClientInterceptor();
            interceptor.setRemoteSystemName("testName");
            interceptor.setServiceUrl("localhost");
            interceptor.setServiceInterface(DummyServiceRemoteBean.class);
            return interceptor;
        }
    }
}