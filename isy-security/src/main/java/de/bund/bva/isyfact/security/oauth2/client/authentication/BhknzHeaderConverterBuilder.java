package de.bund.bva.isyfact.security.oauth2.client.authentication;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;

import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;

public class BhknzHeaderConverterBuilder {

    private final String headerName;

    private final String defaultCertificateOu;

    public BhknzHeaderConverterBuilder(IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps) {
        this.headerName = isyOAuth2ClientProps.getBhknzHeaderName();
        this.defaultCertificateOu = isyOAuth2ClientProps.getDefaultCertificateOu();
    }

    public Converter<OAuth2PasswordGrantRequest, HttpHeaders> buildWith(String bhknz) {
        return new BhknzHeaderConverter(bhknz);
    }

    private class BhknzHeaderConverter implements Converter<OAuth2PasswordGrantRequest, HttpHeaders> {

        private final String bhknz;

        private BhknzHeaderConverter(String bhknz) {
            this.bhknz = bhknz;
        }

        @Override
        public HttpHeaders convert(OAuth2PasswordGrantRequest request) {
            String headerValue = String.format("%s:%s", bhknz, defaultCertificateOu);

            HttpHeaders headers = new HttpHeaders();
            headers.add(headerName, headerValue);
            return headers;
        }
    }
}
