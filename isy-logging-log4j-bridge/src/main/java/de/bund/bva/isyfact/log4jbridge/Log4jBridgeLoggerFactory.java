package de.bund.bva.isyfact.log4jbridge;

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * Log4jBridgeLoggerFactory zum erstellen des Bridge-Loggers auf log4j.
 */
public class Log4jBridgeLoggerFactory implements ILoggerFactory {

    /** Cache zur Verwaltung bestehender Logger-Instanzen. */
    private Map<String, Logger> cache;

    /**
     * Konstruktor der Klasse. Initialisiert den Root-Logger.
     * 
     */
    public Log4jBridgeLoggerFactory() {
        cache = new ConcurrentHashMap<String, Logger>();

        // Initialisieren des Root-Loggers
        org.apache.log4j.Logger log4jRootLogger = org.apache.log4j.Logger.getRootLogger();
        Log4jLoggerAdapter log4jRootLoggerAdapter = new Log4jLoggerAdapter(log4jRootLogger);
        cache.put(Logger.ROOT_LOGGER_NAME, log4jRootLoggerAdapter);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.ILoggerFactory#getLogger(java.lang.String)
     */
    @Override
    public Logger getLogger(String name) {
        Logger logger = cache.get(name);

        if (logger == null) {
            org.apache.log4j.Logger log4jLogger = org.apache.log4j.Logger.getLogger(name);
            logger = new Log4jLoggerAdapter(log4jLogger);
            cache.put(name, logger);
        }

        return logger;
    }

}
