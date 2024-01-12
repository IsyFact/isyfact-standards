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
package de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0;

import de.bund.bva.isyfact.serviceapi.core.httpinvoker.user.User;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * RemoteBean-Interface of the Dummy-Service.
 */
public interface DummyServiceRemoteBean {

    /**
     * Returns the message.
     *
     * @param message The message.
     * @return The message.
     */
    String ping(String message);

    String ping(AufrufKontextTo aufrufKontextTo, String message);

    String addUser(User user);

}
