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
package de.bund.bva.pliscommon.polling.impl;

import org.springframework.test.context.ContextConfiguration;

import de.bund.bva.pliscommon.polling.test.AbstractPollingStandaloneTest;

/**
 * Tests für den Polling Verwalter.
 * 
 * Damit die Tests funktionieren, muss JMX über die folgenden Startparameter der VM 
 * aktiviert werden:
 * 
 * -Dcom.sun.management.jmxremote
 * -Dcom.sun.management.jmxremote.port=9010
 * -Dcom.sun.management.jmxremote.local.only=false
 * -Dcom.sun.management.jmxremote.ssl=false
 * -Dcom.sun.management.jmxremote.authenticate=false
 *
 */
@ContextConfiguration(inheritLocations = true, locations = { 
    "classpath:resources/spring/konfiguration-standalone-nojmxconn.xml"})
public class PollingVerwalterStandaloneNoJmxConnTest extends AbstractPollingStandaloneTest {

}
