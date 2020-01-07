package de.bund.bva.isyfact.exception.service.util;

import static org.junit.Assert.*;

import de.bund.bva.isyfact.exception.service.bridge.util.BridgeExceptionMapper;
import org.junit.Test;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.exception.service.util.factory.IsyTestExceptionFactory;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;

public class BridgeExceptionMapperTest {

    @Test
    public void testMapException() {
        BusinessException exc = IsyTestExceptionFactory.getBusinessException();
        PlisBusinessToException toExc =
                BridgeExceptionMapper.mapException(exc, IsyTestExceptionFactory.MyPlisBusinessToException.class);
        assertNotNull(toExc);
        assertEquals(IsyTestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException2() {
        BusinessException exc = IsyTestExceptionFactory.getBusinessException();
        PlisBusinessToException toExc = BridgeExceptionMapper.mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException3() {
        TechnicalException exc = IsyTestExceptionFactory.getTechnicalException();
        PlisTechnicalToException toExc =
                BridgeExceptionMapper.mapException(exc, IsyTestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testMapException4() {
        TechnicalRuntimeException exc = IsyTestExceptionFactory.getTechnicalRuntimeException();
        IsyTestExceptionFactory.MyPlisTechnicalToException toExc =
                BridgeExceptionMapper.mapException(exc, IsyTestExceptionFactory.MyPlisTechnicalToException.class);
        assertNotNull(toExc);
        assertEquals(IsyTestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException5() {
        TechnicalRuntimeException exc = IsyTestExceptionFactory.getTechnicalRuntimeException();
        IsyTestExceptionFactory.MyPlisTechnicalToException toExc = BridgeExceptionMapper
            .mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException6() {
        TechnicalRuntimeException exc = IsyTestExceptionFactory.getTechnicalRuntimeException();
        IsyTestExceptionFactory.MyWrongConstructorToException toExc =
                BridgeExceptionMapper.mapException(exc, IsyTestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testCreateException() {
        IsyTestExceptionFactory.MyPlisBusinessToException toExc = BridgeExceptionMapper
                .createToException(IsyTestExceptionFactory.ausnahmeId, IsyTestExceptionFactory.provider,
                        IsyTestExceptionFactory.MyPlisBusinessToException.class, IsyTestExceptionFactory.parameter);
        assertNotNull(toExc);
        assertEquals(IsyTestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException1() {
        IsyTestExceptionFactory.MyPlisBusinessToException toExc = BridgeExceptionMapper
                .createToException(IsyTestExceptionFactory.ausnahmeId, IsyTestExceptionFactory.provider, null,
                        IsyTestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException2() {
        IsyTestExceptionFactory.MyWrongConstructorToException toExc = BridgeExceptionMapper
                .createToException(IsyTestExceptionFactory.ausnahmeId, IsyTestExceptionFactory.provider,
                        IsyTestExceptionFactory.MyWrongConstructorToException.class, IsyTestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException3() {
        IsyTestExceptionFactory.MyWrongParameterToException toExc = BridgeExceptionMapper
                .createToException("foo bar", IsyTestExceptionFactory.provider,
                        IsyTestExceptionFactory.MyWrongParameterToException.class, IsyTestExceptionFactory.parameter);
    }


}
