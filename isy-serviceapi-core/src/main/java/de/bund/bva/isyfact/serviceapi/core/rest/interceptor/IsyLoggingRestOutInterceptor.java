package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_EMPFANGSZEITPUNKT;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_SENDEZEITPUNKT;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_STORED_LOGGER;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_STORED_LOG_MESSAGE;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_SYSTEMNAME;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_SYSTEMNAME_REMOTE;

import java.io.OutputStream;
import java.util.Optional;

import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Required;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.LogHelperRest;
import de.bund.bva.isyfact.serviceapi.common.util.IsyHttpUtils;
import de.bund.bva.isyfact.serviceapi.core.rest.message.IsyLoggingMessageBody;
import de.bund.bva.isyfact.serviceapi.core.rest.message.IsyLoggingMessageHeader;
import de.bund.bva.isyfact.serviceapi.core.rest.message.IsyLoggingMessageUtil;

/**
 * Isy-Interceptor zum Loggen der Nachrichten bei ausgehenden REST-Nachrichten.
 */
public class IsyLoggingRestOutInterceptor extends LoggingOutInterceptor {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IsyLoggingRestOutInterceptor.class);

    private static final String LOG_SETUP = IsyLoggingRestOutInterceptor.class.getName() + ".log-setup";

    private LogHelperRest logHelperRest = new LogHelperRest(true, false, false);

    /** Verbessert die Lesbarkeit der Logausgaben in Console-Logs. Default-Wert: false. */
    private boolean minifyBody = false;

    /** Name des aufrufenden Systems. */
    private String systemname;

    public IsyLoggingRestOutInterceptor() {
        this(Phase.PRE_STREAM);
    }

    public IsyLoggingRestOutInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(Message message) {
        if (isRequestor(message)) {
            handleMessageAsRequestor(message);
        } else {
            handleZwischengespeicherteLogMessage(message);
            handleMessageAsRequestee(message);
        }
        handleMessageCommon(message);
    }

    /**
     * Setze das aufrufende System in den Header und halte den Aufrufzeitpunkt des Services fest. Das
     * aufgerufene System, das vom Anwendungscode in den Header gesetzt wurde, wird ebenfalls festgehalten.
     * @param message
     *            Ausgehender Request von diesem Service an einen anderen Service.
     */
    protected void handleMessageAsRequestor(Message message) {
        // Setze aufrufendes System in Header.
        IsyHttpUtils.setProtocolHeader(message, KEY_SYSTEMNAME, this.systemname);
        // Setze aufgerufenes System in Exchange.
        String aufgerufenesSystem = IsyHttpUtils.getProtocolHeader(message, KEY_SYSTEMNAME_REMOTE);
        message.getExchange().put(KEY_SYSTEMNAME_REMOTE, aufgerufenesSystem);
        // Setze Aufrufzeitpunkt in Exchange.
        long startzeit = this.logHelperRest.ermittleAktuellenZeitpunkt();
        message.getExchange().put(KEY_SENDEZEITPUNKT, startzeit);
    }

    /**
     * Die Lognachricht des Body des eingegangen Requests wird gegebenenfalls nur bei einem internen Fehler
     * geloggt.
     * @param message
     *            Aktuell ausgehende Response, ggfs. mit einer Lognachricht im Exchange.
     */
    private void handleZwischengespeicherteLogMessage(Message message) {
        boolean istFehler = IsyHttpUtils.istInternerServerFehler(message);
        boolean istNachrichtVorhanden = IsyHttpUtils.istZwischengespeicherteNachrichtVorhanden(message);
        if (istFehler && istNachrichtVorhanden) {
            String inBody = (String) message.getExchange().remove(KEY_STORED_LOG_MESSAGE);
            IsyLogger inLogger = (IsyLogger) message.getExchange().remove(KEY_STORED_LOGGER);
            this.logHelperRest.logBodyInForce(inLogger, inBody, message.getExchange().getInMessage());
        }
    }

    /**
     * Logge die Dauer der Verarbeitung des eingegangenen Requests durch den Service vor dem Versenden der
     * Response.
     * @param message
     *            Ausgehendede Response auf verarbeiteten Request.
     */
    protected void handleMessageAsRequestee(Message message) {
        // Lese Empfangszeitpunkt und logge Dauer.
        Long startzeit = (Long) message.getExchange().get(KEY_EMPFANGSZEITPUNKT);
        startzeit = Optional.ofNullable(startzeit).orElse(0L);
        long endzeit = this.logHelperRest.ermittleAktuellenZeitpunkt();
        long dauer = endzeit - startzeit;
        String aufrufendesSystem = (String) message.getExchange().get(KEY_SYSTEMNAME);
        aufrufendesSystem = Optional.ofNullable(aufrufendesSystem).orElse("aufrufendes System");
        this.logHelperRest.logDauerVerarbeitung(LOG, aufrufendesSystem, dauer, message);
    }

    /**
     * Logge die relevanten technischen Informationen im Header der Nachricht und den Body der Nachricht als
     * fachliche Daten.
     * @param message
     *            Eingehende oder ausgehende Nachricht.
     */
    protected void handleMessageCommon(Message message) {
        if (LOG.isInfoEnabled()) {
            final OutputStream os = message.getContent(OutputStream.class);
            CachedOutputStream cos = initializeCachedOutputStream(message);
            if (cos != null) {
                cos.registerCallback(new IsyLoggingCallback(message, os));
            }
        }
    }

    /**
     * Zum Loggen des Body muessen alle Daten, die auf den {@link OutputStream} in der ausgehenden Nachricht
     * geschrieben werden, erfasst werden. Dazu wird der originalle {@link OutputStream} durch
     * {@link CacheAndWriteOutputStream} ersetzt. Der {@link CacheAndWriteOutputStream} speichert die Daten
     * und leitet sie gleichzeitig in den originallen {@link OutputStream} weiter. Nachdem alle Daten
     * geschrieben wurden, wird die Nachricht durch einen Callback auf {@link CacheAndWriteOutputStream}
     * geloggt und der originalle {@link OutputStream} wieder in die Nachricht eingesetzt.
     * @param message
     *            Ausgehende Nachricht.
     */
    private CachedOutputStream initializeCachedOutputStream(Message message) {
        final OutputStream os = message.getContent(OutputStream.class);
        if (os == null) {
            return null;
        }
        // Write the output while caching it for the log message.
        boolean hasLogged = message.containsKey(LOG_SETUP);
        if (!hasLogged) {
            message.put(LOG_SETUP, Boolean.TRUE);
            final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
            if (this.threshold > 0) {
                newOut.setThreshold(this.threshold);
            }
            if (this.limit > 0) {
                newOut.setCacheLimit(this.limit);
            }
            message.setContent(OutputStream.class, newOut);
            return newOut;
        }
        return null;
    }

    /**
     * Loggt den Header der {@link Message} in einem lesbaren Format.
     * @param message
     *            Ausgehende Nachricht.
     */
    protected void logHeader(Message message) {
        IsyLoggingMessageHeader buffer = IsyLoggingMessageUtil.createForHeaderOut(message);
        String header = formatLoggingMessageHeader(buffer);
        this.logHelperRest.logHeaderOut(LOG, header, message);
    }

    /**
     * Formattiert die technischen Informationen.
     * @param buffer
     *            Extrahierte technische Informationen.
     * @return Formattierte technische Informationen.
     */
    protected String formatLoggingMessageHeader(IsyLoggingMessageHeader buffer) {
        return buffer.toString();
    }

    /**
     * Loggt den Body im Callback.
     * @param message
     *            Ausgehende Nachricht.
     * @param cos
     *            Stream mit dem Body der ausgehenden Nachricht.
     */
    protected void logBody(Message message, CachedOutputStream cos) {
        IsyLoggingMessageBody buffer = prepareAndWritePayload(message, cos);
        String logmessage = formatLoggingMessageBody(buffer);
        this.logHelperRest.logBodyOut(LOG, logmessage, message);
    }

    protected IsyLoggingMessageBody prepareAndWritePayload(Message message, CachedOutputStream cos) {
        IsyLoggingMessageBody buffer = new IsyLoggingMessageBody();

        // Behandle Nachrichten mit Binaer- oder Multipart-Content.
        String ct = (String) message.get(Message.CONTENT_TYPE);
        if (!isShowBinaryContent() && isBinaryContent(ct)) {
            buffer.getMessage().append(BINARY_CONTENT_MESSAGE);
            return buffer;
        }
        if (!isShowMultipartContent() && isMultipartContent(ct)) {
            buffer.getMessage().append(MULTIPART_CONTENT_MESSAGE);
            return buffer;
        }

        // Truncate den Content ueber dem Limit.
        int lim = IsyLoggingRestOutInterceptor.this.limit == -1 ? Integer.MAX_VALUE
            : IsyLoggingRestOutInterceptor.this.limit;
        if (cos.size() >= lim) {
            buffer.getMessage().append("Outbound Message: ");
            buffer.getMessage().append("(message truncated to " + lim + " bytes).");
        }

        try {
            String encoding = (String) message.get(Message.ENCODING);
            writePayload(buffer.getPayload(), cos, encoding, ct);
        } catch (Exception ex) {
            buffer.getPayload().append("Nachricht konnte nicht geschrieben werden.").append(" Ursache: ")
                .append(ex.getMessage());
        }
        return buffer;
    }

    /**
     * Formattiert die fachlichen Body-Informationen.
     * @param buffer
     *            Extrahierte fachlichen Body.
     * @return Formattierter fachlicher-Body.
     */
    protected String formatLoggingMessageBody(IsyLoggingMessageBody buffer) {
        return buffer.toString(this.minifyBody && !this.prettyLogging);
    }

    /**
     * Schliesst den Stream-in-the-Middle und ersetzt ihn durch den original OutputStream in der ausgehenden
     * Nachricht, als hätte er niemals existiert.
     * @param message
     *            Ausgehende Nachricht.
     * @param cos
     *            Stream zum Caching der ausgehenden Payload.
     * @param originalOutputStream
     *            Original OutputStream, der vom {@link CachedOutputStream} gefüllt wurde.
     */
    protected void finalizeCachedOutputStream(Message message, CachedOutputStream cos,
        OutputStream originalOutputStream) {
        try {
            // empty out the cache
            cos.lockOutputStream();
            cos.resetOut(null, false);
        } catch (Exception ex) {
            // ignore
        }
        message.setContent(OutputStream.class, originalOutputStream);
    }

    /**
     * Loggt die Nachricht wenn der Stream geschlossen wird.
     * @see LoggingCallback in {@link LoggingOutInterceptor}.
     */
    class IsyLoggingCallback implements CachedOutputStreamCallback {

        private final Message message;

        private final OutputStream originalOutputStream;

        public IsyLoggingCallback(final Message msg, final OutputStream os) {
            this.message = msg;
            this.originalOutputStream = os;
        }

        @Override
        public void onFlush(CachedOutputStream cos) {
            // ignore
        }

        @Override
        public void onClose(CachedOutputStream cos) {
            logHeader(this.message);
            // TODO Der Body wird aufbereitet auch wenn der LogHelper ihn nicht schreiben wird.
            logBody(this.message, cos);
            finalizeCachedOutputStream(this.message, cos, this.originalOutputStream);
        }
    }

    public void setLogHelperRest(LogHelperRest logHelperRest) {
        this.logHelperRest = logHelperRest;
    }

    public void setMinifyBody(boolean minifyBody) {
        this.minifyBody = minifyBody;
    }

    @Required
    public void setSystemname(String systemName) {
        this.systemname = systemName;
    }

    /**
     * Limit der Payload in Byte. Die Lognachricht ist größer als die Payload!
     */
    @Override
    public void setLimit(int lim) {
        super.setLimit(lim);
    }
}
