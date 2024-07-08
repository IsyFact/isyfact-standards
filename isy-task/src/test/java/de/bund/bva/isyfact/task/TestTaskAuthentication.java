package de.bund.bva.isyfact.task;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import de.bund.bva.isyfact.task.test.TestTaskRunAssertion;
import de.bund.bva.isyfact.task.test.config.TestConfig;

import io.micrometer.core.instrument.MeterRegistry;

@ActiveProfiles("security-test")
@SpringBootTest(
        classes = {
                TestConfig.class,
                TestTaskAuthenticationTasks.class
        },
        properties = {
                "isy.task.authentication.enabled=true",
                "spring.task.scheduling.pool.size=2",
        })
class TestTaskAuthentication extends AbstractOidcProviderTest {

    @Autowired
    private MeterRegistry registry;

    @Value("${spring.security.oauth2.client.registration.ropc-testclient.client-id}")
    private String ropcClientId;

    @Value("${spring.security.oauth2.client.registration.ropc-testclient.client-secret}")
    private String ropcClientSecret;

    @Value("${isy.security.oauth2.client.registration.ropc-testclient.username}")
    private String ropcUser;

    @Value("${isy.security.oauth2.client.registration.ropc-testclient.password}")
    private String ropcPassword;

    @Value("${isy.security.oauth2.client.registration.ropc-testclient.bhknz}")
    private String ropcBhknz;

    @Value("${spring.security.oauth2.client.registration.cc-testclient.client-id}")
    private String ccClientId;

    @Value("${spring.security.oauth2.client.registration.cc-testclient.client-secret}")
    private String ccClientSecret;

    @BeforeEach
    public void setup() {
        embeddedOidcProvider.removeAllClients();
        embeddedOidcProvider.removeAllUsers();
        // client with authorization-grant-type=password
        embeddedOidcProvider.addUser(
                ropcClientId,
                ropcClientSecret,
                ropcUser,
                ropcPassword,
                Optional.of(ropcBhknz),
                Collections.singleton("Rolle1")
        );
        embeddedOidcProvider.addClient(
                ccClientId,
                ccClientSecret,
                Collections.singleton("Rolle2")
        );
    }

    @Test
    void testTaskSecured() throws Exception {
        String className = TestTaskAuthenticationTasks.class.getSimpleName();
        String annotatedMethodName = "scheduledTaskSecured";

        SECONDS.sleep(5);

        TestTaskRunAssertion.assertTaskSuccess(className, annotatedMethodName, registry,
                AuthenticationCredentialsNotFoundException.class.getSimpleName());
    }

    @Test
    void testTaskSecuredInsufficientRights() throws Exception {
        String className = TestTaskAuthenticationTasks.class.getSimpleName();
        String annotatedMethodName = "scheduledTaskSecuredInsufficientRights";

        SECONDS.sleep(5);

        TestTaskRunAssertion.assertTaskFailure(className, annotatedMethodName, registry,
                AccessDeniedException.class.getSimpleName());
    }
}
