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
package de.bund.bva.isyfact.serviceapi.core.serviceimpl.test;

import java.lang.reflect.InvocationTargetException;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.exception.service.BusinessToException;
import de.bund.bva.isyfact.exception.service.TechnicalToException;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public interface RemoteBean {

    void eineMethode();

    void eineAndereMethode();

    void eineAndereMethode(Integer zahl);

    void eineAndereMethode(Double zahl);

    void eineAndereMethode(AufrufKontextTo to, Integer zahl);

    void methodeMitZweiToExceptions() throws IllegalStateException, TechnicalToException, BusinessToException;

    void methodeMitToException() throws IllegalStateException, TechnicalToException;

    void methodeMitZweiTechnicalToExceptions() throws IllegalStateException, TechnicalToException,
        TechnicalTestToException;

    void eineMethodeMitException() throws InvocationTargetException, TechnicalToException;

    void eineMethodeMitBusinessException() throws BaseException, TechnicalToException;

    void eineMethodeMitTechnicalRuntimeException() throws TechnicalRuntimeException, TechnicalToException;
}
