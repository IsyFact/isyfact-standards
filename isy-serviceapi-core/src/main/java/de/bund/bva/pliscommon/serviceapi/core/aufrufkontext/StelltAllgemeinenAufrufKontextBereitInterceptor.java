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
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import ma.glasnost.orika.MapperFacade;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Ein Interceptor, welcher den AufrufKontext auf Basis eines Mappings bereit stellt.
 */
public class StelltAllgemeinenAufrufKontextBereitInterceptor<T extends AufrufKontext> implements
    MethodInterceptor {

    private final MapperFacade mapper;

    private static final IsyLogger LOGISY = IsyLoggerFactory
        .getLogger(StelltAllgemeinenAufrufKontextBereitInterceptor.class);

    /**
     * Zugriff auf die AufrufKontextFactory zum Mappen des empfangenen AufrufKontextTo auf den
     * Anwendungsspezifischen AufrufKontext.
     */
    private final AufrufKontextFactory<T> aufrufKontextFactory;

    /** Zugriff auf den AufrufKontextVerwalter, um den AufrufKontext zu setzten. */
    private final AufrufKontextVerwalter<T> aufrufKontextVerwalter;

    public StelltAllgemeinenAufrufKontextBereitInterceptor(MapperFacade mapper,
        AufrufKontextFactory<T> aufrufKontextFactory, AufrufKontextVerwalter<T> aufrufKontextVerwalter) {
        this.mapper = mapper;
        this.aufrufKontextFactory = aufrufKontextFactory;
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
            LOGISY.debug("Es wurde kein AufrufKontext uebermittelt.");
            this.aufrufKontextVerwalter.setAufrufKontext(null);
        } else {
            T aufrufKontext = this.aufrufKontextFactory.erzeugeAufrufKontext();
            this.mapper.map(aufrufKontextTo, aufrufKontext);
            this.aufrufKontextFactory.nachAufrufKontextVerarbeitung(aufrufKontext);

            this.aufrufKontextVerwalter.setAufrufKontext(aufrufKontext);
        }

        try {
            return invocation.proceed();
        } finally {
            // Setze alten AufrufKontext zurueck
            this.aufrufKontextVerwalter.setAufrufKontext(alterAufrufKontext);
        }
    }
}
