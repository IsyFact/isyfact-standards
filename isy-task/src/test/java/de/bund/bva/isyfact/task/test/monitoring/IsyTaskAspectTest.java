package de.bund.bva.isyfact.task.test.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.support.ResourceBundleMessageSource;

import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties.TaskConfig;
import de.bund.bva.isyfact.task.exception.TaskKonfigurationInvalidException;
import de.bund.bva.isyfact.task.konfiguration.HostHandler;
import de.bund.bva.isyfact.task.monitoring.IsyTaskAspect;
import de.bund.bva.isyfact.task.security.Authenticator;
import de.bund.bva.isyfact.task.security.AuthenticatorFactory;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class IsyTaskAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private AuthenticatorFactory authenticatorFactory;

    @Mock
    private Authenticator authenticator;

    @Mock
    private HostHandler hostHandler;

    @InjectMocks
    private IsyTaskAspect isyTaskAspect;

    @Spy
    private IsyTaskConfigurationProperties properties = new IsyTaskConfigurationProperties();

    @Spy
    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    @Spy
    private ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();

    private TaskConfig testTaskConfig;

    @BeforeEach
    public void prepare() throws Exception {
        testTaskConfig = new TaskConfig();
        testTaskConfig.setHost("myHost");
        Map<String, TaskConfig> taskConfigMap = properties.getTasks();

        taskConfigMap.put("class-myClass", testTaskConfig);

        when(authenticatorFactory.getAuthenticator(anyString())).thenReturn(authenticator);
        resourceBundleMessageSource.setBasenames("resources/isy-task/nachrichten/ereignisse", "resources/isy-task/nachrichten/hinweise");

        joinPoint = mock(ProceedingJoinPoint.class);
        JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart.class);
        when(joinPoint.getStaticPart()).thenReturn(staticPart);
        Signature signature = mock(Signature.class);
        when(hostHandler.isHostApplicable(anyString())).thenReturn(true);
        when(staticPart.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn(Class.class);
        when(signature.getName()).thenReturn("myClass");
    }

    @Test
    public void testInvokeAndMonitorTask_Success() throws Throwable {
        // Prepare

        // Act
        isyTaskAspect.invokeAndMonitorTask(joinPoint);

        // Verify
        verify(joinPoint).proceed();
        verify(authenticator).logout();
    }

    @Test
    public void testInvokeAndMonitorTask_noTaskConfigUseDefault() throws Throwable {
        // Prepare
        properties.getTasks().clear();

        // Act
        isyTaskAspect.invokeAndMonitorTask(joinPoint);

        // Verify
        verify(hostHandler).isHostApplicable(properties.getDefault().getHost());
        verify(joinPoint).proceed();
    }

    @Test
    public void testInvokeAndMonitorTask_hostnameInvalidRegex() throws Throwable {
        // Prepare
        testTaskConfig.setHost("(");

        // Act
        TaskKonfigurationInvalidException taskKonfigurationInvalidException =
                assertThrows(TaskKonfigurationInvalidException.class,
                        () -> isyTaskAspect.invokeAndMonitorTask(joinPoint));

        assertEquals("ISYTA00003", taskKonfigurationInvalidException.getAusnahmeId());
        assertEquals("Task-Konfiguration für Task class-myClass ungültig: Hostname ist keine gültige Regex.", taskKonfigurationInvalidException.getFehlertext());
    }

    @Test
    public void testInvokeAndMonitorTask_hostnameNullUseDefault() throws Throwable {
        // Prepare
        testTaskConfig.setHost(null);

        // Act
        isyTaskAspect.invokeAndMonitorTask(joinPoint);

        // Verify
        verify(hostHandler).isHostApplicable(properties.getDefault().getHost());
        verify(joinPoint).proceed();
    }

    @Test
    public void testInvokeAndMonitorTask_hostnameNotApplicable() throws Throwable {
        // Prepare
        when(hostHandler.isHostApplicable(anyString())).thenReturn(false);

        // Act
        isyTaskAspect.invokeAndMonitorTask(joinPoint);

        // Verify
        assertNull(isyTaskAspect.invokeAndMonitorTask(joinPoint));
    }

    @Test
    public void testInvokeAndMonitorTask_taskIsDeactivated() throws Throwable {
        // Prepare
        testTaskConfig.setDeaktiviert(true);

        // Act
        isyTaskAspect.invokeAndMonitorTask(joinPoint);

        // Verify
        assertNull(isyTaskAspect.invokeAndMonitorTask(joinPoint));
    }

    @Test
    public void testInvokeAndMonitorTask_authenticatorNull() throws Throwable {
        // Prepare
        when(authenticatorFactory.getAuthenticator(anyString())).thenReturn(null);

        // Act
        RuntimeException runtimeException =
                assertThrows(RuntimeException.class,
                        () -> isyTaskAspect.invokeAndMonitorTask(joinPoint));

        assertEquals("Authenticator for task class-myClass is null", runtimeException.getMessage());
    }

}
