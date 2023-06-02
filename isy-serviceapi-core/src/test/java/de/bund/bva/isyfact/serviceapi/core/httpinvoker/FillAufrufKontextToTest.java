package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.DummyServiceImpl;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.DummyServiceRemoteBean;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The tests checking the adhoc generation of a {@link AufrufKontextTo} via {@link DefaultCreateAufrufKontextToStrategy} on HTTP Invoker calls.
 * Only if {@code null} is passed, this process will be executed.
 * <p>
 * There are 4 different use cases:
 * <p>
 * HttpInvokerProxyFactoryBean without {@link AufrufKontextToRemoteInvocationFactory}
 * HttpInvokerProxyFactoryBean with {@link AufrufKontextToRemoteInvocationFactory}
 * IsyHttpInvokerProxyFactoryBean (Default with {@link AufrufKontextToRemoteInvocationFactory})
 * IsyHttpInvokerProxyFactoryBean (Default with {@link AufrufKontextToRemoteInvocationFactory}) without {@code null} parameter
 */
@SpringBootTest(
        classes = FillAufrufKontextToTest.TestConfig.class,
        properties = "isy.logging.autoconfiguration.enabled=false",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class FillAufrufKontextToTest {

    @LocalServerPort
    private int port;

    @Autowired
    @Qualifier("dummyService")
    @SpyBean
    private DummyServiceImpl userService;

    @Qualifier("invokerIsy")
    @Autowired
    private IsyHttpInvokerProxyFactoryBean serviceProxyIsy;

    @Autowired
    @Qualifier("invokerIsy")
    private DummyServiceRemoteBean serviceRemoteBeanIsy;

    @Qualifier("invokerSpring")
    @Autowired
    private HttpInvokerProxyFactoryBean serviceProxySpring;

    @Autowired
    @Qualifier("invokerSpringWithFactory")
    private HttpInvokerProxyFactoryBean serviceProxySpringWithFactory;

    @Autowired
    @Qualifier("invokerSpring")
    private DummyServiceRemoteBean serviceRemoteBeanSpring;

    @Autowired
    @Qualifier("invokerSpringWithFactory")
    private DummyServiceRemoteBean serviceRemoteBeanSpringWithFactory;

    @Autowired
    @Qualifier("aufrufKontextVerwalter")
    private AufrufKontextVerwalter aufrufKontextVerwalter;

    @Autowired
    @SpyBean
    private RemoteInvocationFactory remoteInvocationFactory;

    @Autowired
    @SpyBean
    private CreateAufrufKontextToStrategy createAufrufKontextToStrategy;

    private static final String[] TEST_AUTHORITIES = {"PRIV_test", "ROLE_test"};
    private static final String[] TEST_ROLES = {"ROLE_1", "ROLE_2"};
    private static final String BHKNZ = "900600";
    private static final String NAME = "Test User";
    private static final String PREFFERED_USERNAME = "testuser123";

    @MockBean
    private JwtAuthenticationToken token;

    @BeforeEach
    void setup() {
        Collection<GrantedAuthority> authorities = Arrays.stream(TEST_AUTHORITIES)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Map<String, Object> tokenAttributes = new HashMap<>();
        tokenAttributes.put("roles", Arrays.asList(TEST_ROLES));
        tokenAttributes.put("bhknz", BHKNZ);
        tokenAttributes.put(StandardClaimNames.NAME, NAME);
        tokenAttributes.put(StandardClaimNames.PREFERRED_USERNAME, PREFFERED_USERNAME);

        when(token.getAuthorities()).thenReturn(authorities);
        when(token.getTokenAttributes()).thenReturn(tokenAttributes);

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    void testSecurityContextFilled() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
    }

    @Test
    void testBeansProvided() {
        assertThat(remoteInvocationFactory)
                .isNotNull()
                .isInstanceOf(AufrufKontextToRemoteInvocationFactory.class);

        assertThat(createAufrufKontextToStrategy)
                .isNotNull()
                .isInstanceOf(DefaultCreateAufrufKontextToStrategy.class);
    }

    /**
     * By default, the {@link AufrufKontextToRemoteInvocationFactory} is set to {@link IsyHttpInvokerClientInterceptor} if isy-security is present in the classpath.
     * So the adhoc creation of {@link AufrufKontextTo} will be performed.
     */
    @Test
    void testFillsAufrufKontextToAdhocForIsyfact() {
        serviceProxyIsy.setServiceUrl("http://localhost:" + port + "/isyDummyServiceBean_v1_0_0");

        assertEquals("Hello", serviceRemoteBeanIsy.ping(null, "Hello"));
        verify(remoteInvocationFactory, times(1)).createRemoteInvocation(any());
        verify(createAufrufKontextToStrategy, times(1)).create();

        ArgumentCaptor<AufrufKontextTo> captor1 = ArgumentCaptor.forClass(AufrufKontextTo.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);

        verify(userService, times(1)).ping(captor1.capture(), captor2.capture());

        AufrufKontextTo aufrufKontextTo = captor1.getValue();

        assertThat(aufrufKontextTo)
                .extracting(
                        AufrufKontextTo::getDurchfuehrenderBenutzerKennung,
                        AufrufKontextTo::getDurchfuehrendeBehoerde,
                        AufrufKontextTo::isRollenErmittelt,
                        AufrufKontextTo::getDurchfuehrenderSachbearbeiterName,
                        AufrufKontextTo::getDurchfuehrenderBenutzerPasswort,
                        AufrufKontextTo::getRolle
                ).containsExactly(
                        PREFFERED_USERNAME,
                        BHKNZ,
                        true,
                        NAME,
                        "",
                        TEST_ROLES
                );

        assertThat(captor2.getValue()).isEqualTo("Hello");
    }

    /**
     * The adhoc creation will be performed only if the given value is {@code null}.
     */
    @Test
    void testNoAdhocAufrufKontextToCreated() {
        serviceProxyIsy.setServiceUrl("http://localhost:" + port + "/isyDummyServiceBean_v1_0_0");

        assertEquals("Hello", serviceRemoteBeanIsy.ping(new AufrufKontextTo(), "Hello"));
        verify(remoteInvocationFactory, times(1)).createRemoteInvocation(any());
        verify(createAufrufKontextToStrategy, times(0)).create();

        ArgumentCaptor<AufrufKontextTo> captor1 = ArgumentCaptor.forClass(AufrufKontextTo.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);

        verify(userService, times(1)).ping(captor1.capture(), captor2.capture());

        AufrufKontextTo aufrufKontextTo = captor1.getValue();

        assertThat(aufrufKontextTo)
                .extracting(
                        AufrufKontextTo::getDurchfuehrenderBenutzerKennung,
                        AufrufKontextTo::getDurchfuehrendeBehoerde,
                        AufrufKontextTo::isRollenErmittelt,
                        AufrufKontextTo::getDurchfuehrenderSachbearbeiterName,
                        AufrufKontextTo::getDurchfuehrenderBenutzerPasswort,
                        AufrufKontextTo::getRolle
                ).containsExactly(
                        null,
                        null,
                        false,
                        null,
                        null,
                        null
                );

        assertThat(captor2.getValue()).isEqualTo("Hello");
    }

    /**
     * By default, only in IsyFact equivalent {@link IsyHttpInvokerProxyFactoryBean} the factory is set.
     * So nothing is replaced here.
     */
    @Test
    void testDontFillsAufrufKontextToForSpring() {
        serviceProxySpring.setServiceUrl("http://localhost:" + port + "/dummyServiceBean_v1_0_0");

        assertEquals("Hello", serviceRemoteBeanSpring.ping(null, "Hello"));
        verify(remoteInvocationFactory, times(0)).createRemoteInvocation(any());
        verify(createAufrufKontextToStrategy, times(0)).create();

        ArgumentCaptor<AufrufKontextTo> captor1 = ArgumentCaptor.forClass(AufrufKontextTo.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);

        verify(userService, times(1)).ping(captor1.capture(), captor2.capture());

        AufrufKontextTo aufrufKontextTo = captor1.getValue();

        assertThat(aufrufKontextTo)
                .isNull();

        assertThat(captor2.getValue()).isEqualTo("Hello");
    }

    /**
     * By default, only in IsyFact equivalent {@link IsyHttpInvokerProxyFactoryBean} the factory is set.
     * So the factory must be set for native spring.
     */
    @Test
    void testFillsAufrufKontextToAdhocForSpringFactory() {
        serviceProxySpringWithFactory.setServiceUrl("http://localhost:" + port + "/dummyServiceBean_v1_0_0");

        assertEquals("Hello", serviceRemoteBeanSpringWithFactory.ping(null, "Hello"));

        verify(remoteInvocationFactory, times(1)).createRemoteInvocation(any());
        verify(createAufrufKontextToStrategy, times(1)).create();

        ArgumentCaptor<AufrufKontextTo> captor1 = ArgumentCaptor.forClass(AufrufKontextTo.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);

        verify(userService, times(1)).ping(captor1.capture(), captor2.capture());

        AufrufKontextTo aufrufKontextTo = captor1.getValue();

        assertThat(aufrufKontextTo)
                .extracting(
                        AufrufKontextTo::getDurchfuehrenderBenutzerKennung,
                        AufrufKontextTo::getDurchfuehrendeBehoerde,
                        AufrufKontextTo::isRollenErmittelt,
                        AufrufKontextTo::getDurchfuehrenderSachbearbeiterName,
                        AufrufKontextTo::getDurchfuehrenderBenutzerPasswort,
                        AufrufKontextTo::getRolle
                ).containsExactly(
                        PREFFERED_USERNAME,
                        BHKNZ,
                        true,
                        NAME,
                        "",
                        TEST_ROLES
                );

        assertThat(captor2.getValue()).isEqualTo("Hello");
    }

    @Configuration
    @EnableWebSecurity
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

        @Bean
        public IsyHttpInvokerProxyFactoryBean invokerIsy() {
            IsyHttpInvokerProxyFactoryBean invoker = new IsyHttpInvokerProxyFactoryBean();
            invoker.setServiceUrl("http://localhost:8080/isyDummyServiceBean_v1_0_0");
            invoker.setServiceInterface(DummyServiceRemoteBean.class);
            invoker.setRemoteSystemName("DummyService");
            return invoker;
        }

        @Bean
        public HttpInvokerProxyFactoryBean invokerSpring() {
            HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
            invoker.setServiceUrl("http://localhost:8080/dummyServiceBean_v1_0_0");
            invoker.setServiceInterface(DummyServiceRemoteBean.class);
            return invoker;
        }

        @Bean
        public HttpInvokerProxyFactoryBean invokerSpringWithFactory(RemoteInvocationFactory remoteInvocationFactory) {
            HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
            invoker.setServiceUrl("http://localhost:8080/dummyServiceBean_v1_0_0");
            invoker.setServiceInterface(DummyServiceRemoteBean.class);
            invoker.setRemoteInvocationFactory(remoteInvocationFactory);
            return invoker;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .anyRequest().permitAll();

            http.csrf().disable();

            return http.build();
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
