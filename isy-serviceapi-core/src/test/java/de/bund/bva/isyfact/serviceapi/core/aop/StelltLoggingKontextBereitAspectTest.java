package de.bund.bva.isyfact.serviceapi.core.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.serviceapi.core.aop.test.LoggingKontextAspectService;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.DefaultAufrufKontextToResolver;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Tests that the {@link StelltLoggingKontextBereitInterceptor} sets a KorrelationsID (AufrufKontextTO)
 * for Classes without Annotations as well.
 */
public class StelltLoggingKontextBereitAspectTest {

    private LoggingKontextAspectService service;

    @Before
    public void setUp() {
        // cleanUp
        MdcHelper.entferneKorrelationsIds();

        AufrufKontextToResolver aufrufKontextToResolver = new DefaultAufrufKontextToResolver();
        StelltLoggingKontextBereitInterceptor aspect =
            new StelltLoggingKontextBereitInterceptor(aufrufKontextToResolver);
        LoggingKontextAspectService service = new LoggingKontextAspectService();
        ProxyFactory proxyFactory = new ProxyFactory(service);
        proxyFactory.addAdvice(aspect);
        this.service = (LoggingKontextAspectService) proxyFactory.getProxy();
    }

    @Test
    public void testAufrufOhneParameter() {
        //just check state before for consistency
        assertNull(MdcHelper.liesKorrelationsId());
        assertNull(service.getKorrelationsIDLetzterAufruf());

        //make call
        service.aufrufOhneParameter();

        //assert MDC was correct during call
        assertNotNull(service.getKorrelationsIDLetzterAufruf());
        //assert MDC state after call
        assertNull(MdcHelper.liesKorrelationsId());
    }

    @Test
    public void testAufrufOhneAufrufKontext() {
        //just check state before for consistency
        assertNull(MdcHelper.liesKorrelationsId());
        assertNull(service.getKorrelationsIDLetzterAufruf());

        //make call
        service.aufrufOhneAufrufKontext(10);

        //assert MDC was correct during call
        assertNotNull(service.getKorrelationsIDLetzterAufruf());
        //assert MDC state after call
        assertNull(MdcHelper.liesKorrelationsId());
    }

    @Test
    public void testAufrufOhneKorrelationsId() {
        //just check state before for consistency
        assertNull(MdcHelper.liesKorrelationsId());
        assertNull(service.getKorrelationsIDLetzterAufruf());

        //call
        AufrufKontextTo to = new AufrufKontextTo();
        service.aufrufMitAufrufKontext(to);

        //assert MDC was correct during call
        assertNotNull(service.getKorrelationsIDLetzterAufruf());
        //assert MDC state after call
        assertNull(MdcHelper.liesKorrelationsId());

        // assert that generated correlation id was added to AufrufKontextTo
        assertEquals(service.getKorrelationsIDLetzterAufruf(), to.getKorrelationsId());
    }

    @Test
    public void testAufrufLeereKorrelationsId() {
        //just check state before for consistency
        assertNull(MdcHelper.liesKorrelationsId());
        assertNull(service.getKorrelationsIDLetzterAufruf());

        //call
        AufrufKontextTo to = new AufrufKontextTo();
        to.setKorrelationsId("");
        service.aufrufMitAufrufKontext(to);

        //assert MDC was correct during call
        String korrId = service.getKorrelationsIDLetzterAufruf();
        assertNotNull(korrId);
        assertFalse(korrId.isEmpty());
        //assert MDC state after call
        assertNull(MdcHelper.liesKorrelationsId());

        // assert that generated correlation id was added to AufrufKontextTo
        assertEquals(service.getKorrelationsIDLetzterAufruf(), to.getKorrelationsId());
    }

    @Test
    public void testAufrufMitKorrelationsId() {

        //just check state before for consistency
        assertNull(MdcHelper.liesKorrelationsId());
        assertNull(service.getKorrelationsIDLetzterAufruf());

        //prepare call
        AufrufKontextTo to = new AufrufKontextTo();
        UUID korrId = UUID.randomUUID();
        to.setKorrelationsId(korrId.toString());
        //make call
        service.aufrufMitAufrufKontext(to);

        //assert MDC was correct during call (from AufrufKontext)
        assertEquals(korrId.toString(), service.getKorrelationsIDLetzterAufruf());
        //assert MDC state after call
        assertNull(MdcHelper.liesKorrelationsId());
    }

    @Test
    public void testAufrufMitException() {
        //just check state before for consistency
        assertNull(MdcHelper.liesKorrelationsId());
        assertNull(service.getKorrelationsIDLetzterAufruf());

        //make call
        try {
            service.aufrufMitException();
            fail(); //exception should still be thrown
        } catch (Exception ignored) {
        }

        //assert MDC was correct during call (from AufrufKontext)
        assertNotNull(service.getKorrelationsIDLetzterAufruf());
        //assert MDC state after call
        assertNull(MdcHelper.liesKorrelationsId());
    }

}
