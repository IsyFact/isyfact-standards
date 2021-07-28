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
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.LogHelper;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.serviceapi.common.konstanten.EreignisSchluessel;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * HTTP-InvokerClientInterceptor zum Erzeugen IsyFact-konformer Loggingeinträge.
 */
public class IsyHttpInvokerClientInterceptor extends HttpInvokerClientInterceptor {

    /** Logger der Klasse. */
    private static final IsyLogger LOGGER = IsyLoggerFactory.getLogger(IsyHttpInvokerClientInterceptor.class);

    /** Helper, zum Erzeugen der Logeinträge. */
    private LogHelper logHelper = new LogHelper(false, false, true, false, false, 0);

    /** Name des aufgerufenen Nachbarsystems. */
    private String remoteSystemName;

    /**
     * {@inheritDoc}
     *
     * Beim Aufruf wird immer eine neue Korrelations-ID erzeugt und zu der bestehenden Korrelations-ID des
     * Aufrufkontextes hinzugefügt. Damit muss das aufrufende System
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

        String oldKorrelationsIdOfKontext = null;

        // Warnung bei falschem Setzen der Korr-Id im Aufrufkontext.
        if (aufrufKontextTo != null && aufrufKontextTo.getKorrelationsId() != null &&
            aufrufKontextTo.getKorrelationsId().length() != 0 &&
            !MdcHelper.liesKorrelationsId()
                .equals(aufrufKontextTo.getKorrelationsId() + ";" + korrelationsId)) {
            LOGGER.warn(EreignisSchluessel.AUFRUFKONTEXT_KORRID_KORRIGIERT,
                "Die Korrelations-Id {} im Aufrufkontext wurde korrigiert, "
                    + "da diese nicht mit der Korr-Id auf dem MDC {} übereinstimmt.",
                aufrufKontextTo.getKorrelationsId(), MdcHelper.liesKorrelationsId());
        }

        // Korrektlations-Id im Kontext setzen.
        if (Objects.nonNull(aufrufKontextTo)) {
            oldKorrelationsIdOfKontext = aufrufKontextTo.getKorrelationsId();
            LOGGER.warn(EreignisSchluessel.AUFRUFKONTEXT_KORRID_KORRIGIERT, "Value: {}", oldKorrelationsIdOfKontext);
            aufrufKontextTo.setKorrelationsId(MdcHelper.liesKorrelationsId());
            LOGGER.warn(EreignisSchluessel.AUFRUFKONTEXT_KORRID_KORRIGIERT, "Value: {}", MdcHelper.liesKorrelationsId());
        }

        // Logge Aufruf Nachbarsystem.
        this.logHelper.loggeNachbarsystemAufruf(LOGGER, methode, this.remoteSystemName, getServiceUrl());
        long startzeit = 0;
        try {
            startzeit = this.logHelper.ermittleAktuellenZeitpunkt();
            Object ergebnis = super.invoke(methodInvocation);

            // Aufruf ist ohne Exception verarbeitet worden.
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
                if (Objects.nonNull(oldKorrelationsIdOfKontext)) {
                    aufrufKontextTo.setKorrelationsId(oldKorrelationsIdOfKontext);
                    LOGGER.warn(EreignisSchluessel.AUFRUFKONTEXT_KORRID_KORRIGIERT, "Value: {}", oldKorrelationsIdOfKontext);
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (this.remoteSystemName == null) {
            throw new IllegalArgumentException("Property 'remoteSystemName' is required");
        }
    }

    /**
     * Setzt den Wert des Attributs 'remoteSystemName'.
     *
     * @param remoteSystemName
     *            Neuer Wert des Attributs.
     */
    public void setRemoteSystemName(String remoteSystemName) {
        this.remoteSystemName = remoteSystemName;
    }

    /**
     * Setzt den Wert des Attributs 'logHelper'.
     *
     * @param logHelper
     *            Neuer Wert des Attributs.
     */
    public void setLogHelper(LogHelper logHelper) {
        this.logHelper = logHelper;
    }

}
