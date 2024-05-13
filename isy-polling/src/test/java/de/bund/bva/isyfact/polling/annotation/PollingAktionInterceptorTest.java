package de.bund.bva.isyfact.polling.annotation;

import de.bund.bva.isyfact.polling.PollingVerwalter;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PollingAktionInterceptorTest {

    @Mock
    private PollingVerwalter pollingVerwalter;

    @Mock
    private MethodInvocation methodInvocation;

    @InjectMocks
    private PollingAktionInterceptor interceptor;

    private static final String POLLING_CLUSTER = "testCluster";

    @BeforeEach
    public void setup() throws Throwable {
        lenient().when(methodInvocation.proceed()).thenReturn(null);
    }


    @Test
    public void testInvokeWithNullTarget() throws Throwable {
        Method method = MyClass.class.getMethod("myMethod");
        when(methodInvocation.getMethod()).thenReturn(method);
        when(methodInvocation.getThis()).thenReturn(null);

        assertDoesNotThrow(() -> interceptor.invoke(methodInvocation));
        verify(pollingVerwalter, never()).aktualisiereZeitpunktLetztePollingAktivitaet(anyString());
    }


    @Test
    public void testInvokeWithPollingAktion() throws Throwable {
        Method method = MyClass.class.getMethod("myMethod");
        when(methodInvocation.getMethod()).thenReturn(method);
        when(methodInvocation.getThis()).thenReturn(new MyClass());

        interceptor.invoke(methodInvocation);

        verify(pollingVerwalter).aktualisiereZeitpunktLetztePollingAktivitaet("testCluster");
    }


    @Test
    public void testInvokeWithoutPollingAktion() throws Throwable {
        Method method = MyClass.class.getMethod("myMethodWithoutAnnotation");
        when(methodInvocation.getMethod()).thenReturn(method);

        interceptor.invoke(methodInvocation);

        verify(pollingVerwalter, never()).aktualisiereZeitpunktLetztePollingAktivitaet(anyString());
    }

    @Test
    public void testInvokeWithExceptionInProceed() throws Throwable {
        Method method = MyClass.class.getMethod("myMethod");
        when(methodInvocation.getMethod()).thenReturn(method);
        when(methodInvocation.getThis()).thenReturn(new MyClass());
        doThrow(new RuntimeException("Test exception")).when(methodInvocation).proceed();

        assertThrows(RuntimeException.class, () -> interceptor.invoke(methodInvocation));

        verify(pollingVerwalter, never()).aktualisiereZeitpunktLetztePollingAktivitaet(anyString());
    }


    @Test
    public void testReturnValues() throws Throwable {
        Method method = MyClass.class.getMethod("methodWithReturnValue");
        when(methodInvocation.getMethod()).thenReturn(method);
        when(methodInvocation.getThis()).thenReturn(new MyClass());
        when(methodInvocation.proceed()).thenReturn("Expected Return");

        Object result = interceptor.invoke(methodInvocation);

        Assertions.assertEquals("Expected Return", result, "The interceptor should return the value from proceed()");
    }




    static class MyClass {
        @PollingAktion(pollingCluster = POLLING_CLUSTER)
        public void myMethod() {
        }

        public void myMethodWithoutAnnotation() {
        }

        public String methodWithReturnValue() {
            return "Expected Return";
        }

    }
}
