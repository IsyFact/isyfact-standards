package de.bund.bva.isyfact.ueberwachung.config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * Helper-Class for Configuring a RestTemplate to use for the NachbarsystemCheck.
 */
public class NachbarsystemRestTemplateConfigurer {

    public static RestTemplateBuilder configureForNachbarSystemCheck( RestTemplateBuilder builder, NachbarsystemConfigurationProperties properties ){
        Duration timeout =
            properties.getNachbarsystemCheck().getTimeout();
        return builder
            .setConnectTimeout(timeout)
            .setReadTimeout(timeout)
            .errorHandler(new CustomErrorHandler())
            ;
    }

    /**
     * Spring Actuator uses 503 as a default Status Code for "DOWN" and "OUT_OF_SERVICE" Health Requests.
     * We skip the default Error Handling for 503.
     * If the responsebody isn't in the format we expect, we handle that in the main-class.
     */
    public static class CustomErrorHandler extends DefaultResponseErrorHandler {

        private static final int STATUSCODE_TO_SKIP = 503;

        @Override
        public void handleError(@NonNull ClientHttpResponse response, @NonNull HttpStatus statusCode)
            throws IOException {

            //skip Default Error Handling for STATUSCODE_TO_SKIP
            if( statusCode.value() != STATUSCODE_TO_SKIP) {
                super.handleError(response, statusCode);
            }
        }
    }
}
