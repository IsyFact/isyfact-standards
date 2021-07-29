package de.bund.bva.isyfact.serviceapi.core.rest.message;

/**
 * Wrapper-Objekt für die Informationen zu dem technischen Header.
 */
public class IsyLoggingMessageHeader extends IsyLoggingMessage {

    public StringBuilder getAddress() {
        return this.delegate.getAddress();
    }

    public StringBuilder getEncoding() {
        return this.delegate.getEncoding();
    }

    public StringBuilder getHeader() {
        return this.delegate.getHeader();
    }

    public StringBuilder getHttpMethod() {
        return this.delegate.getHttpMethod();
    }

    public StringBuilder getContentType() {
        return this.delegate.getContentType();
    }

    public StringBuilder getResponseCode() {
        return this.delegate.getResponseCode();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.delegate.getHttpMethod().length() > 0) {
            buffer.append("Http-Method: ");
            buffer.append(this.delegate.getHttpMethod());
        }
        if (this.delegate.getAddress().length() > 0) {
            buffer.append(buffer.length() > 0 ? ", " : "");
            buffer.append("Address: ");
            buffer.append(this.delegate.getAddress());
        }
        if (this.delegate.getResponseCode().length() > 0) {
            buffer.append(buffer.length() > 0 ? ", " : "");
            buffer.append("Response-Code: ");
            buffer.append(this.delegate.getResponseCode());
        }
        buffer.append(buffer.length() > 0 ? ", " : "");
        buffer.append("Content-Type: ");
        buffer.append(this.delegate.getContentType());
        if (this.delegate.getEncoding().length() > 0) {
            buffer.append(buffer.length() > 0 ? ", " : "");
            buffer.append("Encoding: ");
            buffer.append(this.delegate.getEncoding());
        }
        buffer.append(buffer.length() > 0 ? ", " : "");
        buffer.append("Headers: ");
        buffer.append(this.delegate.getHeader());
        return buffer.toString();
    }
}
