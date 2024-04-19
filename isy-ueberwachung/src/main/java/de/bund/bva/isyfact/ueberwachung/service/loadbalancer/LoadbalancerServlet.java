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
package de.bund.bva.isyfact.ueberwachung.service.loadbalancer;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.ueberwachung.common.konstanten.EreignisSchluessel;

/**
 * Servlet zur Steuerung des Loadbalancings einer Webanwendungen. Der Loadbalancer kann die URL des Servlets
 * regelmäßig abfragen. Das Servlet liefert HTTP OK, falls die IsAlive-Datei gefunden wurde. Falls nicht wird
 * HTTP FORBIDDEN an den aufrufenden Loadbalancer gemeldet. Der Loadbalancer verteilt dann keine Anfragen an
 * die Webanwendung mehr. Der Pfad zur Loadbalancer-Datei kann bei Bedarf &uuml;ber den Init-Parameter
 * {@link #PARAM_IS_ALIVE_FILE_LOCATION} angegeben werden. Ist der Parameter nicht gesetzt, wird der
 * Default-Wert {@link #DEFAULT_IS_ALIVE_FILE_LOCATION} verwendet.
 *
 */
public class LoadbalancerServlet extends HttpServlet {
    /** UID der Klasse. */
    private static final long serialVersionUID = 7248576003928677600L;

    /** Logger der Klasse. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(LoadbalancerServlet.class);

    /** Parametername fuer den Pfad zur IsAlive-Datei. */
    private static final String PARAM_IS_ALIVE_FILE_LOCATION = "isAliveFileLocation";

    /** Standard Ablageort der IsAlive-Datei. */
    private static final String DEFAULT_IS_ALIVE_FILE_LOCATION = "/WEB-INF/classes/config/isAlive";

    /**
     * File-Referenz für IsAlive-Datei.
     */
    private static File isAliveFile;

    /**
     * Initialisiert das Servlet.
     */
    @Override
    public void init() {
        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001, "Initialisiere Loadbalancer-Servlet.");

        String isAliveFileLocation = getInitParameter(PARAM_IS_ALIVE_FILE_LOCATION);
        if (isAliveFileLocation == null) {
            LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001,
                "Position der IsAliveDatei nicht konfiguriert. Verwende Standard-Einstellung: {}",
                DEFAULT_IS_ALIVE_FILE_LOCATION);
            isAliveFileLocation = DEFAULT_IS_ALIVE_FILE_LOCATION;
        }
        String realIsAliveFilePath = getServletContext().getRealPath(isAliveFileLocation);
        isAliveFile = new File(realIsAliveFilePath);

        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001, "IsAlive-Datei {} konfiguriert.",
            isAliveFile.getAbsolutePath());
    }

    /** GET-Request bearbeiten. Prüft, ob die IsAlive-Datei vorhanden ist und liefert dann HTTP OK zurück.
     * Andernfalls wird HTTP FORBIDDEN zurückgeliefert.
     *
     * @param req
     *            Der HttpServletRequest an das Loadbalancer-Servlet.
     * @param resp
     *            Die Antwort des Loadbalancer-Servlets.
     *
     * @throws IOException
     *             Wenn die Antwort nicht geschrieben werden kann.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (isAliveFile.exists()) {
                LOG.debug("IsAlive-Datei gefunden, sende HTTP OK.");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("<html><body><center>IS ALIVE!</center></body></html>");
            } else {
                LOG.error(EreignisSchluessel.IS_ALIVE_EXISTIERT_NICHT,
                        "IsAlive-Datei {} existiert nicht, sende HTTP FORBIDDEN.",
                        isAliveFile.getAbsolutePath());
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (IOException e) {
            LOG.error(EreignisSchluessel.IS_ALIVE_EXISTIERT_IO_EXCEPTION,
                    "IsyAlive-Datei {} existiert, Fehler beim Schreiben der Antwort in Output-Stream",
                    e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
