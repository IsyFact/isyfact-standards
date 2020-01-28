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

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.serviceapi.common.AufrufKontextToHelper;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.Ordered;

/**
 * Erzeugt einen {@link AufrufKontext}, befüllt ihn mit den Daten aus dem {@link AufrufKontextTo}, und setzt
 * ihn in dem {@link AufrufKontextVerwalter}.
 *
 * @param <T> der Aufrufkontext
 */
public class StelltAufrufKontextBereitInterceptor<T extends AufrufKontext> implements MethodInterceptor,
    Ordered {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory
        .getLogger(StelltAufrufKontextBereitInterceptor.class);

    /**
     * Zugriff auf die AufrufKontextFactory zum Mappen des empfangenen AufrufKontextTo auf den
     * Anwendungsspezifischen AufrufKontext.
     */
    private AufrufKontextFactory<T> aufrufKontextFactory;

    /** Zugriff auf den AufrufKontextVerwalter, um den AufrufKontext zu setzten. */
    private AufrufKontextVerwalter<T> aufrufKontextVerwalter;


    /**
     * Standard-Reihenfolge für die Intercepter-Ausführung, wenn kein anderer gesetzt wird.
     * Muss vor GesichertInterceptor ausgeführt werden.
     */
    private static final int DEFAULT_ORDER = 10_000;

    /** Reihenfolge für die Interceptor-Ausführung. */
    private int order = DEFAULT_ORDER;

    @Required
    public void setAufrufKontextFactory(AufrufKontextFactory<T> aufrufKontextFactory) {
        this.aufrufKontextFactory = aufrufKontextFactory;
    }

    @Required
    public void setAufrufKontextVerwalter(AufrufKontextVerwalter<T> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        T alterAufrufKontext = this.aufrufKontextVerwalter.getAufrufKontext();

        AufrufKontextTo aufrufKontextTo =
            AufrufKontextToHelper.leseAufrufKontextTo(invocation.getArguments());

        if (aufrufKontextTo == null) {
            LOG.debug("Es wurde kein AufrufKontext übermittelt.");
            this.aufrufKontextVerwalter.setAufrufKontext(null);
        } else {
            T aufrufKontext = this.aufrufKontextFactory.erzeugeAufrufKontext();

            // AufrufKontextTo auf AufrufKontext abbilden
            aufrufKontext.setDurchfuehrendeBehoerde(aufrufKontextTo.getDurchfuehrendeBehoerde());
            aufrufKontext.setDurchfuehrenderBenutzerKennung(aufrufKontextTo
                .getDurchfuehrenderBenutzerKennung());
            aufrufKontext.setDurchfuehrenderBenutzerPasswort(aufrufKontextTo
                .getDurchfuehrenderBenutzerPasswort());
            aufrufKontext.setDurchfuehrenderSachbearbeiterName(aufrufKontextTo
                .getDurchfuehrenderSachbearbeiterName());
            aufrufKontext.setKorrelationsId(aufrufKontextTo.getKorrelationsId());
            aufrufKontext.setRolle(aufrufKontextTo.getRolle());
            aufrufKontext.setRollenErmittelt(aufrufKontextTo.isRollenErmittelt());

            this.aufrufKontextFactory.nachAufrufKontextVerarbeitung(aufrufKontext);

            this.aufrufKontextVerwalter.setAufrufKontext(aufrufKontext);
        }

        try {
            return invocation.proceed();
        } finally {
            this.aufrufKontextVerwalter.setAufrufKontext(alterAufrufKontext);
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
