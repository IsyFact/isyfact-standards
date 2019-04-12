/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.pliscommon.sicherheit.web;

import java.util.Collection;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.sicherheit.Berechtigungsmanager;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.AuthentifizierungTechnicalException;
import de.bund.bva.pliscommon.sicherheit.common.konstanten.SicherheitFehlerSchluessel;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

/**
 * Ein AccessDecisionManager zur Verwendung mit spring-security.
 * 
 * Der AccessDecisionManager delegiert die Entscheidung über die Berechtigung an den in der Komponente
 * {@link Sicherheit} hinterlegten {@link Berechtigungsmanager}.
 * 
 * 
 */
public class DelegatingAccessDecisionManager implements AccessDecisionManager {

    /** Zugriff auf die Komponente Sicherheit. */
    private final Sicherheit<? extends AufrufKontext> sicherheit;

    public DelegatingAccessDecisionManager(Sicherheit<? extends AufrufKontext> sicherheit) {
        this.sicherheit = sicherheit;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void decide(Authentication authentication, Object object,
        Collection<ConfigAttribute> configAttributes) throws AccessDeniedException,
        InsufficientAuthenticationException {
        Berechtigungsmanager bm;
        try {
            bm = sicherheit.getBerechtigungsManager();
        } catch (AuthentifizierungTechnicalException e) {
            throw new InsufficientAuthenticationException(
                SicherheitFehlerSchluessel.MSG_BERECHTIGUNGSMANAGER_NICHT_VERFUEGBAR, e);
        } catch (AuthentifizierungFehlgeschlagenException e) {
            throw new InsufficientAuthenticationException(
                SicherheitFehlerSchluessel.MSG_BERECHTIGUNGSMANAGER_NICHT_VERFUEGBAR, e);
        }

        for (Object obj : configAttributes) {
            ConfigAttribute attribute = (ConfigAttribute) obj;

            String gefordertesRecht = attribute.getAttribute();
            if (!bm.hatRecht(gefordertesRecht)) {
                throw new AccessDeniedException("Keine Berechtigung für: " + gefordertesRecht + " ("
                    + SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_FEHLGESCHLAGEN + ")");
            }
        }
    }
}