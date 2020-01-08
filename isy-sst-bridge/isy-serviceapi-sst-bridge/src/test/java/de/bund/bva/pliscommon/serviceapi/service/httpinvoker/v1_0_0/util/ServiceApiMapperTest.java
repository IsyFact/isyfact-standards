package de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.util;

import static org.junit.Assert.*;

import java.util.Objects;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.bridge.util.ServiceApiMapper;
import org.junit.Test;

import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.ClientAufrufKontextTo;

public class ServiceApiMapperTest {

    private ServiceApiMapper mapper = new ServiceApiMapper();

    @Test
    public void mapNullAufrufKontextTo() {
        AufrufKontextTo isyAufrufKontextTo = null;
        assertNull(mapper.map(isyAufrufKontextTo));
    }

    @Test
    public void mapNullClientAufrufKontextTo() {
        ClientAufrufKontextTo isyClientAufrufKontextTo = null;
        assertNull(mapper.map(isyClientAufrufKontextTo));
    }

    @Test
    public void mapAufrufKontextTo() {
        AufrufKontextTo isyAufrufKontextTo = getIsyAufrufKontextToStub();
        AufrufKontextTo plisAufrufKontextTo =
                mapper.map(isyAufrufKontextTo);
        assertNotNull(plisAufrufKontextTo);
        assertTrue(areEqual(isyAufrufKontextTo, plisAufrufKontextTo));
    }

    @Test
    public void mapClientAufrufKontextTo() {
        ClientAufrufKontextTo isyClientAufrufKontextTo = getIsyClientAufrufKontextToStub();
        ClientAufrufKontextTo plisClientAufrufKontextTo =
                mapper.map(isyClientAufrufKontextTo);
        assertNotNull(plisClientAufrufKontextTo);
        assertTrue(areEqual(isyClientAufrufKontextTo, plisClientAufrufKontextTo));
    }

    private AufrufKontextTo getIsyAufrufKontextToStub() {
        AufrufKontextTo aufrufKontextTo = new AufrufKontextTo();
        aufrufKontextTo.setDurchfuehrendeBehoerde("123456");
        aufrufKontextTo.setDurchfuehrenderBenutzerKennung("martha.mustermann@bva.bund.de");
        aufrufKontextTo.setDurchfuehrenderBenutzerPasswort(null);
        aufrufKontextTo.setDurchfuehrenderSachbearbeiterName("Martha Mustermann");
        aufrufKontextTo.setRolle(new String[] {});
        aufrufKontextTo.setRollenErmittelt(aufrufKontextTo.getRolle() != null);
        return aufrufKontextTo;
    }

    private ClientAufrufKontextTo getIsyClientAufrufKontextToStub() {
        ClientAufrufKontextTo clientAufrufKontextTo = new ClientAufrufKontextTo();
        clientAufrufKontextTo.setKennung("GEORGE123");
        clientAufrufKontextTo.setKennwort("123");
        clientAufrufKontextTo.setZertifikat(new byte[] {1, 2, 3, 4});
        clientAufrufKontextTo.setZertifikatKennwort(null);
        return clientAufrufKontextTo;
    }

    private boolean areEqual(AufrufKontextTo ak1, AufrufKontextTo ak2) {
        if ((ak1 == null && ak2 != null) || (ak1 != null && ak2 == null)) {
            return false;
        }
        if (ak1 == null) {
            return true;
        }
        boolean result = Objects.equals(ak1.getDurchfuehrenderSachbearbeiterName(), ak2.getDurchfuehrenderSachbearbeiterName());
        result &= Objects.equals(ak1.getDurchfuehrenderBenutzerPasswort(), ak2.getDurchfuehrenderBenutzerPasswort());
        result &= Objects.equals(ak1.getDurchfuehrenderBenutzerKennung(), ak2.getDurchfuehrenderBenutzerKennung());
        result &= Objects.equals(ak1.getDurchfuehrendeBehoerde(), ak2.getDurchfuehrendeBehoerde());
        result &= Objects.equals(ak1.getKorrelationsId(), ak2.getKorrelationsId());
        result &= Objects.deepEquals(ak1.getRolle(), ak2.getRolle());
        return result;
    }

    private boolean areEqual(ClientAufrufKontextTo cak1, ClientAufrufKontextTo cak2) {
        if ((cak1 == null && cak2 != null) || (cak1 != null && cak2 == null)) {
            return false;
        }
        if (cak1 == null) {
            return true;
        }
        boolean result = Objects.deepEquals(cak1.getZertifikat(), cak2.getZertifikat());
        result &= Objects.equals(cak1.getZertifikatKennwort(), cak2.getZertifikatKennwort());
        result &= Objects.equals(cak1.getKennwort(), cak2.getKennwort());
        result &= Objects.equals(cak1.getKennung(), cak2.getKennung());
        return result;
    }
}
