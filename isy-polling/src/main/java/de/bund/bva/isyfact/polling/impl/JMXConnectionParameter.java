package de.bund.bva.isyfact.polling.impl;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import de.bund.bva.isyfact.polling.common.exception.PollingClusterKonfigurationException;
import de.bund.bva.isyfact.polling.common.konstanten.Fehlerschluessel;

/**
 * Diese Klasse enthält die Informationen zu einer JMX-Connection.
 * 
 */
class JMXConnectionParameter {

    /** ID des JMXConnectionParameter-Objekts. */
    private String id;
    
    /** IP-Adresse mit Portangabe. */
    private String ipAdressePort;
    
    /** JMX-Service-URL. */
    private JMXServiceURL jmxServiceUrl;
    
    /** Environment mit Benutzerkennung und Kennwort. */ 
    private Map<String, Object> environment;

    /**
     * Erzeugt neue JMX-Connection-Parameter.
     * 
     * @param id
     *          ID des JMXConnectionParameter-Objekts.
     * @param host
     *          Host.
     * @param port
     *          Port.
     * @param benutzerId
     *          Benutzer-Id.
     * @param passwort
     *          Passwort.
     */
    JMXConnectionParameter(String id, String host, String port, String benutzerId, String passwort)  {
        this.id = id;

        ipAdressePort = host + ":" + port;
        String url = "service:jmx:rmi:///jndi/rmi://" + ipAdressePort + "/jmxrmi";
        try {
            jmxServiceUrl = new JMXServiceURL(url);
        } catch (MalformedURLException e) {
            throw new PollingClusterKonfigurationException(
                Fehlerschluessel.MSG_JMX_IPADRESSEPORT_FEHLERHAFT, url, id);
        }

        if (benutzerId != null) {
            environment = new HashMap<String, Object>();
            environment.put(JMXConnector.CREDENTIALS, new String[] {benutzerId, passwort});
        }
    }
    
    /**
     * Liefert das Feld 'id' zurück.
     * @return Wert von id
     */
    String getId() {
        return id;
    }

    /**
     * Liefert das Feld 'jmxServiceUrl' zurück.
     * @return Wert von jmxServiceUrl
     */
    JMXServiceURL getJmxServiceUrl() {
        return jmxServiceUrl;
    }

    /**
     * Liefert das Feld 'ipAdressePort' zurück.
     * @return Wert von ipAdressePort
     */
    String getIpAdressePort() {
        return ipAdressePort;
    }
    
    /**
     * Liefert das Environment mit Benutzerkennung und Kennwort. 
     * @return Environment.
     */
    Map<String, ?> getEnvironment() {
        return environment;
    }

}
