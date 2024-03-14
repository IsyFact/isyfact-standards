package de.bund.bva.isyfact.task.security.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.security.Authenticator;
import de.bund.bva.isyfact.task.security.AuthenticatorFactory;
import de.bund.bva.isyfact.task.test.config.TestConfig;

@SpringBootTest(
        classes = TestConfig.class,
        properties = {"isy.task.authentication.enabled"}
)
class IsySecurityAuthenticatorFactoryTest {

    @Autowired
    AuthenticatorFactory authFactory;

    @Autowired
    IsyTaskConfigurationProperties isyTaskConfigurationProperties;

    @Test
    void getAuthenticator_doesNotThrowWithUnknownTaskId() {
        final String UNKNOWN_TASK_ID = "DieserTaskWurdeNichtKonfiguriert";
        // check task is not configured
        assertThat(isyTaskConfigurationProperties.getTasks().get(UNKNOWN_TASK_ID)).isNull();

        assertThat(authFactory).isNotNull();
        assertThat(authFactory).isInstanceOf(IsySecurityAuthenticatorFactory.class);

        assertThatNoException().isThrownBy(() -> {
            // no NullPointerException due to Map.get() from Properties
            Authenticator authenticator = authFactory.getAuthenticator(UNKNOWN_TASK_ID);
            // expect NoOpAuthenticator instead
            assertThat(authenticator).isInstanceOf(NoOpAuthenticator.class);
        });
    }
}