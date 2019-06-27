package de.bund.bva.isyfact.serviceapi.core.rest.message;

/**
 * Wrapper-Objekt für die Informationen zu dem fachlichen Body.
 */
public class IsyLoggingMessageBody extends IsyLoggingMessage {

    public static final String EMPTY_BODY = "--- No Content ---";

    public StringBuilder getMessage() {
        return this.delegate.getMessage();
    }

    public StringBuilder getPayload() {
        return this.delegate.getPayload();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean minifyBody) {
        String message = this.delegate.getMessage().toString();
        String payload = this.delegate.getPayload().toString();

        if (message.isEmpty() && payload.isEmpty()) {
            message = EMPTY_BODY;
        }

        StringBuilder loggingMessage = new StringBuilder();
        loggingMessage.append(message.length() > 0 ? "Message: " : "").append(message);
        loggingMessage.append(loggingMessage.length() > 0 && payload.length() > 0 ? " " : "");
        loggingMessage.append(payload.length() > 0 ? "Payload: " : "");
        loggingMessage.append(minifyBody ? IsyLoggingMessageUtil.minifyXml(payload) : payload);
        return loggingMessage.toString();
    }
}
