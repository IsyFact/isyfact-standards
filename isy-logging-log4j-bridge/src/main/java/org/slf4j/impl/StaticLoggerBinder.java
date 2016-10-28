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
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

import de.bund.bva.isyfact.log4jbridge.Log4jBridgeLoggerFactory;

/**
 * StaticLoggerBinder zum Zugriff auf die Logger-Factory der Bridge. Diese Klasse muss von jeder
 * SLF4J-Implementierung bereitgestellt werden. Sie wird direkt von der SLF4J-API genutzt um eine Instanz der
 * LoggerFactory zu erhalten.
 * 
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {

    /** Singleton der Klasse, die SLF4J bereitgestellt wird. */
    public static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    /** Die zu verwendende LoggerFactory. */
    private final ILoggerFactory loggerFactory = new Log4jBridgeLoggerFactory();

    /**
     * Version der SLF4J-Api, dieser gegen die diese Implementierung erstellt wurde. Diese wird durch SLF4J
     * zur Prüfung der Versionskompabilität verwendet. Wir halten uns hierbei an die Version, die auch die
     * in isy-logging genutzte logback-Version verwendet.
     */
    public static String REQUESTED_API_VERSION = "1.6";

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.LoggerFactoryBinder#getLoggerFactory()
     */
    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.spi.LoggerFactoryBinder#getLoggerFactoryClassStr()
     */
    @Override
    public String getLoggerFactoryClassStr() {
        return Log4jBridgeLoggerFactory.class.getName();
    }

    /**
     * Liefert den Wert des Attributs 'singleton'.
     * 
     * @return Wert des Attributs.
     */
    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

}
