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
package de.bund.bva.isyfact.ueberwachung.common.jmx.bean;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.IsyLoggerStandard;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Eine Bean, welche Dummy Methoden bereitstellt um Interceptor zu testen.
 *
 * @author Capgemini, Marcel Vielsack
 * @version $Id: ServiceImpl.java 130451 2015-02-13 16:17:14Z sdm_ahoerning $
 *
 */
public class ServiceImpl implements Service {

    private static final IsyLoggerStandard LOG = IsyLoggerFactory.getLogger(Service.class);

    private static final Random random = new SecureRandom();

    @Override
    public void erfolgreicherAufruf() {
        int dauer = random.nextInt(200);
        try {
            Thread.sleep(dauer);
        } catch (InterruptedException e) { }
        LOG.debug("Erfolgreicher Aufruf mit Dauer {})", dauer);
    }

    @Override
    public void fehlerhafterAufruf() throws TestTechnicalToException {
        int dauer = random.nextInt(200);
        try {
            Thread.sleep(dauer);
        } catch (InterruptedException e) { }
        LOG.debug("Fehlerhafter Aufruf mit Dauer {})", dauer);
        throw new TestTechnicalToException("Das ist ein dummy Fehler", "TEST00001", UUID.randomUUID()
            .toString());
    }
}
