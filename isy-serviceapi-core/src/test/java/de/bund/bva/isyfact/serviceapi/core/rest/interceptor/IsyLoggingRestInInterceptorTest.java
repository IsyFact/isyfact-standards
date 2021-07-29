package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
 * Testet den {@link IsyLoggingRestInInterceptor}.
 */
public class IsyLoggingRestInInterceptorTest extends AbstractInterceptorTest {

    @InjectMocks
    private IsyLoggingRestInInterceptor interceptor;

    @Captor
    private ArgumentCaptor<String> logMessageCaptor = ArgumentCaptor.forClass(String.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        setLoggingLevel(Level.INFO);
        setRequestor(false);
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
        setRequestor(false);
        setInputStreamContent(TEST_DATA_XML);
        this.interceptor.handleMessage(this.message);

        setRequestor(true);
        setInputStreamContent(TEST_DATA_XML);
        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(2)).logHeaderIn(any(), any(), any());
        verify(this.logHelper, times(2)).logBodyIn(any(), any(), any());
        verify(this.logHelper, times(2)).logBodyInForce(any(), any(), any());
    }

    /**
     * Bei Konfiguration {@link LogHelperRest#setLoggeDaten(boolean)} wird nur der Header, nicht der Body
     * geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testHeaderUndKeinBodyGeloggt() throws Exception {
        this.message.put(Message.ENCODING, "UTF-8");
        this.message.put(Message.CONTENT_TYPE, MediaType.APPLICATION_XML);
        Mockito.doCallRealMethod().when(this.logHelper).setLoggeDaten(eq(false));
        Mockito.doCallRealMethod().when(this.logHelper).logBodyIn(any(), any(), any());
        this.logHelper.setLoggeDaten(false);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(1)).logHeaderIn(any(), any(), any());
        verify(this.logHelper, times(1)).logBodyIn(any(), any(), any());
        verify(this.logHelper, never()).logBodyInForce(any(), any(), any());
    }

    /**
     * Bei {@link Level#WARN} wird weder Header noch Body geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testNoMessageLogLevelWarn() throws Exception {
        setLoggingLevel(Level.WARN);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, never()).logHeaderIn(any(), any(), any());
        verify(this.logHelper, never()).logBodyIn(any(), any(), any());
    }

    /**
     * Bei einem leeren Body wird eine entsprechender Hinweis geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testNoContentMessage() throws Exception {
        setRequestor(false);
        this.interceptor.handleMessage(this.message);

        setRequestor(true);
        this.interceptor.handleMessage(this.message);

        IsyLoggingMessageBody messageBody = new IsyLoggingMessageBody();
        messageBody.getMessage().append(IsyLoggingMessageBody.EMPTY_BODY);
        verify(this.logHelper, times(2)).logBodyIn(any(), eq(messageBody.toString()), any());
    }

    /**
     * Falls kein Binary-Content geloggt werden soll, die Nachricht aber Binary-Content enthält, wird ein
     * Hinweis geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testShowNoBinaryContent() throws Exception {
        this.message.put(Message.CONTENT_TYPE, "image/png");
        setInputStreamContent(TEST_DATA_BINARY);
        this.interceptor.setShowBinaryContent(false);

        setRequestor(false);
        this.interceptor.handleMessage(this.message);

        setRequestor(true);
        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(2)).logBodyIn(any(), contains("Binary Content"), any());
    }

    /**
     * Falls Binary-Content geloggt werden soll und die Nachricht Binary-Content enthält, wird der Content
     * geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testShowBinaryContent() throws Exception {
        this.message.put(Message.CONTENT_TYPE, "image/png");
        setInputStreamContent(TEST_DATA_BINARY);
        this.interceptor.setShowBinaryContent(true);

        setRequestor(false);
        this.interceptor.handleMessage(this.message);

        setRequestor(true);
        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(2)).logBodyIn(any(), contains(TEST_DATA_BINARY), any());
    }

    /**
     * Falls kein Multipart-Content geloggt werden soll, aber die Nachricht Multipart-Content enthält, wird
     * ein Hinweis geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testShowNoMultipartContent() throws Exception {
        this.message.put(Message.CONTENT_TYPE, "multipart/xml");
        setInputStreamContent(TEST_DATA_XML);
        this.interceptor.setShowMultipartContent(false);

        setRequestor(false);
        this.interceptor.handleMessage(this.message);

        setRequestor(true);
        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(2)).logBodyIn(any(), contains("Multipart Content"), any());
    }

    /**
     * Falls Multipart-Content geloggt werden soll und die Nachricht Multipart-Content enthält, wird der
     * Content geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testShowMultipartContent() throws Exception {
        this.message.put(Message.CONTENT_TYPE, "multipart/xml");
        setInputStreamContent(TEST_DATA_XML);
        this.interceptor.setShowMultipartContent(true);

        setRequestor(false);
        this.interceptor.handleMessage(this.message);

        setRequestor(true);
        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(2)).logBodyIn(any(), contains(TEST_DATA_XML), any());
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
        this.interceptor.setLimit(limit);
        String content = RandomStringUtils.randomAlphanumeric(1000);
        setInputStreamContent(content);
        assertThat(content.getBytes().length, is(1000));

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(1)).logBodyIn(any(), this.logMessageCaptor.capture(), any());
        String capturedLogMessage = this.logMessageCaptor.getValue();

        // Extrahiere die "echte" Payload. CXF erstellt hier (fehlerhafterweise) keinen Truncate-Hinweis.
        int payloadStart = capturedLogMessage.indexOf(content.substring(0, 10));
        String payload = capturedLogMessage.substring(payloadStart);
        assertThat(payload.getBytes().length, is(limit));
    }
}
