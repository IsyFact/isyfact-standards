package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.message.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import de.bund.bva.isyfact.logging.util.LogHelperRest;
import de.bund.bva.isyfact.serviceapi.core.rest.message.IsyLoggingMessageBody;

import ch.qos.logback.classic.Level;

/**
 * Testet den {@link IsyLoggingRestOutInterceptor}.
 */
public class IsyLoggingRestOutInterceptorTest extends AbstractInterceptorTest {

    @InjectMocks
    private IsyLoggingRestOutInterceptor interceptor;

    @Captor
    private ArgumentCaptor<String> logMessageCaptor = ArgumentCaptor.forClass(String.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        setLoggingLevel(Level.INFO);
        setRequestor(true);
    }

    /**
     * Eine leere Nachricht wird ohne Fehler behandelt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testEmptyMessage() throws Exception {
        setRequestor(false);
        this.interceptor.handleMessage(this.message);
        setRequestor(true);
        this.interceptor.handleMessage(this.message);
    }

    /**
     * Header und Body werden geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testHeaderUndBodyGeloggt() throws Exception {
        executeOutAsRequestorAndRequestee(TEST_DATA_XML);

        verify(this.logHelper, times(2)).logHeaderOut(any(), any(), any());
        verify(this.logHelper, times(2)).logBodyOut(any(), contains(TEST_DATA_XML), any());
        verify(this.logHelper, times(2)).logBodyOutForce(any(), any(), any());
    }

    /**
     * Bei Konfiguration {@link LogHelperRest#setLoggeDaten(boolean)} wird nur der Header, nicht der Body
     * geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testHeaderUndNichtBodyGeloggt() throws Exception {
        this.message.put(Message.ENCODING, "UTF-8");
        this.message.put(Message.CONTENT_TYPE, MediaType.APPLICATION_XML);
        Mockito.doCallRealMethod().when(this.logHelper).setLoggeDaten(eq(false));
        Mockito.doCallRealMethod().when(this.logHelper).logBodyOut(any(), any(), any());
        this.logHelper.setLoggeDaten(false);
        prepareOutputStreamContent();

        this.interceptor.handleMessage(this.message);
        closeOutputStreamContent();

        verify(this.logHelper, times(1)).logHeaderOut(any(), any(), any());
        verify(this.logHelper, times(1)).logBodyOut(any(), any(), any());
        verify(this.logHelper, never()).logBodyOutForce(any(), any(), any());
    }

    /**
     * Bei {@link Level#WARN} wird weder Header noch Body geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testNoMessageLogLevelWarn() throws Exception {
        setLoggingLevel(Level.WARN);
        prepareOutputStreamContent();

        this.interceptor.handleMessage(this.message);
        closeOutputStreamContent();

        verify(this.logHelper, never()).logHeaderOut(any(), any(), any());
        verify(this.logHelper, never()).logBodyOut(any(), any(), any());
    }

    /**
     * Bei einem leeren Body wird eine entsprechender Hinweis geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testNoContentMessage() throws Exception {
        executeOutAsRequestorAndRequestee();

        IsyLoggingMessageBody messageBody = new IsyLoggingMessageBody();
        messageBody.getMessage().append(IsyLoggingMessageBody.EMPTY_BODY);
        verify(this.logHelper, times(2)).logBodyOut(any(), eq(messageBody.toString()), any());
    }

    /**
     * Falls kein Binary-Content geloggt werden soll, die Nachricht aber Binary-Content enthält, wird ein
     * Hinweis geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testShowNoBinaryContent() throws Exception {
        this.interceptor.setShowBinaryContent(false);
        this.message.put(Message.CONTENT_TYPE, "image/png");

        executeOutAsRequestorAndRequestee(TEST_DATA_BINARY);

        verify(this.logHelper, times(2)).logBodyOut(any(), contains("Binary Content"), any());
    }

    /**
     * Falls Binary-Content geloggt werden soll und die Nachricht Binary-Content enthält, wird der Content
     * geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testShowBinaryContent() throws Exception {
        this.interceptor.setShowBinaryContent(true);
        this.message.put(Message.CONTENT_TYPE, "image/png");

        executeOutAsRequestorAndRequestee(TEST_DATA_BINARY);

        verify(this.logHelper, times(2)).logBodyOut(any(), contains(TEST_DATA_BINARY), any());
    }

    /**
     * Falls kein Multipart-Content geloggt werden soll, aber die Nachricht Multipart-Content enthält, wird
     * ein Hinweis geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testShowNoMultipartContent() throws Exception {
        this.interceptor.setShowMultipartContent(false);
        this.message.put(Message.CONTENT_TYPE, "multipart/xml");

        executeOutAsRequestorAndRequestee(TEST_DATA_XML);

        verify(this.logHelper, times(2)).logBodyOut(any(), contains("Multipart Content"), any());
    }

    /**
     * Falls Multipart-Content geloggt werden soll und die Nachricht Multipart-Content enthält, wird der
     * Content geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testShowMultipartContent() throws Exception {
        this.interceptor.setShowMultipartContent(true);
        this.message.put(Message.CONTENT_TYPE, "multipart/xml");

        executeOutAsRequestorAndRequestee(TEST_DATA_XML);

        verify(this.logHelper, times(2)).logBodyOut(any(), contains(TEST_DATA_XML), any());
    }

    /**
     * Falls die Lognachricht die Limitierung überschreitet, wird die Lognachricht abgeschintten mit einem
     * Hinweis abgeschnitten.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testLimitedLogMessage() throws Exception {
        int limit = 500;
        String content = RandomStringUtils.randomAlphanumeric(limit * 2);
        this.interceptor.setLimit(limit);
        prepareOutputStreamContent();

        this.interceptor.handleMessage(this.message);
        closeOutputStreamContent(content);

        verify(this.logHelper, times(1)).logBodyOut(any(), this.logMessageCaptor.capture(), any());
        // Extrahiere die "echte" Payload (ohne den Hinweis).
        int payloadStart = this.logMessageCaptor.getValue().indexOf(content.substring(0, 10));
        String payload = this.logMessageCaptor.getValue().substring(payloadStart);
        assertThat(payload.getBytes().length, is(limit));
    }

    private void executeOutAsRequestorAndRequestee() throws IOException {
        executeOutAsRequestorAndRequestee("");
    }

    private void executeOutAsRequestorAndRequestee(String content) throws IOException {
        setRequestor(true);
        prepareOutputStreamContent();
        this.interceptor.handleMessage(this.message);
        closeOutputStreamContent(content);

        cleanInterceptor();

        setRequestor(false);
        prepareOutputStreamContent();
        this.interceptor.handleMessage(this.message);
        closeOutputStreamContent(content);
    }
}
