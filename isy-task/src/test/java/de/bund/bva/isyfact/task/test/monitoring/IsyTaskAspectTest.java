package de.bund.bva.isyfact.task.test.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.support.ResourceBundleMessageSource;

import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties.TaskConfig;
import de.bund.bva.isyfact.task.exception.TaskKonfigurationInvalidException;
import de.bund.bva.isyfact.task.konfiguration.HostHandler;
import de.bund.bva.isyfact.task.monitoring.IsyTaskAspect;
import de.bund.bva.isyfact.task.security.Authenticator;
import de.bund.bva.isyfact.task.security.AuthenticatorFactory;
import de.bund.bva.isyfact.task.util.TaskId;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@RunWith(MockitoJUnitRunner.class)
public class IsyTaskAspectTest {
    @Mock
    ProceedingJoinPoint joinPoint;

    @Mock
    AuthenticatorFactory authenticatorFactory;

    @Mock
    Authenticator authenticator;

    @Mock
    HostHandler hostHandler;

    @InjectMocks
    IsyTaskAspect isyTaskAspect;

    TaskId taskid;

    @Mock
    Counter counter;

    @Spy
    IsyTaskConfigurationProperties properties = new IsyTaskConfigurationProperties();

    @Spy
    MeterRegistry meterRegistry = new SimpleMeterRegistry();

    @Spy
    ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();

    TaskConfig testTaskConfig;

    @Before
    public void prepare() throws Exception {
        testTaskConfig = new TaskConfig();
        testTaskConfig.setHost("myHost");
        Map<String, TaskConfig> taskConfigMap = properties.getTasks();

        taskConfigMap.put("class-myClass", testTaskConfig);

        doReturn(authenticator).when(authenticatorFactory).getAuthenticator(anyString());
        resourceBundleMessageSource.setBasenames("resources/isy-task/nachrichten/ereignisse", "resources/isy-task/nachrichten/hinweise");
        joinPoint = mock(ProceedingJoinPoint.class);
        JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart.class);
        doReturn(staticPart).when(joinPoint).getStaticPart();
        Signature signature = mock(Signature.class);
        doReturn(true).when(hostHandler).isHostApplicable(anyString());
        doReturn(signature).when(staticPart).getSignature();
        doReturn(Class.class).when(signature).getDeclaringType();
        doReturn("myClass").when(signature).getName();

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
    public void testInvokeAndMonitorTask_noTaskConfig() throws Throwable {

        // Prepare
        properties.getTasks().clear();

        // Act
        TaskKonfigurationInvalidException taskKonfigurationInvalidException =
                assertThrows(TaskKonfigurationInvalidException.class,
                        () -> isyTaskAspect.invokeAndMonitorTask(joinPoint));

        assertEquals("ISYTA00003", taskKonfigurationInvalidException.getAusnahmeId());
        assertEquals("Task-Konfiguration für Task class-myClass ungültig: Keine Taskkonfiguration vorhanden.", taskKonfigurationInvalidException.getFehlertext());
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

        // Verify)
        verify(hostHandler).isHostApplicable(properties.getDefault().getHost());
        verify(joinPoint).proceed();
    }

    @Test
    public void testInvokeAndMonitorTask_hostnameNotApplicable() throws Throwable {

        // Prepare
        doReturn(false).when(hostHandler).isHostApplicable(anyString());

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
        doReturn(null).when(authenticatorFactory).getAuthenticator(anyString());

        // Act
        RuntimeException runtimeException =
                assertThrows(RuntimeException.class,
                        () -> isyTaskAspect.invokeAndMonitorTask(joinPoint));

        assertEquals("Authenticator for task class-myClass is null", runtimeException.getMessage());

    }

}

