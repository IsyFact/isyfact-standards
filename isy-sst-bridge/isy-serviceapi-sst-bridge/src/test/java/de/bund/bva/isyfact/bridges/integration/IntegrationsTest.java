package de.bund.bva.isyfact.bridges.integration;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

import de.bund.bva.isyfact.bridges.integration.server.IsyTestServer;
import de.bund.bva.isyfact.bridges.integration.sst.IsyTestRemoteBean;
import de.bund.bva.isyfact.bridges.integration.sst.IsyTestToException;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextFactoryImpl;
import de.bund.bva.pliscommon.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.pliscommon.exception.PlisBusinessException;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.bridge.util.ServiceApiMapper;

public class IntegrationsTest {
    private IsyTestServer isyTestServer;
    private String serviceUrl;

    private static AufrufKontextVerwalter<AufrufKontext> plisAufrufKontextVerwalter;
    private static ServiceApiMapper mapper;

    @BeforeClass
    public static void staticSetUp() {
        AufrufKontextVerwalterStub<AufrufKontext> verwalternStub = new AufrufKontextVerwalterStub<>();
        verwalternStub.setAufrufKontextFactory(new AufrufKontextFactoryImpl<>());
        verwalternStub.setFesterAufrufKontext(true);
        plisAufrufKontextVerwalter = verwalternStub;

        mapper = new ServiceApiMapper();
    }

    @Before
    public void setUp() throws Exception {
        isyTestServer = new IsyTestServer();
        serviceUrl = isyTestServer.getServiceUrl();
    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(1000);
        isyTestServer.stop();
    }

    public IsyTestRemoteBean createHttpInvokerProxy(SimpleHttpInvokerRequestExecutor simpleHttpInvokerRequestExecutor) {
        HttpInvokerProxyFactoryBean proxyFactory = new HttpInvokerProxyFactoryBean();
        proxyFactory.setHttpInvokerRequestExecutor(simpleHttpInvokerRequestExecutor);
        proxyFactory.setServiceUrl(serviceUrl);
        proxyFactory.setServiceInterface(IsyTestRemoteBean.class);
        proxyFactory.afterPropertiesSet();
        return (IsyTestRemoteBean) proxyFactory.getObject();
    }

    @Test(expected = IsyTestToException.class)
    public void testRemoteBeanWirftException() throws Exception {
        IsyTestRemoteBean isyTestRemoteBean = createHttpInvokerProxy(new SimpleHttpInvokerRequestExecutor());
        isyTestRemoteBean.ping(mapper.map(holeAufrufKontext()), true);
    }

    @Test(expected = PlisWrapperException.class)
    public void testWrappePlisTOExceptionInIsyException() throws Exception {
        IsyTestRemoteBean isyTestRemoteBean = createHttpInvokerProxy(new SimpleHttpInvokerRequestExecutor());
        try {
            isyTestRemoteBean.ping(mapper.map(holeAufrufKontext()), true);
        } catch (IsyTestToException e) {
            throw new PlisWrapperException();
        }
    }

    /**
     * Der Service gibt aus dem übermittelten AufrufKontextTo den SachbearbeiterName zurück
     * Testet, ob der korrekte Name wieder beim Client ankommt.
     * @throws Exception
     */
    @Test
    public void testRemoteBeanPingBack() throws Exception {
        IsyTestRemoteBean isyTestRemoteBean = createHttpInvokerProxy(new SimpleHttpInvokerRequestExecutor());
        String user = "testuser123";
        AufrufKontextTo aufrufKontextTo = holeAufrufKontext();
        aufrufKontextTo.setDurchfuehrenderSachbearbeiterName(user);
        assertEquals(user, isyTestRemoteBean.ping(mapper.map(aufrufKontextTo), false));
    }

    private AufrufKontextTo holeAufrufKontext() {
        AufrufKontext aufrufKontext = plisAufrufKontextVerwalter.getAufrufKontext();
        return mappeAufrufKontext2AufrufKontextTo(aufrufKontext);
    }

    private AufrufKontextTo mappeAufrufKontext2AufrufKontextTo(AufrufKontext aufrufKontext) {
        AufrufKontextTo aufrufKontextTo = new AufrufKontextTo();

        aufrufKontextTo.setDurchfuehrendeBehoerde(aufrufKontext.getDurchfuehrendeBehoerde());
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung(aufrufKontext.getDurchfuehrenderBenutzerKennung());
        aufrufKontextTo
                .setDurchfuehrenderBenutzerPasswort(aufrufKontext.getDurchfuehrenderBenutzerPasswort());
        aufrufKontextTo
                .setDurchfuehrenderSachbearbeiterName(aufrufKontext.getDurchfuehrenderSachbearbeiterName());
        aufrufKontextTo.setKorrelationsId(aufrufKontext.getKorrelationsId());
        aufrufKontextTo.setRolle(aufrufKontext.getRolle());
        aufrufKontextTo.setRollenErmittelt(aufrufKontext.isRollenErmittelt());

        return aufrufKontextTo;
    }

    public static class PlisWrapperException extends PlisBusinessException {
        public PlisWrapperException() {
            super("id", (_param1, _param2) -> "fehlertext");
        }
    }
}
