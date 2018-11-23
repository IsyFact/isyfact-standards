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


import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletTester;
import org.springframework.web.servlet.DispatcherServlet;

import test.de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.DummyServiceImpl;
import test.de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.DummyServiceRemoteBean;

/**
 * Diese Klasse implementiert einen Server, der einen einfachen HttpInvoker-Service anbietet.
 * Der Service implementiert das Interface {@link DummyServiceRemoteBean}.
 * 
 *
 */
class DummyServer {
    /** Pfad, unter dem der Service angeboten wird. */
    private static final String DUMMY_SERVICE_BEAN_PATH = "/dummyServiceBean_v1_0_0";

    /** Url des Services.*/
    private String serviceUrl;

    /** Servlet-Container für den DummyService. */
    private ServletTester servletTester;

    /** Implementierung des Services. */
    private DummyServiceImpl dummyService;
    
    /**
     * Erzeugt die BaseUrl an Hand der Datei remoting-servlet.xml
     * @return Die BaseUrl mit den Servlet-Konfigurationen.
     */
    private String getBaseUrl() {
        String baseUrl = this.getClass().getResource("/remoting-servlet.xml").toExternalForm();
        int i = baseUrl.lastIndexOf("/");
        return baseUrl.substring(0, i);
    }

    /**
     * Erzeugt und startet den Server.
     * @throws Exception Wenn der Service nicht gestartet werden kann.
     */
    DummyServer() throws Exception {
        servletTester = new ServletTester();
        servletTester.setContextPath("/");
        servletTester.setResourceBase(getBaseUrl());
        ServletHolder holder = servletTester.addServlet(DispatcherServlet.class, DUMMY_SERVICE_BEAN_PATH);
        holder.setInitParameter("contextConfigLocation", "remoting-servlet.xml");
        serviceUrl = servletTester.createConnector(false) + DUMMY_SERVICE_BEAN_PATH;
        servletTester.start();
        dummyService =
            (DummyServiceImpl) ((DispatcherServlet) holder.getServlet()).getWebApplicationContext().getBean(
                "dummyService");
    }

    /**
     * Stoppt den Server.
     * @throws Exception
     */
    void stop() throws Exception {
        servletTester.stop();
    }

    /**
     * Liefert das Feld 'serviceUrl' zurück.
     * @return Wert von serviceUrl
     */
    String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * Liefert das Feld 'dummyService' zurück.
     * @return Wert von dummyService
     */
    DummyServiceImpl getDummyService() {
        return dummyService;
    }
}
