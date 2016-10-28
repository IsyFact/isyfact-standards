package org.slf4j.impl;

/*
 * #%L
 * isy-logging-log4j-bridge
 * %%
 * 
 * %%
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
 * #L%
 */

import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

/**
 * StaticMarkerBinder zum Zugriff auf den MarkerBinder der Bridge. Sie wird direkt von der SLF4J-API genutzt
 * um eine Instanz der MarkerBinder zu erhalten.
 * 
 */
public class StaticMarkerBinder implements MarkerFactoryBinder {

    /**
     * Singleton der Klasse, die SLF4J bereitgestellt wird. SLf4J greift direkt auf das Attribut zu - nicht
     * den getter.
     */
    public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

    /** Die zu verwendende MarkerFactory. */
    private final IMarkerFactory markerFactory = new BasicMarkerFactory();

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.MarkerFactoryBinder#getMarkerFactory()
     */
    @Override
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.MarkerFactoryBinder#getMarkerFactoryClassStr()
     */
    @Override
    public String getMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }

}
