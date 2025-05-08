package de.bund.bva.isyfact.sicherheit.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
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
 * Diese Klasse ließt ein RollenRechte-XML ein und gibt es aus.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class XmlAccess {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(XmlAccess.class);

    /**
     * In einem ersten Schritt wird eine Übersicht über alle Rechte erstellt.
     */
    private HashMap<String, Recht> rechtIdZuRecht = new HashMap<String, Recht>();

    /**
     * Name des XML-Elementes in Recht über das die Properties angesprochen werden.
     */
    private static final String PROPERTIES = "properties";

    /**
     * Name des XML-Attributes der Id einer Rolle.
     */
    private static final String PROPERTY_NAME = "PropertyName";

    /**
     * Name des XML-Attributes der Id einer Rolle.
     */
    private static final String PROPERTY_VALUE = "PropertyValue";

    /**
     * Name des XML-Elementes in Recht über das die RechtId angesprochen wird.
     */
    private static final String RECHT_ID = "rechtId";

    /**
     * Name des XML-Attributes der Id einer RechteId.
     */
    private static final String ID_IN_RECHTE_ID = "Id";

    /**
     * Name des XML-Attributes der Id einer Rolle.
     */
    private static final String ROLLE_ID = "RolleId";

    /**
     * Name des XML-Attributes des Namen einer Rolle.
     */
    private static final String ROLLE_NAME = "RolleName";

    /**
     * Name der XML-Elementes über die Rollen von einer Anwendung aus angesprochen werden.
     */
    private static final String ROLLEN = "rollen";

    /**
     * Name der XML-Elementes über die Rechte von einer Anwendung aus angesprochen werden.
     */
    private static final String RECHTE = "rechte";

    /**
     * Name des XML-Attributes der Id einer Anwendung.
     */
    private static final String ANWENDUNGS_ID = "AnwendungsId";

    /**
     * Parst die Datei an dem übergebenen Pfad und macht daraus ein RollenRechteMapping mit dem ein
     * Berechtigungsmanager über das Mapping von Rollen zu Rechten innerhalbe einer Anwendung informiert wird.
     *
     * @param filename
     *            Vollständiger Pfad zur Datei mit den Rechten
     * @return Die eingelesene Datei als Rollen zu Rechte Mapping
     * @throws RollenRechteMappingException
     *             Bei allen Verarbeitungsfehlern
     */
    public RollenRechteMapping parseRollenRechteFile(String filename) {
        LOG.debug("Lese Rollen-Rechte-Mapping aus {}.", filename);
        Document dom;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setExpandEntityReferences(false);
        try (InputStream stream = XmlAccess.class.getResourceAsStream(filename)) {
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
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
        }
    }

    /**
     * In dieser Methode wird die AnwendungsId aus dem Document extrahiert und ins Mapping gesetzt. Das Parse
     * der Mappinginformationen von Rollen zu Rechten wird aufgerufen.
     *
     * @param doc
     *            Das geparste Document
     * @return Das vollständige RollenRechteMapping
     * @throws RollenRechteMappingException
     *             Falls die AnwendungsId fehlt oder es bei den aufgerufenen Methoden zu Fehlern kommt
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
     * Durchläuft alle Nodes und ruft für jedes Recht die Methode leseRecht auf. Alle gelesenen Rechte werden
     * dann in der HashMap rechtIdZuRecht gesetzt.
     *
     * @param anwendung
     *            Das Basis-Element der eingelesenen XML-Datein
     * @throws RollenRechteMappingException
     *             Wenn es beim lesen eines Rechtes zu Fehlern kommt
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
     * Wandelt ein Element das ein Recht hat in eine Instanz des Interfaces Recht um.
     *
     * @param recht
     *            Element das ein Recht ist
     * @return Das Transofmierte Recht
     * @throws RollenRechteMappingException
     *             Wenn das Recht nicht den Erwartungen entspricht
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
     * Liest alle definierten Rollen aus speichert für diese die RollenId und ruft jeweils das Mapping aller
     * Rechte für diese Rolle auf.
     *
     * @param anwendung
     *            Das Top-Element des RollenRechteMapping
     * @return Eine HashMaps mit Rechten zu Rollen
     * @throws RollenRechteMappingException
     *             Falls bei einem Eintrag die RollenId fehlt, oder keine Rolle definiert wurde
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
     * Liest aus dem Element einer Rolle alle Rechte aus.
     *
     * @param rolle
     *            Das Element für eine Rolle
     * @return Eine Liste aller Rechte für diese Rolle
     * @throws RollenRechteMappingException
     *             Falls eine unbekannte RechtId gefunden wird
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
