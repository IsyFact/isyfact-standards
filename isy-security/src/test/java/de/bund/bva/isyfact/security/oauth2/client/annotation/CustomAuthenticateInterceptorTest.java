package de.bund.bva.isyfact.security.oauth2.client.annotation;

import static de.bund.bva.isyfact.security.autoconfigure.IsyOAuth2ClientAutoConfiguration.ClientsConfiguredDependentBeans.AUTHENTICATE_INTERCEPTOR_BEAN;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import de.bund.bva.isyfact.security.AbstractOidcProviderTest;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;

@ActiveProfiles(profiles = "test-clients")
@SpringBootTest
public class CustomAuthenticateInterceptorTest extends AbstractOidcProviderTest {

    @Autowired
    @Qualifier(AUTHENTICATE_INTERCEPTOR_BEAN)
    private Advisor authenticateInterceptor;

    private static int customOrder;

    @BeforeAll
    public static void setup() {
        registerTestClients();
    }

    @Test
    void testAuthenticateInterceptorCanBeChanged() {
        int order = ((AuthenticateInterceptor) authenticateInterceptor).getOrder();
        assertEquals(customOrder, order);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean(AUTHENTICATE_INTERCEPTOR_BEAN)
        public Advisor authenticateInterceptor(Authentifizierungsmanager authentifizierungsmanager) {
            AuthenticateInterceptor custom = new AuthenticateInterceptor(authentifizierungsmanager);
            customOrder = custom.getOrder() + 500;
            custom.setOrder(customOrder);
            return custom;
        }
    }
}
