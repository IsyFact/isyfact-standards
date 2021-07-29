package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_EMPFANGSZEITPUNKT;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_SENDEZEITPUNKT;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_STORED_LOGGER;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_STORED_LOG_MESSAGE;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_SYSTEMNAME;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_SYSTEMNAME_REMOTE;

import java.io.InputStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.Optional;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.DelegatingInputStream;
import org.apache.cxf.message.Message;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.LogHelperRest;
import de.bund.bva.isyfact.serviceapi.common.util.IsyHttpUtils;
import de.bund.bva.isyfact.serviceapi.core.rest.message.IsyLoggingMessageBody;
import de.bund.bva.isyfact.serviceapi.core.rest.message.IsyLoggingMessageHeader;
import de.bund.bva.isyfact.serviceapi.core.rest.message.IsyLoggingMessageUtil;

/**
 * Isy-Interceptor zum Loggen der Nachrichten bei eingehenden REST-Nachrichten.
 */
public class IsyLoggingRestInInterceptor extends LoggingInInterceptor {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IsyLoggingRestInInterceptor.class);

    private LogHelperRest logHelperRest = new LogHelperRest(true, false, false);

    /** Verbessert die Lesbarkeit der Logausgaben in Console-Logs. Default-Wert: false. */
    private boolean minifyBody = false;

    @Override
    public void handleMessage(Message message) {
        if (isRequestor(message)) {
            handleZwischengespeicherteLogMessage(message);
            handleMessageAsRequestor(message);
        } else {
            handleMessageAsRequestee(message);
        }
        handleMessageCommon(message);
    }

    /**
     * Die Lognachricht des Body des ausgegangenen Requests wird gegebenenfalls nur bei einem internen Fehler
     * geloggt.
     * @param message
     *            Aktuell eingehende Response, ggfs. mit einer Lognachricht im Exchange.
     */
    private void handleZwischengespeicherteLogMessage(Message message) {
        boolean istFehler = IsyHttpUtils.istInternerServerFehler(message);
        boolean istNachrichtVorhanden = IsyHttpUtils.istZwischengespeicherteNachrichtVorhanden(message);
        if (istFehler && istNachrichtVorhanden) {
            String outBody = (String) message.getExchange().remove(KEY_STORED_LOG_MESSAGE);
            IsyLogger outLogger = (IsyLogger) message.getExchange().remove(KEY_STORED_LOGGER);
            this.logHelperRest.logBodyOutForce(outLogger, outBody, message.getExchange().getInMessage());
        }
    }

    /**
     * Logge die Aufrufdauer bis zum Erhalt einer Response von dem aufgerufenen System.
     * @param message
     *            Eingehende Response auf einen Request dieses Services.
     */
    protected void handleMessageAsRequestor(Message message) {
        // Logge Dauer der Verarbeitung des Requests in anderem Service.
        Long startzeit = (Long) message.getExchange().get(KEY_SENDEZEITPUNKT);
        startzeit = Optional.ofNullable(startzeit).orElse(0L);
        long endezeit = this.logHelperRest.ermittleAktuellenZeitpunkt();
        long dauer = endezeit - startzeit;
        String aufgerufenesSystem = (String) message.getExchange().get(KEY_SYSTEMNAME_REMOTE);
        aufgerufenesSystem = Optional.ofNullable(aufgerufenesSystem).orElse("aufgerufenem System");
        this.logHelperRest.logDauerAufruf(LOG, aufgerufenesSystem, dauer, message);
    }

    /**
     * Setze und halte das aufrufende System und den Empfangszeitpunkt eines Requests fest.
     * @param message
     *            Eingehender Request eines anderen Services.
     */
    protected void handleMessageAsRequestee(Message message) {
        // Das aufrufende System wird im Header geloggt.
        // Halte das aufrufende System zusaetzlich fest.
        String aufrufendesSystem = IsyHttpUtils.getProtocolHeader(message, KEY_SYSTEMNAME);
        message.getExchange().put(KEY_SYSTEMNAME, aufrufendesSystem);

        // Halte Empfangszeitpunkt fest.
        long startzeit = this.logHelperRest.ermittleAktuellenZeitpunkt();
        message.getExchange().put(KEY_EMPFANGSZEITPUNKT, startzeit);
    }

    /**
     * Logge die relevanten technischen Informationen im Header der Nachricht und den Body der Nachricht als
     * fachliche Daten.
     * @param message
     *            Eingehende oder ausgehende Nachricht.
     */
    protected void handleMessageCommon(Message message) {
        if (LOG.isInfoEnabled()) {
            // Logge relevante Header-Attribute.
            String header = leseHeader(message);
            this.logHelperRest.logHeaderIn(LOG, header, message);

            // Logge Body.
            // TODO Der Body wird aufbereitet auch wenn der LogHelper ihn nicht schreiben wird.
            String body = leseBody(message);
            this.logHelperRest.logBodyIn(LOG, body, message);
        }
    }

    protected String leseHeader(Message message) {
        IsyLoggingMessageHeader buffer = IsyLoggingMessageUtil.createForHeaderIn(message);
        return formatLoggingMessageHeader(buffer);
    }

    protected String formatLoggingMessageHeader(IsyLoggingMessageHeader buffer) {
        return buffer.toString();
    }

    /**
     * Liest den Body der {@link Message}. Basiert auf {@link LoggingInInterceptor#logging}.
     * @param message
     *            Eingehende oder ausgehende Nachricht.
     * @return Formattierte Payload.
     */
    protected String leseBody(Message message) {
        IsyLoggingMessageBody buffer = new IsyLoggingMessageBody();
        String encoding = (String) message.get(Message.ENCODING);
        String ct = (String) message.get(Message.CONTENT_TYPE);

        if (!isShowBinaryContent() && isBinaryContent(ct)) {
            buffer.getMessage().append(BINARY_CONTENT_MESSAGE);
        } else if (!isShowMultipartContent() && isMultipartContent(ct)) {
            buffer.getMessage().append(MULTIPART_CONTENT_MESSAGE);
        } else {
            InputStream is = message.getContent(InputStream.class);
            if (is != null) {
                logInputStream(message, is, buffer.getDelegate(), encoding, ct);
            } else {
                Reader reader = message.getContent(Reader.class);
                if (reader != null) {
                    logReader(message, reader, buffer.getDelegate());
                }
            }
        }

        return formatLoggingMessageBody(buffer);
    }

    /**
     * Überschreibt {@link LoggingInInterceptor}, damit die Truncate-Message gesetzt wird. In der normalen
     * Implementierung wird die Message unter Umständen nicht erreicht, obwohl das Limit überschritten wurde.
     */
    @Override
    protected void logInputStream(Message message, InputStream is, LoggingMessage buffer, String encoding,
        String ct) {
        CachedOutputStream bos = new CachedOutputStream();
        if (this.threshold > 0) {
            bos.setThreshold(this.threshold);
        }
        try {
            // use the appropriate input stream and restore it later
            InputStream bis =
                is instanceof DelegatingInputStream ? ((DelegatingInputStream) is).getInputStream() : is;

            // only copy up to the limit since that's all we need to log
            // we can stream the rest
            IOUtils.copyAtLeast(bis, bos, this.limit == -1 ? Integer.MAX_VALUE : this.limit);
            int remainingByte = bis.available(); // Es wird die verbleibende Groesse festgehalten.
            bos.flush();
            bis = new SequenceInputStream(bos.getInputStream(), bis);

            // restore the delegating input stream or the input stream
            if (is instanceof DelegatingInputStream) {
                ((DelegatingInputStream) is).setInputStream(bis);
            } else {
                message.setContent(InputStream.class, bis);
            }

            if (bos.getTempFile() != null) {
                // large thing on disk...
                buffer.getMessage().append("\nMessage (saved to tmp file):\n");
                buffer.getMessage().append("Filename: " + bos.getTempFile().getAbsolutePath() + "\n");
            }
            // Der OutputStream wird mit mindestens der Menge an Bytes des Limits befuellt. Es kann auch
            // genau die Menge an Bytes wie das Limit in den OutputStream kopiert werden. Der normale Check
            // wuerde diesen Fall nicht feststellen und faelschlicherweise keine Truncate-Hinweis erstellen.
            if ((bos.size() > this.limit || remainingByte > 0) && this.limit != -1) {
                buffer.getMessage().append("(message truncated to " + this.limit + " bytes)\n");
            }
            writePayload(buffer.getPayload(), bos, encoding, ct);

            bos.close();
        } catch (Exception e) {
            throw new Fault(e);
        }
    }

    protected String formatLoggingMessageBody(IsyLoggingMessageBody buffer) {
        return buffer.toString(this.minifyBody && !this.prettyLogging);
    }

    public void setLogHelperRest(LogHelperRest logHelperRest) {
        this.logHelperRest = logHelperRest;
    }

    public void setMinifyBody(boolean minifyBody) {
        this.minifyBody = minifyBody;
    }
}
