package de.bund.bva.pliscommon.bridges.integration;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.util.ServiceApiMapper;
import de.bund.bva.pliscommon.bridges.integration.config.TestClientConfiguration;
import de.bund.bva.pliscommon.bridges.integration.config.TestServiceConfiguration;
import de.bund.bva.pliscommon.bridges.integration.sst.PlisTestRemoteBean;
import de.bund.bva.pliscommon.bridges.integration.sst.PlisTestToException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestClientConfiguration.class, TestServiceConfiguration.class }, properties = {
    "isy.logging.autoconfiguration.enabled=false",
    "server.port=8088" }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationsTest {

    @Autowired
    private AufrufKontextVerwalter<AufrufKontext> isyAufrufKontextVerwalter;

    @Autowired
    private ServiceApiMapper mapper;

    @Autowired
    @Qualifier("invoker")
    private PlisTestRemoteBean remoteBean;

    @Test(expected = PlisTestToException.class)
    public void testRemoteBeanWirftException() throws Exception {
        remoteBean.ping(mapper.map(holeAufrufKontext()), true);
    }

    @Test(expected = IsyWrapperException.class)
    public void testWrappePlisTOExceptionInIsyException() throws Exception {
        try {
            remoteBean.ping(mapper.map(holeAufrufKontext()), true);
        } catch (PlisTestToException e) {
            throw new IsyWrapperException();
        }
    }

    /**
     * Der Service gibt aus dem übermittelten AufrufKontextTo den SachbearbeiterName zurück
     * Testet, ob der korrekte Name wieder beim Client ankommt.
     * @throws Exception
     */
    @Test
    public void testRemoteBeanPingBack() throws Exception {
        String user = "testuser123";
        AufrufKontextTo aufrufKontextTo = holeAufrufKontext();
        aufrufKontextTo.setDurchfuehrenderSachbearbeiterName(user);
        assertEquals(user, remoteBean.ping(mapper.map(aufrufKontextTo), false));
    }



    /*
     * Analog zu BehoerdenverzeichnisHttpInvokerWrapper übernommen
     */
    private AufrufKontextTo holeAufrufKontext() {
        AufrufKontext aufrufKontext = this.isyAufrufKontextVerwalter.getAufrufKontext();
        return mappeAufrufKontext2AufrufKontextTo(aufrufKontext);
    }

    /*
     * Analog zu BehoerdenverzeichnisHttpInvokerWrapper übernommen
     */
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

    //Exception aus dem Isyfact 2 Namespace
    public static class IsyWrapperException extends BusinessException {
        public IsyWrapperException() {
            super("id", (_param1, _param2) -> "fehlertext");
        }
    }

}
