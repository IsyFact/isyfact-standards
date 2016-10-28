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

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Schnittstelle einer Servicekomponente zum Testen.
 *
 * @author Capgemini, Marcel Vielsack
 * @version $Id: ServiceIntf.java 130451 2015-02-13 16:17:14Z sdm_ahoerning $
 */
public interface ServiceIntf {

    public void erfolgreicherAufruf(AufrufKontextTo aufrufKontextTo);

    public void fehlerhafterAufruf(AufrufKontextTo aufrufKontextTo) throws TestTechnicalToException;

}