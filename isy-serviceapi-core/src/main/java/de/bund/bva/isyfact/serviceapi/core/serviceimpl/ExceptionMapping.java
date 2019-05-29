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
package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.bund.bva.isyfact.exception.PlisException;
import de.bund.bva.isyfact.exception.PlisTechnicalRuntimeException;
import de.bund.bva.isyfact.exception.service.PlisTechnicalToException;
import de.bund.bva.isyfact.exception.service.PlisToException;

/**
 * Definiert, wie Exceptions des Anwendungskerns in einer Service-Komponente auf TO-Exceptions abgebildet
 * werden. Diese Annotation muss im Implementierungspackage der Service-Komponente verwendet werden
 * (Packagename = Packagename der RemoteBean-Schnittstelle + ".impl").
 * 
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionMapping {

    /**
     * Die technische TO-Exception, auf die alle {@link PlisTechnicalRuntimeException} und alle unchecked
     * Exceptions abgebildet werden.
     */
    Class<? extends PlisTechnicalToException> technicalToException();

    /**
     * Die Abbildungen der {@link PlisException PlisExceptions} auf {@link PlisToException PlisToExceptions}.
     */
    Mapping[] mappings();
}
