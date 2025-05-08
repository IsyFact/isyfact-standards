package de.bund.bva.isyfact.batchrahmen.core.protokoll;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.StatistikEintrag;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenProtokollException;
import de.bund.bva.isyfact.batchrahmen.core.konstanten.NachrichtenSchluessel;

/**
 * Erzeugt eine XML-Darstellung für ein {@link BatchErgebnisProtokoll}.
 * 
 * 
 */
public class XmlProtokollGenerator implements ProtokollGenerator {

    private static final String ENCODING_UTF_8 = "UTF-8";

    /** Attribut-Name. */
    private static final String ATTR_DATUM = "Datum";

    /** Attribut-Name. */
    private static final String ATTR_BATCH_ID = "BatchID";

    /**
     * Attribut-TYP "CDATA" ({@link Attributes}).
     */
    private static final String ATTR_TYPE_CDATA = "CDATA";

    /** Attribut-Name. */
    private static final String ATTR_FACHLICHE_ID = "FachlicheID";

    /** Attribut-Name. */
    private static final String ATTR_MELDUNG = "Meldung";

    /** Attribut-Name. */
    private static final String ATTR_WERT = "Wert";

    /** Attribut-Name. */
    private static final String ATTR_TEXT = "Text";

    /** Attribut-Name. */
    private static final String ATTR_TYP = "Typ";

    /** Attribut-Name. */
    private static final String ATTR_ID = "ID";

    /** Attribut-Name. */
    private static final String ATTR_UHRZEIT = "Uhrzeit";

    /** Attribut-Name. */
    private static final String ATTR_PARAMETER = "Parameter";

    /** Element-Name. */
    private static final String ELEM_STATISTIK_EINTRAG = "Statistik-Eintrag";

    /** Element-Name. */
    private static final String ELEM_STATISTIK = "Statistik";

    /** Element-Name. */
    private static final String ELEM_RETURN_CODE = "Return-Code";

    /** Element-Name. */
    private static final String ELEM_BATCH_START = "Start";

    /** Element-Name. */
    private static final String ELEM_BATCH_ENDE = "Ende";

    /** Element-Name. */
    private static final String ELEM_BATCH_ERGEBNIS = "Batch-Ergebnis";

    /** Element-Name. */
    private static final String ELEM_MELDUNGEN = "Meldungen";

    /** Handler zum XML-Schreiben. **/
    private TransformerHandler handler;
    
    /** Der Outputstream zur Datei. **/
    private Writer writer;

    /**
     * Konstruktor. Setzt den Outputstream.
     * @param outStream
     *            OutputStream
     * @exception SAXException
     *                Probleme beim XML parsen
     * @exception TransformerConfigurationException
     *                Probleme beim XML parsen
     * @throws UnsupportedEncodingException 
     *      Falls das UTF-8 Encoding nicht bekannt ist.
     */
    public XmlProtokollGenerator(OutputStream outStream) throws SAXException,
        TransformerConfigurationException, UnsupportedEncodingException {
        super();
        SAXTransformerFactory transformerFactory =
            (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        handler = transformerFactory.newTransformerHandler();
        Transformer serializer = handler.getTransformer();
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        // Eignenen Writer setzen, auf dem Flush aufgerufen werden kann.
        // Flush für Outputstream führt leider nicht dazu, dass Ausgaben direkt geschrieben werden.
        this.writer = new OutputStreamWriter(outStream, ENCODING_UTF_8);
        handler.setResult(new StreamResult(writer));
        handler.startDocument();
        handler.startElement("", "", ELEM_BATCH_ERGEBNIS, null);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void flusheOutput() {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, e);
        }
    }

    /**
     * Erzeugt Header-Element mit BatchID, Startzeit/Datum sowie Angabe der Parameter (Key/Value).
     * 
     * @param protokoll
     *            Das BatchProtokoll
     */
    public void erzeugeStartInfoElement(BatchErgebnisProtokoll protokoll) {
        SimpleDateFormat uhrzeitFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat datumFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Start-Tag
        String parameters = "";
        for (String parameter : protokoll.getParameter()) {
            parameters += parameter + " ";
        }
        parameters = parameters.substring(0, parameters.length() - 1);
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", "", ATTR_BATCH_ID, "", nullSafeGet(protokoll.getBatchId()));
        atts.addAttribute("", "", ATTR_DATUM, "", datumFormat.format((protokoll.getStartDatum())));
        atts.addAttribute("", "", ATTR_UHRZEIT, "", uhrzeitFormat.format((protokoll.getStartDatum())));
        atts.addAttribute("", "", ATTR_BATCH_ID, "", protokoll.getBatchId());
        atts.addAttribute("", "", ATTR_PARAMETER, "", parameters);
        try {
            handler.startElement("", "", ELEM_BATCH_START, atts);
            handler.endElement("", "", ELEM_BATCH_START);

            // Öffnendes Meldungen-Tag
            handler.startElement("", "", ELEM_MELDUNGEN, null);
        } catch (SAXException e) {
            throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, e);
        }
    }

    /**
     * Erzeugt Header-Element mit BatchID, Startzeit/Datum sowie Angabe der Parameter (Key/Value).
     * 
     * @param protokoll
     *            Das BatchProtokoll
     */
    public void erzeugeEndeInfoElement(BatchErgebnisProtokoll protokoll) {
        SimpleDateFormat uhrzeitFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat datumFormat = new SimpleDateFormat("yyyy-MM-dd");

        // End-Tag
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", "", ATTR_DATUM, "", datumFormat.format((protokoll.getEndeDatum())));
        atts.addAttribute("", "", ATTR_UHRZEIT, "", uhrzeitFormat.format((protokoll.getEndeDatum())));
        try {
            handler.startElement("", "", ELEM_BATCH_ENDE, atts);
            handler.endElement("", "", ELEM_BATCH_ENDE);
        } catch (SAXException e) {
            throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, e);
        }
    }

    /**
     * Erzeugt den Abschnitt, der alle Meldungen ({@link VerarbeitungsMeldung}) des Protokolls enthält.
     * @param meldung
     *            Die zu verarbeitende Meldung.
     */
    public void erzeugeMeldung(VerarbeitungsMeldung meldung) {       
        AttributesImpl atts = new AttributesImpl();
        atts.clear();
        atts.addAttribute("", "", ATTR_ID, ATTR_TYPE_CDATA, nullSafeGet(meldung.getId()));
        if (meldung.getFachlicheId() != null) {
            atts.addAttribute("", "", ATTR_FACHLICHE_ID, ATTR_TYPE_CDATA, nullSafeGet(meldung
                .getFachlicheId()));
        }
        atts.addAttribute("", "", ATTR_TYP, ATTR_TYPE_CDATA, nullSafeGet(meldung.getTyp().getKuerzel()));
        atts.addAttribute("", "", ATTR_TEXT, ATTR_TYPE_CDATA, nullSafeGet(meldung.getText()));
        try {
            handler.startElement("", "", ATTR_MELDUNG, atts);
            handler.endElement("", "", ATTR_MELDUNG);
        } catch (SAXException e) {
            throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, e);
        }
    }

    /**
     * Erzeugt den Abschnitt, der die Statistiken ({@link StatistikEintrag}) des Protokolls enthält.
     * @param protokoll
     *            Das ErgebnisProtokoll.
     */
    public void erzeugeStatistik(BatchErgebnisProtokoll protokoll) {
        try {
            handler.endElement("", "", ELEM_MELDUNGEN);
            handler.startElement("", "", ELEM_STATISTIK, null);
            AttributesImpl atts = new AttributesImpl();
            for (StatistikEintrag statistikEintrag : protokoll.getStatistikEintraege()) {
                atts.clear();
                atts.addAttribute("", "", ATTR_ID, ATTR_TYPE_CDATA, nullSafeGet(statistikEintrag.getId()));
                atts
                    .addAttribute("", "", ATTR_TEXT, ATTR_TYPE_CDATA, nullSafeGet(statistikEintrag.getText()));
                atts.addAttribute("", "", ATTR_WERT, ATTR_TYPE_CDATA, Integer.toString(statistikEintrag
                    .getWert()));
                handler.startElement("", "", ELEM_STATISTIK_EINTRAG, atts);
                handler.endElement("", "", ELEM_STATISTIK_EINTRAG);
            }
            handler.endElement("", "", ELEM_STATISTIK);
        } catch (SAXException e) {
            throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, e);
        }
    }

    /**
     * Erzeugt den Abschnitt, der den ReturnCode beschreibt ({@link BatchReturnCode}) des Protokolls enthält.
     * @param protokoll
     *            Das ErgebnisProtokoll.
     */
    public void erzeugeReturnCodeElement(BatchErgebnisProtokoll protokoll) {
        try {
            AttributesImpl atts = new AttributesImpl();
            atts.addAttribute("", "", "RC", ATTR_TYPE_CDATA, Integer.toString(protokoll.getReturnCode()
                .getWert()));
            atts.addAttribute("", "", ATTR_TEXT, ATTR_TYPE_CDATA, nullSafeGet(protokoll.getReturnCode()
                .getText()));
            handler.startElement("", "", ELEM_RETURN_CODE, atts);
            handler.endElement("", "", ELEM_RETURN_CODE);
        } catch (SAXException e) {
            throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, e);
        }
    }

    /**
     * Liefert "" zurück, falls der angegebene String <code>null</code> ist. Ansonsten wird der String direkt
     * zurückgegeben;
     * @param value
     *            String
     * @return Den String oder "".
     */
    private static String nullSafeGet(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    /**
     * Schliesst das Schreiben der Protokolldatei ab.
     */
    public void close() {
        try {
            handler.endElement("", "", ELEM_BATCH_ERGEBNIS);
            handler.endDocument();
            writer.close();
        } catch (Throwable t) {
            throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, t);
        }
    }
}
