package de.bund.bva.isyfact.bridges.integration.server;

import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.testing.ServletTester;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Diese Klasse implementiert einen Server, der einen einfachen HttpInvoker-Service anbietet.
 * Der Service implementiert das Interface {@link DummyServiceRemoteBean}.
 *
 *
 */
public class IsyTestServer {
    /** Pfad, unter dem der Service angeboten wird. */
    private static final String TEST_SERVICE_BEAN_PATH = "/TestService";

    /** Url des Services.*/
    private String serviceUrl;

    /** Servlet-Container für den DummyService. */
    private ServletTester servletTester;

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
    public IsyTestServer() throws Exception {
        servletTester = new ServletTester();
        servletTester.setContextPath("/");
        servletTester.setResourceBase(getBaseUrl());
        ServletHolder holder = servletTester.addServlet(DispatcherServlet.class, TEST_SERVICE_BEAN_PATH);
        holder.setInitParameter("contextConfigLocation", "remoting-servlet.xml");
        serviceUrl = servletTester.createSocketConnector(false) + TEST_SERVICE_BEAN_PATH;
        servletTester.start();
    }

    /**
     * Stoppt den Server.
     * @throws Exception
     */
    public void stop() throws Exception {
        servletTester.stop();
    }

    /**
     * Liefert das Feld 'serviceUrl' zurück.
     * @return Wert von serviceUrl
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

}