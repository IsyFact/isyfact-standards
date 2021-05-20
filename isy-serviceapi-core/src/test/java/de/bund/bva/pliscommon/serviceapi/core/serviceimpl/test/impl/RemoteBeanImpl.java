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
package de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl;

import java.lang.reflect.InvocationTargetException;

import de.bund.bva.pliscommon.exception.PlisException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.PlisBusinessTestException;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.PlisTechnicalRuntimeTestException;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public class RemoteBeanImpl implements RemoteBean {

    @Override
    public void eineMethode() {
        // noop
    }

    @Override
    public void eineAndereMethode() {
        // noop
    }

    @Override
    public void methodeMitZweiToExceptions() throws IllegalStateException {
        // noop
    }

    @Override
    public void methodeMitToException() throws IllegalStateException {
        // noop
    }

    @Override
    public void methodeMitZweiTechnicalToExceptions()
            throws IllegalStateException {
        // noop
    }

    @Override
    public void eineAndereMethode(AufrufKontextTo to, Integer zahl) {
        // noop
    }

    public void nichtImInterfaceDeklariert() {
        // noop
    }

    @Override
    public void eineAndereMethode(Integer zahl) {
        // noop
    }

    @Override
    public void eineAndereMethode(Double zahl) {

    }

    @Override
    public void eineMethodeMitException() throws InvocationTargetException {
        IllegalArgumentException e = new IllegalArgumentException("eine simulierte Exception.");
        throw new InvocationTargetException(e);
    }

    @Override
    public void eineMethodeMitPlisException() throws PlisException {
        throw new PlisBusinessTestException();
    }

    @Override
    public void eineMethodeMitPlisTechnicalRuntimeException() throws PlisTechnicalRuntimeException {
        throw new PlisTechnicalRuntimeTestException("", null, null);
    }

}
