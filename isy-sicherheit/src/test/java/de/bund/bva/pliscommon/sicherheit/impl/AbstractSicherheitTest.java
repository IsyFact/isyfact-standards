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
package de.bund.bva.pliscommon.sicherheit.impl;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.accessmgr.test.TestAccessManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/resources/spring/application_interceptor.xml")
@DirtiesContext
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractSicherheitTest {

    @Autowired
    protected Konfiguration konfiguration;

    @Autowired
    protected Sicherheit sicherheit;

    @Autowired
    protected TestAccessManager testAccessManager;

    @Autowired
    protected AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    @Autowired
    protected AufrufKontextFactory<AufrufKontext> aufrufKontextFactory;

    @Before
    public void setup() {
        this.aufrufKontextVerwalter.setAufrufKontext(null);
        this.testAccessManager.reset();
        ((SicherheitImpl) this.sicherheit).leereCache();
        this.testAccessManager.setResultAuthentifiziere("Rolle_A", "Rolle_B");
    }

}
