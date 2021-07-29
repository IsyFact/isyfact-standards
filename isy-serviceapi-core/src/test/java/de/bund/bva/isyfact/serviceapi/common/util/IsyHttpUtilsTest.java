package de.bund.bva.isyfact.serviceapi.common.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.Test;

/**
 * Testet die {@link IsyHttpUtils}.
 */
public class IsyHttpUtilsTest {

    @Test
    public void testIsLoggableProtocolHeader() throws Exception {
        assertThat(IsyHttpUtils.isLoggableProtocolHeader("Authorization"), is(false));
        assertThat(IsyHttpUtils.isLoggableProtocolHeader("authorization"), is(false));
        assertThat(IsyHttpUtils.isLoggableProtocolHeader("x-client-cert"), is(false));
        assertThat(IsyHttpUtils.isLoggableProtocolHeader("X-Client-Cert"), is(false));

        assertThat(IsyHttpUtils.isLoggableProtocolHeader("Accept"), is(true));
        assertThat(IsyHttpUtils.isLoggableProtocolHeader("accept"), is(true));
    }

    @Test
    public void testGetLoggableProtocolHeaders() throws Exception {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.putSingle("x-client-cert", "");
        headers.putSingle("Authorization", "");
        headers.putSingle("authorization", "");
        headers.putSingle("Accept", "");
        Message message = new MessageImpl();
        message.put(Message.PROTOCOL_HEADERS, headers);

        MultivaluedMap<String, Object> result = IsyHttpUtils.getLoggableProtocolHeaders(message);

        assertThat(result.containsKey("Accept"), is(true));
        assertThat(result.containsKey("authorization"), is(false));
        assertThat(result.containsKey("Authorization"), is(false));
        assertThat(result.containsKey("x-client-cert"), is(false));
    }
}
