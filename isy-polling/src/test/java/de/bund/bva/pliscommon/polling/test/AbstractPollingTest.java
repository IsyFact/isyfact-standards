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
package de.bund.bva.pliscommon.polling.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(inheritLocations = true, locations = { 
    "classpath:resources/spring/spring-context.xml",
    "classpath:resources/spring/konfiguration.xml",
    "classpath:resources/spring/plis-polling.xml"})
public abstract class AbstractPollingTest {

    /**
     * Überprüft den JMX-Status und gibt Informationen auf der Konsole aus.
     * @return true, falls JMX aktiv ist, false sonst. 
     */
    public boolean pruefeJMXStatus() throws Exception {

        if (System.getProperty("com.sun.management.jmxremote") == null) {
            System.out.println("JMX remote ist disabled");
            return false;
        } else {
            System.out.println("JMX remote is enabled");
            if (System.getProperty("com.sun.management.jmxremote.port") != null) {
                System.out.println("JMX running on port "
                    + Integer.parseInt(
                    System.getProperty("com.sun.management.jmxremote.port")));
            }
        }

        return true;
    }
}
