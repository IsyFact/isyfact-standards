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
package de.bund.bva.pliscommon.serviceapi.core.aufrufkontext;

import java.util.UUID;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.aufrufkontext.common.konstanten.EreignisSchluessel;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Dieser Aspekt sorgt dafür, dass in Service-Methoden automatisch der Logging-Kontext gesetzt wird.
 *
 *
 */
public class StelltLoggingKontextBereitInterceptor implements MethodInterceptor {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory
        .getLogger(StelltLoggingKontextBereitInterceptor.class);

    /**
     * Dieser Aspekt sorgt dafür, dass eine Korrelation-ID erzeugt wird, falls im AufrufKontext keine gesetzt
     * ist. Die Korrelation ID wird vor dem eigentlichen Aufruf im Logging-Kontext gesetzt und danach
     * automatisch wieder entfernt.
     *
     * @param invocation
     *            der Methodenaufruf
     *
     * @return das Ergebnis der eigentlichen Methode.
     * @throws Throwable
     *             wenn die eigentliche Methode einen Fehler wirft.
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String korrelationsId = null;

        AufrufKontextTo aufrufKontextTo =
            AufrufKontextToHelper.leseAufrufKontextTo(invocation.getArguments());

        if (aufrufKontextTo == null) {
            korrelationsId = UUID.randomUUID().toString();
            LOG.warn(EreignisSchluessel.KEIN_AUFRUFKONTEXT_UEBERMITTELT,
                "Es wurde kein AufrufKontext übermittelt. Erzeuge neue Korrelations-ID.");
        } else if (StringUtils.isEmpty(aufrufKontextTo.getKorrelationsId())) {
            LOG.debug("Es wurde keine Korrelations-ID übermittelt. Erzeuge neue Korrelations-ID.");
            korrelationsId = UUID.randomUUID().toString();
            aufrufKontextTo.setKorrelationsId(korrelationsId);
        } else {
            LOG.debug("Setzte Korrelations-ID aus AufrufKontext.");
            korrelationsId = aufrufKontextTo.getKorrelationsId();
        }

        try {
            MdcHelper.pushKorrelationsId(korrelationsId);
            return invocation.proceed();
        } finally {
            // Nach dem Aufruf alle Korrelations-IDs vom MDC entfernen.
            MdcHelper.entferneKorrelationsIds();
        }
    }
}
