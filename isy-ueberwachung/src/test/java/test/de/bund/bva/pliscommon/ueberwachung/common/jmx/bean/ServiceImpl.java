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
package test.de.bund.bva.pliscommon.ueberwachung.common.jmx.bean;

import java.util.UUID;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Eine Bean, welche Dummy Methoden bereitstellt um Interceptor zu testen.
 *
 * @author Capgemini, Marcel Vielsack
 * @version $Id: ServiceImpl.java 130451 2015-02-13 16:17:14Z sdm_ahoerning $
 *
 */
public class ServiceImpl implements ServiceIntf {

    /**
     * {@inheritDoc}
     */
    @Override
    public void erfolgreicherAufruf(AufrufKontextTo aufrufKontextTo) {
        // noop
    }

    /**
     * {@inheritDoc}
     * @throws TestTechnicalToException
     */
    @Override
    public void fehlerhafterAufruf(AufrufKontextTo aufrufKontextTo) throws TestTechnicalToException {
        // Fehler simulieren
        throw new TestTechnicalToException("Das ist ein dummy Fehler", "TEST00001", UUID.randomUUID()
            .toString());

    }

}
