package de.bund.bva.isyfact.batchrahmen.batch.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;

import org.junit.jupiter.api.Test;
import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

public class BatchAusfuehrungsExceptionTest {

    @Test
    public void testDefaultConstructor() {
        BatchAusfuehrungsException exception = new BatchAusfuehrungsException("testId", "Test message");

        assertEquals("testId", exception.getAusnahmeId());
        assertEquals("Test message", exception.getMessage());
        assertNull(exception.getCause());
        assertNull(exception.getReturnCode());
    }

    @Test
    public void testConstructorWithCause() {
        Exception cause = new RuntimeException("Cause message");
        BatchAusfuehrungsException exception = new BatchAusfuehrungsException("testId", cause);

        assertEquals("testId", exception.getAusnahmeId());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getReturnCode());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        Exception cause = new RuntimeException("Cause message");
        BatchAusfuehrungsException exception = new BatchAusfuehrungsException("testId", "Test message", cause);

        assertEquals("testId", exception.getAusnahmeId());
        assertEquals("Test message", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getReturnCode());
    }

    @Test
    public void testConstructorWithBaseException() {
        BaseException mockBaseException = mock(BaseException.class);
        when(mockBaseException.getAusnahmeId()).thenReturn("testId");
        when(mockBaseException.getMessage()).thenReturn("Test message");

        BatchAusfuehrungsException exception = new BatchAusfuehrungsException(mockBaseException);

        assertEquals("testId", exception.getAusnahmeId());
        assertEquals("Test message", exception.getMessage());
    }

    @Test
    public void testConstructorWithTechnicalRuntimeException() {
        TechnicalRuntimeException technicalRuntimeExceptionMock =  mock(TechnicalRuntimeException.class);
        when(technicalRuntimeExceptionMock.getAusnahmeId()).thenReturn("testId");
        when(technicalRuntimeExceptionMock.getMessage()).thenReturn("Test message");

        BatchAusfuehrungsException exception = new BatchAusfuehrungsException(technicalRuntimeExceptionMock);

        assertEquals("testId", exception.getAusnahmeId());
        assertEquals("Test message", exception.getMessage());
        assertEquals(technicalRuntimeExceptionMock, exception.getCause());
        assertNull(exception.getReturnCode());
    }

    @Test
    public void testConstructorWithReturnCode() {
        BatchReturnCode returnCode = BatchReturnCode.FEHLER_ABBRUCH;
        BatchAusfuehrungsException exception = new BatchAusfuehrungsException("testId", "Test message", returnCode);

        assertEquals("testId", exception.getAusnahmeId());
        assertEquals("Test message", exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(returnCode, exception.getReturnCode());
    }

    @Test
    public void testConstructorWithAusnahmeCauseAndReturnCode() {
        Exception cause = new RuntimeException("Cause message");
        BatchReturnCode returnCode = BatchReturnCode.FEHLER_ABBRUCH;
        BatchAusfuehrungsException exception = new BatchAusfuehrungsException("testId", cause, returnCode);

        assertEquals("testId", exception.getAusnahmeId());
        assertEquals(cause, exception.getCause());
        assertEquals(returnCode, exception.getReturnCode());
    }

    @Test
    public void testConstructorWithBaseCauseAndReturnCode() {
        BaseException mockBaseException = mock(BaseException.class);
        BatchReturnCode returnCode = BatchReturnCode.FEHLER_ABBRUCH;
        FehlertextProvider mockFehlertextProvider = mock(FehlertextProvider.class);

        when(mockFehlertextProvider.getMessage("testId")).thenReturn("Test message");
        when(mockBaseException.getAusnahmeId()).thenReturn("testId");
        when(mockBaseException.getMessage()).thenReturn("Test message");

        BatchAusfuehrungsException exception = new BatchAusfuehrungsException(mockBaseException, returnCode);

        assertEquals("testId", exception.getAusnahmeId());
        assertEquals("Test message", exception.getMessage());
        assertEquals(returnCode, exception.getReturnCode());
    }

    @Test
    public void testConstructorWithCauseAndReturnCode() {
        TechnicalRuntimeException mockTechnicalRuntimeException = mock(TechnicalRuntimeException.class);
        BatchReturnCode returnCode = BatchReturnCode.FEHLER_ABBRUCH;
        FehlertextProvider mockFehlertextProvider = mock(FehlertextProvider.class);

        when(mockFehlertextProvider.getMessage("testId")).thenReturn("Test message");
        when(mockTechnicalRuntimeException.getAusnahmeId()).thenReturn("testId");
        when(mockTechnicalRuntimeException.getMessage()).thenReturn("Test message");

        BatchAusfuehrungsException exception = new BatchAusfuehrungsException(mockTechnicalRuntimeException, returnCode);

        assertEquals("testId", exception.getAusnahmeId());
        assertEquals("Test message", exception.getMessage());
    }

    @Test
    public void testConstructorWithCauseAndReturnCodeThrowable() {
        String ausnahmeId = "testId";
        String msg = "Test message";
        Throwable cause = new RuntimeException("Test cause");
        BatchReturnCode returnCode = BatchReturnCode.FEHLER_ABBRUCH;

        BatchAusfuehrungsException exception = new BatchAusfuehrungsException(ausnahmeId, msg, cause, returnCode);

        assertEquals(ausnahmeId, exception.getAusnahmeId());
        assertEquals(msg, exception.getMessage());
    }
}


