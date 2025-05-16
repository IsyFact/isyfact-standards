package de.bund.bva.isyfact.batchrahmen.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper-Klasse um Protokoll-Dateien zu parsen und auf bestimmte Kriterien und Fehlerkennungen hin zu prüfen.
 * Stellt Funktionen bereit um Batchprotokolle auf Fehlerfreiheit oder das Vorkommen von bestimmenten Fehlern
 * zu prüfen. Daneben können Statistikwerte abgefragt werden.
 * 
 * 
 */
public class BatchProtokollTester {
    Logger logger = LoggerFactory.getLogger(BatchProtokollTester.class);
    /** Das BatchProtokoll Document. **/
    private Document batchProtokoll;

    /** Das XPath-Object. **/
    private XPath xpath;

    /**
     * Konstruktor. Parst die Eingabe-Datei.
     * @param ergebnisDatei
     *            Das zu überprüfende Batchprotokoll.
     */
    public BatchProtokollTester(String ergebnisDatei) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            docFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            docFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            builder = docFactory.newDocumentBuilder();
            batchProtokoll = builder.parse(ergebnisDatei);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            xpath = xpathFactory.newXPath();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Error while creating BatchProtokollTester", e);
        }
    }

    /**
     * Überprüft ob das Batchprotokoll mindestens eine Meldung mit der angegebenen ID enthält.
     * @param id
     *            Die gesucht MeldungsID
     * @return true/false
     */
    public boolean enthaeltMeldungsId(String id) {
        String xpathQuery = "//Meldung[@ID='" + id + "']";
        return getNodeListFromXpath(xpathQuery).getLength() != 0;
    }

    /**
     * Liefert zurück ob das Batchprotokoll Fehler enthält.
     * @return true/false
     */
    public boolean isFehlerfrei() {
        String xpathQuery = "//Meldung[@Typ='F']";
        return getNodeListFromXpath(xpathQuery).getLength() == 0;
    }

    /**
     * Liefert zurück ob die übergebene FehlerID min. einmal im Protokoll vorkommt.
     * @param fehlerId
     *            Die gesuchte FehlerId
     * @return true/false
     */
    public boolean enthaeltFehler(String fehlerId) {
        String xpathQuery = "//Meldung[@Typ='F' and @ID='" + fehlerId + "']";
        return getNodeListFromXpath(xpathQuery).getLength() != 0;
    }

    /**
     * Liefert zurück ob ein Fehler mit der angegebenen ID im Protokoll vorkommt, dessen Fehlertext den
     * gesuchten Wortteil enthält. Nützlich um parametrisierte Fehlermeldungen auf Ausprägungen zu überprüfen.
     * 
     * @param fehlerId
     *            FehlerID des gesuchten Fehlers
     * @param textTeil
     *            Der gesuchte Text im Fehlertext
     * @return true/false
     */
    public boolean enthaeltFehler(String fehlerId, String textTeil) {
        String xpathQuery = "//Meldung[@Typ='F' and @ID='" + fehlerId + "' and contains(@Text, '" + textTeil + "')]";
        return getNodeListFromXpath(xpathQuery).getLength() != 0;
    }

    /**
     * Liefert alle FehlerIDs zurück (inklusive Duplikaten).
     * @return Liste der FehlerIDs als Strings.
     */
    public Collection<String> getFehlerIds() {
        ArrayList<String> fehlerListe = new ArrayList<>();
        String xpathQuery = "//Meldung[@Typ='F']";
        NodeList nodes = getNodeListFromXpath(xpathQuery);
        if (nodes.getLength() != 0) {
            for (int i = 0; i < nodes.getLength(); i++) {
                NamedNodeMap attributMap = nodes.item(i).getAttributes();
                fehlerListe.add(attributMap.getNamedItem("ID").getNodeValue());
            }
        }
        return fehlerListe;
    }

    /**
     * Liefert alle eindeutigen FehlerIDs zurück (ohne Duplikate).
     * @return Menge der eindeutigen FehlerIDs
     */
    public Set<String> getFehlerIdsEindeutig() {
        HashSet<String> fehlerIdsEindeutig = new HashSet<>();
        for (String fehlerId : getFehlerIds()) {
            if (!fehlerIdsEindeutig.contains(fehlerId)) {
                fehlerIdsEindeutig.add(fehlerId);
            }
        }
        return fehlerIdsEindeutig;
    }

    /**
     * Überprüft ob das Batchprotokoll ausgibt, dass der Batch im Restart-Modus gestartet wurde.
     * @return <code>true</code> wenn der Batch im Modus Restart gestartet wurde, ansonsten <code>false</code>
     * 
     */
    public boolean isStartmodusRestart() {
        String xpathQuery = "//Meldung[@ID='RESTART']";
        return getNodeListFromXpath(xpathQuery).getLength() != 0;
    }

    /**
     * Liefert die Anzahl Fehler zurück.
     * @return Anzahl Fehler.
     */
    public int getAnzahlFehler() {
        return getFehlerIds().size();
    }

    /**
     * Liefert die Anzahl Fehler zurück.
     * @param id
     *            Die ID des gesuchten Fehlers.
     * @return Anzahl Fehler.
     */
    public int getAnzahlFehler(String id) {
        String xpathQuery = "//Meldung[@Typ='F' and @ID='" + id + "']";
        return getNodeListFromXpath(xpathQuery).getLength();
    }

    /**
     * Liefert die Anzahl der eindeutigen FehlerIDs zurück.
     * @return Anzahl Fehler ohne Duplikate
     */
    public int getAnzahlFehlerEindeutig() {
        return getFehlerIdsEindeutig().size();
    }

    /**
     * Liest einen Statistikwert aus dem Protokoll.
     * @param id
     *            Die ID des Statistik-Eintrags.
     * @return Wert des Statistikeintrags. -1 wenn der Statistikeintrag unbekannt ist.
     */
    public int getStatistikwert(String id) {
        String xpathQuery = "//Statistik-Eintrag[@ID='" + id + "']";
        if (getNodeListFromXpath(xpathQuery).getLength() != 1) {
            return -1;
        } else {
            NodeList nodes = getNodeListFromXpath(xpathQuery);
            NamedNodeMap attributMap = nodes.item(0).getAttributes();
            return Integer.parseInt(attributMap.getNamedItem("Wert").getNodeValue());
        }
    }

    /**
     * Führt einen XPath-Query auf dem Protokoll aus und liefert die Ergebnisse als NodeList zurück.
     * @param xpathQuery
     *            Der auszuführende XPath-Query.
     * @return Die NodeList der Ergebnisse
     */
    public NodeList getNodeListFromXpath(String xpathQuery) {
        try {
            XPathExpression expr = xpath.compile(xpathQuery);
            return (NodeList) expr.evaluate(batchProtokoll, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
