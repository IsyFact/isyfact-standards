package de.bund.bva.isyfact.logging.util;

/*
 * #%L
 * isy-logging
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

/**
 * Interface für BeanConverter zur Serialisierung von Beans bei der Logausgabe.
 * 
 * @author msg systems AG, Andreas Buechner
 * @version $Id$
 */
public interface BeanConverter {

    /**
     * Konvertiert das übergebene Bean in eine Repräsentation, die direkt als Wert für einen Platzhalter in
     * einer Logausgabe verwendet werden kann.
     * 
     * @param bean
     *            das zu konvertierende Bean.
     * @return das konvertierte Bean.
     */
    public Object convert(Object bean);

}