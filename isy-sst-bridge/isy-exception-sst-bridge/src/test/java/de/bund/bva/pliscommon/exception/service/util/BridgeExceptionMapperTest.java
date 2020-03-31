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
    public void testMapExceptionZuNull() {
        PlisBusinessException exc = PlisTestExceptionFactory.getBusinessException();
        BusinessToException toExc = BridgeExceptionMapper.mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapExceptionZuUngueltigerKonstruktor() {
        PlisTechnicalException exc = PlisTestExceptionFactory.getTechnicalException();
        TechnicalToException toExc =
                BridgeExceptionMapper.mapException(exc, PlisTestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testMapRuntimeException() {
        PlisTechnicalRuntimeException exc = PlisTestExceptionFactory.getTechnicalRuntimeException();
        PlisTestExceptionFactory.MyIsyTechnicalToException toExc =
                BridgeExceptionMapper.mapException(exc, PlisTestExceptionFactory.MyIsyTechnicalToException.class);
        assertNotNull(toExc);
        assertEquals(PlisTestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapRuntimeExceptionZuNull() {
        PlisTechnicalRuntimeException exc = PlisTestExceptionFactory.getTechnicalRuntimeException();
        PlisTestExceptionFactory.MyIsyTechnicalToException toExc = BridgeExceptionMapper
            .mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapRuntimeExceptionZuUngueltigerKonstruktor() {
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
    public void testCreateNullException() {
        PlisTestExceptionFactory.MyIsyBusinessToException toExc = BridgeExceptionMapper
                .createToException(PlisTestExceptionFactory.ausnahmeId, PlisTestExceptionFactory.provider, null,
                        PlisTestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUngueltigerKonstruktorException() {
        PlisTestExceptionFactory.MyWrongConstructorToException toExc = BridgeExceptionMapper
                .createToException(PlisTestExceptionFactory.ausnahmeId, PlisTestExceptionFactory.provider,
                        PlisTestExceptionFactory.MyWrongConstructorToException.class, PlisTestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUngueltigeExceptionInitialisierung() {
        PlisTestExceptionFactory.MyWrongParameterToException toExc = BridgeExceptionMapper
                .createToException("foo bar", PlisTestExceptionFactory.provider,
                        PlisTestExceptionFactory.MyWrongParameterToException.class, PlisTestExceptionFactory.parameter);
    }


}
