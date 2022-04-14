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
package de.bund.bva.isyfact.serviceapi.common;

import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.DefaultAufrufKontextToResolver;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Helperclass to resolve AufrufKontextTo from service parameters.
 * @deprecated use non-static {@link AufrufKontextToResolver} as a bean instead.
 */
@Deprecated
public class AufrufKontextToHelper {

    /**
     * Loads the first {@link AufrufKontextTo} found from the parameters of the called function.
     *
     * @param args
     *            arguments of the service call
     *
     * @return the AufrufKontextTo object
     */
    public static AufrufKontextTo leseAufrufKontextTo(Object[] args) {
        return new DefaultAufrufKontextToResolver().leseAufrufKontextTo(args).orElse(null);
    }

}
