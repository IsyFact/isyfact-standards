package de.bund.bva.pliscommon.exception.service.util;

import static org.junit.Assert.*;

import de.bund.bva.pliscommon.exception.service.bridge.util.BridgeExceptionMapper;
import org.junit.Test;

import de.bund.bva.pliscommon.exception.PlisBusinessException;
import de.bund.bva.pliscommon.exception.PlisTechnicalException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;
import de.bund.bva.pliscommon.exception.service.util.factory.PlisTestExceptionFactory;
import de.bund.bva.isyfact.exception.service.BusinessToException;
import de.bund.bva.isyfact.exception.service.TechnicalToException;

public class BridgeExceptionMapperTest {

    @Test
    public void testMapException() {
        PlisBusinessException exc = PlisTestExceptionFactory.getBusinessException();
        BusinessToException toExc =
                BridgeExceptionMapper.mapException(exc, PlisTestExceptionFactory.MyIsyBusinessToException.class);
        assertNotNull(toExc);
        assertEquals(PlisTestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException2() {
        PlisBusinessException exc = PlisTestExceptionFactory.getBusinessException();
        BusinessToException toExc = BridgeExceptionMapper.mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException3() {
        PlisTechnicalException exc = PlisTestExceptionFactory.getTechnicalException();
        TechnicalToException toExc =
                BridgeExceptionMapper.mapException(exc, PlisTestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testMapException4() {
        PlisTechnicalRuntimeException exc = PlisTestExceptionFactory.getTechnicalRuntimeException();
        PlisTestExceptionFactory.MyIsyTechnicalToException toExc =
                BridgeExceptionMapper.mapException(exc, PlisTestExceptionFactory.MyIsyTechnicalToException.class);
        assertNotNull(toExc);
        assertEquals(PlisTestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException5() {
        PlisTechnicalRuntimeException exc = PlisTestExceptionFactory.getTechnicalRuntimeException();
        PlisTestExceptionFactory.MyIsyTechnicalToException toExc = BridgeExceptionMapper
            .mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException6() {
        PlisTechnicalRuntimeException exc = PlisTestExceptionFactory.getTechnicalRuntimeException();
        PlisTestExceptionFactory.MyWrongConstructorToException toExc =
                BridgeExceptionMapper.mapException(exc, PlisTestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testCreateException() {
        PlisTestExceptionFactory.MyIsyBusinessToException toExc = BridgeExceptionMapper
                .createToException(PlisTestExceptionFactory.ausnahmeId, PlisTestExceptionFactory.provider,
                        PlisTestExceptionFactory.MyIsyBusinessToException.class, PlisTestExceptionFactory.parameter);
        assertNotNull(toExc);
        assertEquals(PlisTestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException1() {
        PlisTestExceptionFactory.MyIsyBusinessToException toExc = BridgeExceptionMapper
                .createToException(PlisTestExceptionFactory.ausnahmeId, PlisTestExceptionFactory.provider, null,
                        PlisTestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException2() {
        PlisTestExceptionFactory.MyWrongConstructorToException toExc = BridgeExceptionMapper
                .createToException(PlisTestExceptionFactory.ausnahmeId, PlisTestExceptionFactory.provider,
                        PlisTestExceptionFactory.MyWrongConstructorToException.class, PlisTestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException3() {
        PlisTestExceptionFactory.MyWrongParameterToException toExc = BridgeExceptionMapper
                .createToException("foo bar", PlisTestExceptionFactory.provider,
                        PlisTestExceptionFactory.MyWrongParameterToException.class, PlisTestExceptionFactory.parameter);
    }


}
