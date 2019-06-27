package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_STORED_LOGGER;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_STORED_LOG_MESSAGE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.util.LogHelperRest;

import ch.qos.logback.classic.Level;

/**
 * Beinhaltet Utility-Methoden zur Vereinfachung der Tests der Logging-Interceptoren.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractInterceptorTest {

    @Spy
    protected LogHelperRest logHelper;

    protected static final String TEST_DATA_BINARY =
        "01010000 01100001 01111001 01101100 01101111 01100001 01100100";

    protected static final String TEST_DATA_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    protected Message message;

    public void setUp() throws Exception {
        this.message = new MessageImpl();
        this.message.put(Message.PROTOCOL_HEADERS, new MetadataMap<>());
        this.message.setExchange(new ExchangeImpl());

        this.logHelper.setLoggeDaten(true);
        this.logHelper.setLoggeDauer(true);
        this.logHelper.setLoggeDatenBeiServerFehler(true);
    }

    protected static void setLoggingLevel(Level level) {
        Arrays.stream(new Class[] { IsyLoggingRestInAuthInterceptor.class, IsyLoggingRestInInterceptor.class,
            IsyLoggingRestOutInterceptor.class }).forEach(l -> setLoggingLevel(l, level));
    }

    protected static void setLoggingLevel(Class<?> clazz, Level level) {
        ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(clazz)).setLevel(level);
    }

    protected void setRequestor(boolean flag) {
        this.message.put(Message.REQUESTOR_ROLE, Boolean.valueOf(flag));
    }

    protected void storeLogMessage(IsyLogger logger, String logMessage) {
        Exchange ex = this.message.getExchange();
        ex.put(KEY_STORED_LOG_MESSAGE, logMessage);
        ex.put(KEY_STORED_LOGGER, logger);
        ex.setInMessage(new MessageImpl());
    }

    protected void storeLogMessage(String logger, String logMessage) {
        Exchange ex = this.message.getExchange();
        ex.put(KEY_STORED_LOG_MESSAGE, logMessage);
        ex.put(KEY_STORED_LOGGER, logger);
    }

    protected void assertHasStoredLogMessage() {
        Exchange ex = this.message.getExchange();
        assertThat(ex.get(KEY_STORED_LOG_MESSAGE), is(notNullValue()));
        assertThat(ex.get(KEY_STORED_LOGGER), is(instanceOf(IsyLogger.class)));
    }

    /**
     * Setzt den Schutz gegen doppeltes Schreiben einer Nachricht zurück.
     */
    protected void cleanInterceptor() {
        this.message.values().removeIf(o -> o instanceof Boolean);
    }

    protected void setInputStreamContent(String content) {
        InputStream is = new ByteArrayInputStream(content.getBytes());
        this.message.setContent(InputStream.class, is);
        this.message.putIfAbsent(Message.ENCODING, "UTF-8");
        this.message.putIfAbsent(Message.CONTENT_TYPE, MediaType.APPLICATION_XML);
    }

    protected void prepareOutputStreamContent() {
        OutputStream os = new ByteArrayOutputStream();
        this.message.setContent(OutputStream.class, os);
        this.message.putIfAbsent(Message.ENCODING, "UTF-8");
        this.message.putIfAbsent(Message.CONTENT_TYPE, MediaType.APPLICATION_XML);
    }

    protected void closeOutputStreamContent(String content) throws IOException {
        OutputStream os = this.message.getContent(OutputStream.class);
        os.write(content.getBytes());
        os.close();
    }

    protected void closeOutputStreamContent() throws IOException {
        OutputStream os = this.message.getContent(OutputStream.class);
        os.close();
    }

    protected void loggeNurBeiException() {
        this.logHelper.setLoggeDauer(false);
        this.logHelper.setLoggeDaten(false);
        this.logHelper.setLoggeDatenBeiServerFehler(true);
    }
}
