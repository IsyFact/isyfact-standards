package de.bund.bva.isyfact.serviceapi.core.rest.message;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;

import de.bund.bva.isyfact.serviceapi.common.util.IsyHttpUtils;

/**
 * Utility-Methoden zur Extraktion einer {@link LoggingMessage} aus einer {@link Message} und Formattierung
 * zur Erstellung von Logeintraegen.
 */
public final class IsyLoggingMessageUtil {

    private IsyLoggingMessageUtil() {
    }

    /**
     * Entfernt Zeilenumbrüche zwischen Elemente, aber nicht innerhalb eines Elements.
     * @param message
     *            XML-Nachricht mit Zeilenumbrüchen.
     * @return XML-Nachricht ohne Zeilenumbrüchen zwischen Elementen.
     */
    public static String minifyXml(String message) {
        // TODO Nicht Teil der offiziellen Anforderung. Hauptsächlich während der Entwicklung genutzt. Nicht
        // ausgiebig getestet.
        String closing = "(>|\" )"; // mit Sonderbehandlung für Namespace-Definitionen.
        String emptySpace = "(\\r\\n|\\r|\\n|\\s)+";
        String opening = "(<|x)"; // mit Sonderbehandlung für Namespace-Definitionen.
        // Regex: Lookbehind for closing tag, Match empty space, Lookahead for opening tag.
        String lookAroundTemplate = "(?<=%s)%s(?=%s)";
        String regexSpaceBetweenElements = String.format(lookAroundTemplate, closing, emptySpace, opening);
        return message.replaceAll(regexSpaceBetweenElements, "");
    }

    /**
     * Extrahiert technische Header-Informationen aus der Nachricht.
     * @param message
     *            Eingehende Nachricht.
     * @return Header-Informationen.
     * @see LoggingInInterceptor
     */
    public static IsyLoggingMessageHeader createForHeaderIn(Message message) {
        IsyLoggingMessageHeader buffer = createForHeader(message);

        if (!Boolean.TRUE.equals(message.get(Message.DECOUPLED_CHANNEL_MESSAGE))) {
            // avoid logging the default responseCode 200 for the decoupled responses
            Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
            if (responseCode != null) {
                buffer.getResponseCode().append(responseCode);
            }
        }

        String uri = (String) message.get(Message.REQUEST_URL);
        uri = formatUri(message, uri);
        if (uri != null) {
            buffer.getAddress().append(uri);
            String query = (String) message.get(Message.QUERY_STRING);
            if (query != null) {
                buffer.getAddress().append("?").append(query);
            }
        }
        return buffer;
    }

    /**
     * Extrahiert technische Header-Informationen aus der Nachricht.
     * @param message
     *            Ausgehende Nachricht.
     * @return Header-Informationen.
     * @see LoggingOutInterceptor
     */
    public static IsyLoggingMessageHeader createForHeaderOut(Message message) {
        IsyLoggingMessageHeader buffer = createForHeader(message);

        Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
        if (responseCode != null) {
            buffer.getResponseCode().append(responseCode);
        }
        String address = (String) message.get(Message.ENDPOINT_ADDRESS);
        if (address != null) {
            buffer.getAddress().append(address);
            String uri = (String) message.get(Message.REQUEST_URI);
            if (uri != null && !address.startsWith(uri)) {
                if (!address.endsWith("/") && !uri.startsWith("/")) {
                    buffer.getAddress().append("/");
                }
                buffer.getAddress().append(uri);
            }
        }
        return buffer;
    }

    private static IsyLoggingMessageHeader createForHeader(Message message) {
        IsyLoggingMessageHeader buffer = new IsyLoggingMessageHeader();

        String encoding = (String) message.get(Message.ENCODING);
        if (encoding != null) {
            buffer.getEncoding().append(encoding);
        }
        String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
        if (httpMethod != null) {
            buffer.getHttpMethod().append(httpMethod);
        }
        String ct = (String) message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        Object headers = message.get(Message.PROTOCOL_HEADERS);
        if (headers != null) {
            buffer.getHeader().append(IsyHttpUtils.getLoggableProtocolHeaders(message));
        }

        return buffer;
    }

    private static String formatUri(Message message, String uri) {
        if (uri == null) {
            String address = (String) message.get(Message.ENDPOINT_ADDRESS);
            uri = (String) message.get(Message.REQUEST_URI);
            if (uri != null && uri.startsWith("/")) {
                if (address != null && !address.startsWith(uri)) {
                    if (address.endsWith("/") && address.length() > 1) {
                        address = address.substring(0, address.length());
                    }
                    uri = address + uri;
                }
            } else {
                uri = address;
            }
        }
        return uri;
    }
}
