package de.bund.bva.isyfact.ueberwachung.metrics.impl;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.AccessibleObject;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.ueberwachung.common.data.TestBusinessException;
import de.bund.bva.isyfact.ueberwachung.common.data.TestTechnicalException;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;


/**
 * Tests for recognition and handling of exceptions in {@link DefaultServiceStatistik}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"isy.logging.anwendung.name=Test",
                "isy.logging.anwendung.typ=Test",
                "isy.logging.anwendung.version=0.1"})
public class TestDefaultServiceStatistikExceptionHandling {

    private DefaultServiceStatistik serviceStatistik;

    @Autowired
    private MeterRegistry meterRegistry;

    private Gauge anzahlBusinessExceptions;
    private Gauge anzahlTechnicalExceptions;

    @PostConstruct
    public void postConstruct() {
        anzahlBusinessExceptions = meterRegistry.get("anzahlBusinessExceptions").gauge();
        anzahlTechnicalExceptions = meterRegistry.get("anzahlTechnicalExceptions").gauge();
    }

    /**
     * Setup method.
     */
    @Before
    public void setUp() {
        serviceStatistik = new DefaultServiceStatistik();
    }

    /**
     * Tests successful call of default {@link DefaultServiceStatistik#invoke}.
     */
    @Test
    public void testInvokeSuccessful() throws Throwable {
        // Given
        MethodInvocation invocation = new MethodInvocationImpl("expectedResult");

        // When
        Object result = serviceStatistik.invoke(invocation);

        // Then
        assertNotNull(result);
        assertEquals(0, anzahlBusinessExceptions.value(), 0.01);
        assertEquals(0, anzahlTechnicalExceptions.value(), 0.01);
    }

    /**
     * Tests {@link DefaultServiceStatistik#invoke}, to verify that a thrown {@link TechnicalException} is recognized
     * as a technically unsuccessful call and increments the value of {@code anzahlTechnicalExceptions}.
     */
    @Test(expected = TestTechnicalException.class)
    public void testInvokeWithTechnicalException() throws Throwable {
        // Given
        MethodInvocation invocation = new MethodInvocationImpl(new TestTechnicalException("Technical error"));

        // When
        serviceStatistik.invoke(invocation);

        // Then
        assertEquals(1, anzahlTechnicalExceptions.value(), 0.01);
        assertEquals(0, anzahlBusinessExceptions.value(), 0.01);
    }

    /**
     * Tests {@link DefaultServiceStatistik#invoke}, to verify that a thrown {@link BusinessException} is recognized
     * as a functionally unsuccessful call and increments the value of {@code anzahlBusinessExceptions}.
     */
    @Test(expected = TestBusinessException.class)
    public void testInvokeWithBusinessException() throws Throwable {
        // Given
        MethodInvocation invocation = new MethodInvocationImpl(new TestBusinessException("Business error"));

        // When
        serviceStatistik.invoke(invocation);

        // Then
        assertEquals(0, anzahlTechnicalExceptions.value(), 0.01);
        assertEquals(1, anzahlBusinessExceptions.value(), 0.01);
    }

    /**
     * Tests {@link DefaultServiceStatistik#invoke} to verify that a thrown {@link RuntimeException}
     * is recognized as a technically and functionally unsuccessful call and increments
     * both {@code anzahlTechnicalExceptions} and {@code anzahlBusinessExceptions}.
     */
    @Test(expected = RuntimeException.class)
    public void testInvokeWithRuntimeException() throws Throwable {
        // Given
        MethodInvocation invocation = new MethodInvocationImpl(new RuntimeException("Technical error"));

        // When
        serviceStatistik.invoke(invocation);

        // Then
        assertEquals(1, anzahlTechnicalExceptions.value(), 0.01);
        assertEquals(1, anzahlBusinessExceptions.value(), 0.01);
    }

    /**
     * Implementation of {@link MethodInvocation}.
     */
    private static class MethodInvocationImpl implements MethodInvocation {
        private final Object result;
        private final Throwable throwable;

        public MethodInvocationImpl(Object result) {
            this.result = result;
            this.throwable = null;
        }

        public MethodInvocationImpl(Throwable throwable) {
            this.result = null;
            this.throwable = throwable;
        }

        @Override
        public Object proceed() throws Throwable {
            if (throwable != null) {
                throw throwable;
            }
            return result;
        }

        @Override
        public Object getThis() {
            return null;
        }

        @Nonnull
        @Override
        public AccessibleObject getStaticPart() {
            return null;
        }

        @Override
        public Object[] getArguments() {
            return new Object[0];
        }

        @Override
        public java.lang.reflect.Method getMethod() {
            return null;
        }
    }
}