package de.bund.bva.isyfact.security.oauth2.client.authentication.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;

import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;

public class BhknzHeaderConverterBuilder {

    /** The name of the HTTP header that's used to pass the BHKNZ. */
    private final String headerName;

    public BhknzHeaderConverterBuilder(IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps) {
        this.headerName = isyOAuth2ClientProps.getBhknzHeaderName();
    }

    public Converter<OAuth2PasswordGrantRequest, HttpHeaders> buildWith(String bhknz, String certificateOu) {
        return new BhknzHeaderConverter(bhknz, certificateOu);
    }

    private class BhknzHeaderConverter implements Converter<OAuth2PasswordGrantRequest, HttpHeaders> {

        /** The BHKNZ to pass as part of the header. */
        private final String bhknz;

        /** The certificate OU to pass as part of the header. */
        private final String certificateOu;

        private BhknzHeaderConverter(String bhknz, String certificateOu) {
            this.bhknz = bhknz;
            this.certificateOu = certificateOu;
        }

        @Override
        public HttpHeaders convert(OAuth2PasswordGrantRequest request) {
            String headerValue = String.format("%s:%s", bhknz, certificateOu);

            HttpHeaders headers = new HttpHeaders();
            headers.add(headerName, headerValue);
            return headers;
        }
    }
}
