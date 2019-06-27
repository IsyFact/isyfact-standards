package de.bund.bva.isyfact.serviceapi.common.util;

import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_STORED_LOGGER;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_STORED_LOG_MESSAGE;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.LOGGABLE_HEADER;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.utils.HttpUtils;
import org.apache.cxf.message.Message;
import org.springframework.http.HttpStatus;

/**
 * Utility-Methoden zum Umgang mit der {@link Message} bei den {@link Interceptor} (basierend auf
 * {@link HttpUtils}).
 */
public final class IsyHttpUtils {

    private IsyHttpUtils() {
    }

    public static MultivaluedMap<String, Object> getProtocolHeaders(Message message) {
        return HttpUtils.getModifiableHeaders(message);
    }

    public static String getProtocolHeader(Message message, String key, String defaultValue) {
        MultivaluedMap<String, Object> headers = getProtocolHeaders(message);
        return Optional.ofNullable(headers.getFirst(key)).map(Object::toString).orElse(defaultValue);
    }

    public static String getProtocolHeader(Message message, String key) {
        MultivaluedMap<String, Object> headers = getProtocolHeaders(message);
        return (String) headers.getFirst(key);
    }

    public static void setProtocolHeader(Message message, String key, String value) {
        MultivaluedMap<String, Object> headers = getProtocolHeaders(message);
        headers.putSingle(key, value);
    }

    public static MultivaluedMap<String, Object> getLoggableProtocolHeaders(Message message) {
        MultivaluedMap<String, Object> headers = getProtocolHeaders(message);
        return copyAndFilter(headers);
    }

    private static MultivaluedMap<String, Object> copyAndFilter(MultivaluedMap<String, Object> map) {
        MultivaluedMap<String, Object> copy = new MultivaluedHashMap<>();
        copy.putAll(map);
        copy.keySet().removeIf(key -> !isLoggableProtocolHeader(key));
        return copy;
    }

    private static boolean containsIgnoreCase(String key, Collection<String> list) {
        return list.stream().anyMatch(l -> l.equalsIgnoreCase(key));
    }

    public static boolean isLoggableProtocolHeader(String key) {
        return containsIgnoreCase(key, LOGGABLE_HEADER);
    }

    public static boolean istZwischengespeicherteNachrichtVorhanden(Message message) {
        Collection<String> notwendigeKeys = Arrays.asList(KEY_STORED_LOG_MESSAGE, KEY_STORED_LOGGER);
        return message.getExchange().keySet().containsAll(notwendigeKeys);
    }

    public static boolean istInternerServerFehler(Message message) {
        Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
        return responseCode != null && HttpStatus.INTERNAL_SERVER_ERROR.value() == responseCode;
    }
}
