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
package de.bund.bva.pliscommon.sicherheit.annotation;

import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.pliscommon.sicherheit.annotation.bean.ServiceImpl;
import de.bund.bva.pliscommon.sicherheit.common.exception.AnnotationFehltRuntimeException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.sicherheit.annotation.bean.ServiceIntf;

import junit.framework.Assert;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "/resources/spring/application_nutzer_authentifizierung.xml")
public class NutzerAuthentifizierungInterceptorTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ServiceIntf testBean;

    // @Autowired
    // private ServiceImpl serviceImpl;

    @Autowired
    private SicherheitStub sicherheitStub;

    @Autowired
    private Konfiguration konfiguration;

    @Autowired
    private NutzerAuthentifizierungInterceptor<AufrufKontextImpl> interceptor;

    @Before
    public void setUp() {
        this.sicherheitStub.reset();
    }

    @Test
    public void testPositiv_gesichertDurch_RechtA() {
        this.testBean.gesichertDurch_RechtA();
        Assert.assertNotNull(this.sicherheitStub.getLetzterAufrufKontext());
        Assert.assertEquals(this.konfiguration.getAsString("testBenutzer.kennung"), this.sicherheitStub
            .getLetzterAufrufKontext().getDurchfuehrenderBenutzerKennung());
    }

    @Test(expected = AnnotationFehltRuntimeException.class)
    public void testNegativ_gesichertDurchNichts() throws Throwable{
        SimpleMethodInvocation invocation = new SimpleMethodInvocation(null, ServiceImpl.class.getMethod("statischeMethodeGesichert"), new Object[]{} );
        interceptor.invoke(invocation);
    }

}
