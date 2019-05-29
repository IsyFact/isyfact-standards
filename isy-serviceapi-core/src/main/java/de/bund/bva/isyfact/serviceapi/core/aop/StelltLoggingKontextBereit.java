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
package de.bund.bva.isyfact.serviceapi.core.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kennzeichnet Operationen einer Exception-Fassade, an die zwingend ein AufrufKontextTo übermittelt wird, aus
 * dem die Korrelations-ID als Logging-Kontext bereitgestellt wird. Ist kein AufrufKontextTo vorhanden, wird
 * eine Exception geworfen. Alternativ können auch Operationen ohne AufrufKontextTo gekennzeichnet werden.
 * Dann muss dies mit dem Flag nutzeAufrufKontext=false kenntlich gemacht werden.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
public @interface StelltLoggingKontextBereit {
    boolean nutzeAufrufKontext() default true;
}
