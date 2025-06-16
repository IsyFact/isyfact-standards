package de.bund.bva.isyfact.serviceapi.common.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.serviceapi.common.exception.test.TestExceptionFactory;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;

public class ExceptionMapperTest {

    @Test
    public void testMapException() {
        BusinessException exc = TestExceptionFactory.getBusinessException();
        PlisBusinessToException toExc =
            ExceptionMapper.mapException(exc, TestExceptionFactory.MyBusinessToException.class);
        assertNotNull(toExc);
        assertEquals(TestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException2() {
        BusinessException exc = TestExceptionFactory.getBusinessException();
        PlisBusinessToException toExc = ExceptionMapper.mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException3() {
        TechnicalException exc = TestExceptionFactory.getTechnicalException();
        PlisTechnicalToException toExc =
            ExceptionMapper.mapException(exc, TestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testMapException4() {
        TechnicalRuntimeException exc = TestExceptionFactory.getTechnicalRuntimeException();
        TestExceptionFactory.MyTechnicalToException toExc =
            ExceptionMapper.mapException(exc, TestExceptionFactory.MyTechnicalToException.class);
        assertNotNull(toExc);
        assertEquals(TestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException5() {
        TechnicalRuntimeException exc = TestExceptionFactory.getTechnicalRuntimeException();
        TestExceptionFactory.MyTechnicalToException toExc = ExceptionMapper.mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException6() {
        TechnicalRuntimeException exc = TestExceptionFactory.getTechnicalRuntimeException();
        TestExceptionFactory.MyWrongConstructorToException toExc =
            ExceptionMapper.mapException(exc, TestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testCreateException() {
        TestExceptionFactory.MyBusinessToException toExc = ExceptionMapper
            .createToException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider,
                TestExceptionFactory.MyBusinessToException.class, TestExceptionFactory.parameter);
        assertNotNull(toExc);
        assertEquals(TestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException1() {
        TestExceptionFactory.MyBusinessToException toExc = ExceptionMapper
            .createToException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider, null,
                TestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException2() {
        TestExceptionFactory.MyWrongConstructorToException toExc = ExceptionMapper
            .createToException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider,
                TestExceptionFactory.MyWrongConstructorToException.class, TestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException3() {
        TestExceptionFactory.MyWrongParameterToException toExc = ExceptionMapper
            .createToException("foo bar", TestExceptionFactory.provider,
                TestExceptionFactory.MyWrongParameterToException.class, TestExceptionFactory.parameter);
    }

}
