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
package test.de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import java.io.InterruptedIOException;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

import de.bund.bva.pliscommon.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.pliscommon.serviceapi.core.httpinvoker.TimeoutWiederholungHttpInvokerRequestExecutor;

import junit.framework.TestCase;
import test.de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.DummyServiceImpl;
import test.de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.DummyServiceRemoteBean;

public class TimeoutWiederholungTest extends TestCase {
    private DummyServer dummyServer;
    private DummyServiceImpl dummyService;
    private String serviceUrl;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        dummyServer = new DummyServer();
        serviceUrl = dummyServer.getServiceUrl();
        dummyService = dummyServer.getDummyService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        Thread.sleep(1000);
        dummyServer.stop();
    }
    
    public DummyServiceRemoteBean createHttpInvokerProxy(SimpleHttpInvokerRequestExecutor simpleHttpInvokerRequestExecutor) {
        HttpInvokerProxyFactoryBean proxyFactory = new HttpInvokerProxyFactoryBean();
        proxyFactory.setHttpInvokerRequestExecutor(simpleHttpInvokerRequestExecutor);
        proxyFactory.setServiceUrl(serviceUrl);
        proxyFactory.setServiceInterface(DummyServiceRemoteBean.class);
        proxyFactory.afterPropertiesSet();
        return (DummyServiceRemoteBean) proxyFactory.getObject();
    }    

    public void testTimeoutKurz() {
        dummyService.setWaitTime(0);
        TimeoutWiederholungHttpInvokerRequestExecutor timeoutExecutor =
                new TimeoutWiederholungHttpInvokerRequestExecutor(new AufrufKontextVerwalterStub<>());
        timeoutExecutor.setTimeout(100);

        DummyServiceRemoteBean dummyServiceRemoteBean = createHttpInvokerProxy(timeoutExecutor);
        assertEquals("Hello", dummyServiceRemoteBean.ping("Hello"));
        assertEquals(1, dummyService.getAnzahlAufrufe());
    }
    
    public void testTimeoutLang() {
        dummyService.setWaitTime(30000);
        TimeoutWiederholungHttpInvokerRequestExecutor timeoutExecutor =
                new TimeoutWiederholungHttpInvokerRequestExecutor(new AufrufKontextVerwalterStub<>());
        timeoutExecutor.setTimeout(60000);

        DummyServiceRemoteBean dummyServiceRemoteBean = createHttpInvokerProxy(timeoutExecutor);
        assertEquals("Hello", dummyServiceRemoteBean.ping("Hello"));
        assertEquals(1, dummyService.getAnzahlAufrufe());        
    }    
    
    public void testAufrufWiederholung() {
        dummyService.setWaitTime(200);
        TimeoutWiederholungHttpInvokerRequestExecutor timeoutExecutor =
                new TimeoutWiederholungHttpInvokerRequestExecutor(new AufrufKontextVerwalterStub<>());
        timeoutExecutor.setTimeout(50);
        timeoutExecutor.setWiederholungenAbstand(500);
        timeoutExecutor.setAnzahlWiederholungen(10);

        long t0 = System.currentTimeMillis();
        try {
            DummyServiceRemoteBean dummyServiceRemoteBean = createHttpInvokerProxy(timeoutExecutor);
            dummyServiceRemoteBean.ping("Hello");      
            fail("Exception erwartet");
        } catch (Throwable t) {
            long t1 = System.currentTimeMillis();
            assertTrue(InterruptedIOException.class.isAssignableFrom(t.getCause().getClass()));
            assertEquals(10, dummyService.getAnzahlAufrufe());
            assertTrue("Wiederholungspausen wurde nicht eingehalten: " + (t1 - t0), (t1 - t0) > 4000);
        }
    }

}
