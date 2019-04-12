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
import de.bund.bva.pliscommon.sicherheit.config.NutzerauthentifizierungTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NutzerauthentifizierungTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test", "isy.logging.anwendung.version=test",
    "isy.sicherheit.nutzerauthentifizierung.benutzer.testBenutzer.kennung=testbenutzer",
    "isy.sicherheit.nutzerauthentizifierung.benutzer.testBenutzer.passwort=passwort",
    "isy.sicherheit.nutzerauthentizifierung.benutzer.testBenutzer.bhknz=123456" })
public class NutzerAuthentifizierungInterceptorTest {

    @Autowired
    private ServiceImpl testBean;

    @Autowired
    private SicherheitStub sicherheitStub;

    @Autowired
    private NutzerAuthentifizierungInterceptor<AufrufKontextImpl> interceptor;

    @Before
    public void setUp() {
        this.sicherheitStub.reset();
    }

    @Test
    public void testPositiv_gesichertDurch_RechtA() {
        testBean.methodeGesichertDurchNutzerAnnotation();
        assertNotNull(sicherheitStub.getLetzterAufrufKontext());
        assertEquals("testbenutzer", sicherheitStub.getLetzterAufrufKontext().getDurchfuehrenderBenutzerKennung());
    }

    @Test(expected = AnnotationFehltRuntimeException.class)
    public void testNegativ_gesichertDurchNichts() throws Throwable {
        SimpleMethodInvocation invocation =
            new SimpleMethodInvocation(testBean, ServiceImpl.class.getMethod("nichtGesichert"));
        interceptor.invoke(invocation);
    }

}
