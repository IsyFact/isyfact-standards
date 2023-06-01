package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import java.util.Objects;

import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.security.core.Berechtigungsmanager;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * The implementation of the {@link CreateAufrufKontextToStrategy} takes the {@link Berechtigungsmanager} from the module isy-security and creates,
 * based on the keys, the transport object ad hoc.
 *
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class DefaultCreateAufrufKontextToStrategy implements CreateAufrufKontextToStrategy {

    /**
     * The implementation of the {@link Berechtigungsmanager} of isy-security with all information of the logged in user.
     */
    private final Berechtigungsmanager berechtigungsmanager;

    /**
     * key for the bhknz.
     */
    private static final String BHKNZ = "bhknz";

    public DefaultCreateAufrufKontextToStrategy (Berechtigungsmanager berechtigungsmanager) {
        this.berechtigungsmanager = berechtigungsmanager;
    }

    @Override
    public AufrufKontextTo create() {
        AufrufKontextTo aufrufKontextTo = new AufrufKontextTo();

        aufrufKontextTo.setDurchfuehrenderBenutzerKennung(getStringValue(StandardClaimNames.PREFERRED_USERNAME));
        aufrufKontextTo.setDurchfuehrenderSachbearbeiterName(ermittleSachbearbeiterName());
        aufrufKontextTo.setRolle(berechtigungsmanager.getRollen().toArray(new String[0]));
        aufrufKontextTo.setDurchfuehrendeBehoerde(getStringValue(BHKNZ));
        aufrufKontextTo.setRollenErmittelt(true);
        aufrufKontextTo.setDurchfuehrenderBenutzerPasswort("");
        aufrufKontextTo.setKorrelationsId(MdcHelper.liesKorrelationsId());

        return aufrufKontextTo;
    }

    private String ermittleSachbearbeiterName () {
        String name = getStringValue(StandardClaimNames.NAME);
        if (name.isEmpty()) {
            name = getStringValue(StandardClaimNames.PREFERRED_USERNAME);
        }

        return name;
    }

    private String getStringValue(final String key) {
        Object object = berechtigungsmanager.getTokenAttribute(key);
        return Objects.toString(object, "");
    }
}
