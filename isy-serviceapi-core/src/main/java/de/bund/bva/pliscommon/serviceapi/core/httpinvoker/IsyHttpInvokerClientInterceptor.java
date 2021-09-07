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
package de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

import de.bund.bva.pliscommon.serviceapi.common.AufrufKontextToHelper;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.LogHelper;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.serviceapi.common.konstanten.EreignisSchluessel;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * HTTP-InvokerClientInterceptor to generate IsyFact compliant logging entries.
 */
public class IsyHttpInvokerClientInterceptor extends HttpInvokerClientInterceptor {

    /**
     * Logger
     */
    private static final IsyLogger LOGGER = IsyLoggerFactory.getLogger(IsyHttpInvokerClientInterceptor.class);

    /**
     * Helper for creating Logentries.
     */
    private LogHelper logHelper = new LogHelper(false, false, true, false, false, 0);

    /**
     * Name of the remote system that is being called.
     */
    private String remoteSystemName;

    /**
     * {@inheritDoc}
     * <p>
     * When called, a new correlation ID is always created and added to the existing correlation ID of the
     * calling context.
     *
     * @see org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        String korrelationsId = UUID.randomUUID().toString();
        boolean aufrufErfolgreich = false;

        Method methode = methodInvocation.getMethod();

        AufrufKontextTo aufrufKontextTo =
            AufrufKontextToHelper.leseAufrufKontextTo(methodInvocation.getArguments());

        LOGGER.debug("Erzeuge neue Korrelations-ID {}", korrelationsId);
        MdcHelper.pushKorrelationsId(korrelationsId);

        if (Objects.nonNull(aufrufKontextTo)) {
            // Warning if there was already a Korr-Id in the AufrufkontextTo which didn't match
            if (Objects.nonNull(aufrufKontextTo.getKorrelationsId()) && !aufrufKontextTo.getKorrelationsId()
                .isEmpty() && !MdcHelper.liesKorrelationsId()
                .equals(aufrufKontextTo.getKorrelationsId() + ";" + korrelationsId)) {
                LOGGER.warn(EreignisSchluessel.AUFRUFKONTEXT_KORRID_KORRIGIERT,
                    "Die Korrelations-Id {} im Aufrufkontext wurde korrigiert, "
                        + "da diese nicht mit der Korr-Id auf dem MDC {} übereinstimmt.",
                    aufrufKontextTo.getKorrelationsId(), MdcHelper.liesKorrelationsId());
            }

            // set korrelationsId in the context
            aufrufKontextTo.setKorrelationsId(MdcHelper.liesKorrelationsId());
        }

        // Logging call of remote system
        this.logHelper.loggeNachbarsystemAufruf(LOGGER, methode, this.remoteSystemName, getServiceUrl());
        long startzeit = 0;
        try {
            startzeit = this.logHelper.ermittleAktuellenZeitpunkt();
            Object ergebnis = super.invoke(methodInvocation);

            // call was executed without exceptions
            aufrufErfolgreich = true;
            return ergebnis;

        } finally {
            long endezeit = this.logHelper.ermittleAktuellenZeitpunkt();
            long dauer = endezeit - startzeit;
            this.logHelper.loggeNachbarsystemErgebnis(LOGGER, methode, this.remoteSystemName, getServiceUrl(),
                aufrufErfolgreich);
            this.logHelper.loggeNachbarsystemDauer(LOGGER, methode, dauer, this.remoteSystemName,
                getServiceUrl(), aufrufErfolgreich);

            MdcHelper.entferneKorrelationsId();

            if (Objects.nonNull(aufrufKontextTo)) {
                aufrufKontextTo.setKorrelationsId(MdcHelper.liesKorrelationsId());
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (this.remoteSystemName == null) {
            throw new IllegalArgumentException("Property 'remoteSystemName' is required");
        }
    }

    /**
     * Sets value of attribute 'remoteSystemName'.
     *
     * @param remoteSystemName New value of the attribute.
     */
    public void setRemoteSystemName(String remoteSystemName) {
        this.remoteSystemName = remoteSystemName;
    }

    /**
     * Sets value of attribute 'logHelper'.
     *
     * @param logHelper New value of the attribute.
     */
    public void setLogHelper(LogHelper logHelper) {
        this.logHelper = logHelper;
    }

}
