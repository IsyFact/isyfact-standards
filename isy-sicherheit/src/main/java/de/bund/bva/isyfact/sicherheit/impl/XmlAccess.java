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
package de.bund.bva.isyfact.sicherheit.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.bund.bva.isyfact.sicherheit.Rolle;
import de.bund.bva.isyfact.sicherheit.common.exception.RollenRechteMappingException;
import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.sicherheit.Recht;

/**
 * This class reads a RoleRights XML and outputs it.
 *
 */
public class XmlAccess {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(XmlAccess.class);

    /**
     * In a first step, an overview of all rights is created.
     */
    private HashMap<String, Recht> rechtIdZuRecht = new HashMap<String, Recht>();

    /**
     * Name of the XML element in Recht over which the properties are addressed.
     */
    private static final String PROPERTIES = "properties";

    /**
     * Name of the XML attribute of the ID of a role.
     */
    private static final String PROPERTY_NAME = "PropertyName";

    /**
     * Name of the XML attribute of the value of a property.
     */
    private static final String PROPERTY_VALUE = "PropertyValue";

    /**
     * Name of the XML element in Recht over which the RechtId is addressed.
     */
    private static final String RECHT_ID = "rechtId";

    /**
     * Name of the XML attribute of the ID of a RechteId.
     */
    private static final String ID_IN_RECHTE_ID = "Id";

    /**
     * Name of the XML attribute of the ID of a role.
     */
    private static final String ROLLE_ID = "RolleId";

    /**
     * Name of the XML attribute of the name of a role.
     */
    private static final String ROLLE_NAME = "RolleName";

    /**
     * Name of the XML element over which the roles of an application are addressed.
     */
    private static final String ROLLEN = "rollen";

    /**
     * Name of the XML element over which the rights of an application are addressed.
     */
    private static final String RECHTE = "rechte";

    /**
     * Name of the XML attribute of the ID of an application.
     */
    private static final String ANWENDUNGS_ID = "AnwendungsId";

    /**
     * Parses the file at the given path and turns it into a RoleRightsMapping which informs a
     * Berechtigungsmanager about the mapping of roles to rights within an application.
     *
     * @param filename
     *            Full path to the file with the rights
     * @return The read file as Role to Rights Mapping
     * @throws RollenRechteMappingException
     *             For all processing errors
     */
    public RollenRechteMapping parseRollenRechteFile(String filename) {
        LOG.debug("Lese Rollen-Rechte-Mapping aus {}.", filename);
        Document dom;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        InputStream stream = XmlAccess.class.getResourceAsStream(filename);
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            dom = documentBuilder.parse(stream);
            RollenRechteMapping result = parseDocument(dom);
            result.setAlleDefiniertenRechte(this.rechtIdZuRecht.values());
            return result;
        } catch (ParserConfigurationException pce) {
            throw new RollenRechteMappingException(
                    SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT, pce,
                    "XML-Parser Fehler");
        } catch (SAXException se) {
            throw new RollenRechteMappingException(
                    SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT, se,
                    "SAX-Exception");
        } catch (IOException ioe) {
            throw new RollenRechteMappingException(
                    SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT, ioe,
                    "IO-Exception");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // Log the exception instead of throwing it.
                    LOG.error("Failed to close the InputStream", String.valueOf(e));
                }
            }
        }
    }


    /**
     * In this method, the application ID is extracted from the Document and set into the mapping. The parse
     * of the mapping information from roles to rights is called.
     *
     * @param doc
     *            The parsed Document
     * @return The complete RoleRightsMapping
     * @throws RollenRechteMappingException
     *             If the application ID is missing or if errors occur in the called methods
     */
    private RollenRechteMapping parseDocument(Document doc) {
        RollenRechteMapping ergebnis = new RollenRechteMapping();
        Element anwendung = doc.getDocumentElement();
        String anwendungsId = anwendung.getAttribute(ANWENDUNGS_ID);
        if (anwendungsId.equals("")) {
            throw new RollenRechteMappingException(
                SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT,
                "Es ist keine AnwendungsId gesetzt");
        }
        erstelleRechteUebersicht(anwendung);
        ergebnis.setAnwendungsId(anwendungsId);
        ergebnis.setRollenRechteMapping(getRollenRechtMapping(anwendung));
        return ergebnis;
    }

    /**
     * Goes through all nodes and calls the method readRecht for each right. All read rights are
     * then set in the HashMap rechtIdZuRecht.
     *
     * @param anwendung
     *            The base element of the read XML files
     * @throws RollenRechteMappingException
     *             If an error occurs when reading a right
     */
    private void erstelleRechteUebersicht(Element anwendung) {
        Node child = anwendung.getFirstChild();
        while (child != null) {
            if (child.getNodeName().contains(RECHTE)) {
                Element recht = (Element) child;
                Recht rechtObjekt = leseRecht(recht);
                this.rechtIdZuRecht.put(rechtObjekt.getId(), rechtObjekt);
            }
            child = child.getNextSibling();
        }
    }

    /**
     * Converts an element that has a right into an instance of the Recht interface.
     *
     * @param recht
     *            Element that is a right
     * @return The transformed right
     * @throws RollenRechteMappingException
     *             If the right does not meet expectations
     */
    private Recht leseRecht(Element recht) {
        String rechtId = null;
        HashMap<String, Object> properties = new HashMap<String, Object>();
        Node child = recht.getFirstChild();
        while (child != null) {
            if (child.getNodeName().contains(RECHT_ID)) {
                if (rechtId != null) {
                    throw new RollenRechteMappingException(
                        SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT,
                        "Es wurde eine Rolle mit zwei Referenzen auf RechteIds gefunden");
                }
                Element rechtIdElement = (Element) child;
                rechtId = rechtIdElement.getAttribute(ID_IN_RECHTE_ID);
                if (rechtId == null || rechtId.equals("")) {
                    throw new RollenRechteMappingException(
                        SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT,
                        "Es wurde eine RechtId ohne Id gefunden");
                }
            }
            if (child.getNodeName().contains(PROPERTIES)) {
                Element property = (Element) child;
                String propertyName = property.getAttribute(PROPERTY_NAME);
                if (propertyName == null || propertyName.equals("")) {
                    throw new RollenRechteMappingException(
                        SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT,
                        "Es wurde ein Property ohne Name gefunden");
                }
                String propertyValue = property.getAttribute(PROPERTY_VALUE);
                if (propertyValue == null || propertyValue.equals("")) {
                    throw new RollenRechteMappingException(
                        SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT,
                        "Es wurde ein Property ohne Value gefunden");
                }
                properties.put(propertyName, propertyValue);
            }

            child = child.getNextSibling();
        }
        if (rechtId == null) {
            throw new RollenRechteMappingException(
                SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT,
                "Recht ohne RechtId gefunden");
        }
        if (properties.size() == 0) {
            return new RechtImpl(rechtId, null);
        }
        return new RechtImpl(rechtId, properties);
    }

    /**
     * Reads all defined roles from and saves for these the role ID and calls the mapping of all
     * rights for this role.
     *
     * @param anwendung
     *            The top element of the RoleRightsMapping
     * @return A HashMap with rights to roles
     * @throws RollenRechteMappingException
     *             If an entry is missing the role ID, or no role was defined
     */
    private HashMap<Rolle, List<Recht>> getRollenRechtMapping(Element anwendung) {
        boolean mindestensEineRolleGefunden = false;
        HashMap<Rolle, List<Recht>> rollenRechte = new HashMap<Rolle, List<Recht>>();
        Node child = anwendung.getFirstChild();
        while (child != null) {
            if (child.getNodeName().contains(ROLLEN)) {
                mindestensEineRolleGefunden = true;
                Element rolle = (Element) child;
                String rolleId = rolle.getAttribute(ROLLE_ID);
                if (rolleId == null || rolleId.equals("")) {
                    throw new RollenRechteMappingException(
                        SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT,
                        "Id der Rolle fehlt");
                }
                String rolleName = rolle.getAttribute(ROLLE_NAME);
                RolleImpl neueRolle = new RolleImpl(rolleId, rolleName);
                List<Recht> rechte = getRechteFuerRolle(rolle);
                rollenRechte.put(neueRolle, rechte);
            }
            child = child.getNextSibling();
        }
        if (!mindestensEineRolleGefunden) {
            throw new RollenRechteMappingException(
                SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT,
                "Es wurde keine einzige Rolle gefunden");
        }
        return rollenRechte;
    }

    /**
     * Reads from the role element all the rights.
     *
     * @param rolle
     *            The element for a role
     * @return A list of all rights for this role
     * @throws RollenRechteMappingException
     *             If an unknown RechtId is found
     */
    private List<Recht> getRechteFuerRolle(Element rolle) {
        List<Recht> alleRechte = new ArrayList<Recht>();
        Node child = rolle.getFirstChild();
        while (child != null) {
            if (child.getNodeName().contains(RECHT_ID)) {
                Element rechtId = (Element) child;
                String idVonRechtId = rechtId.getAttribute(ID_IN_RECHTE_ID);
                if (idVonRechtId.equals("")) {
                    throw new RollenRechteMappingException(
                        SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT,
                        "Recht hat keine Id");
                }
                Recht neuesRecht = this.rechtIdZuRecht.get(idVonRechtId);
                if (neuesRecht == null) {
                    throw new RollenRechteMappingException(
                        SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_ROLLENRECHTEMAPPING_FEHLERHAFT,
                        "Das Recht mit Id: " + idVonRechtId + " ist unbekannt");
                }
                alleRechte.add(neuesRecht);
            }
            child = child.getNextSibling();
        }

        return alleRechte;
    }
}
