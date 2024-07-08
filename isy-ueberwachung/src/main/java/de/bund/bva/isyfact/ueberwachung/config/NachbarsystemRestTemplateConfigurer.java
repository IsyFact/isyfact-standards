package de.bund.bva.isyfact.ueberwachung.config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * Helper-Class for Configuring a RestTemplate to use for the NachbarsystemCheck.
 */
public final class NachbarsystemRestTemplateConfigurer {

    private NachbarsystemRestTemplateConfigurer() {
        // private constructor to hide the implicit public one.
    }

    public static RestTemplateBuilder configureForNachbarSystemCheck(RestTemplateBuilder builder, NachbarsystemConfigurationProperties properties) {
        Duration timeout =
            properties.getNachbarsystemCheck().getTimeout();
        return builder
            .setConnectTimeout(timeout)
            .setReadTimeout(timeout)
            .errorHandler(new CustomErrorHandler());
    }

    /**
     * Spring Actuator uses 503 as a default Status Code for "DOWN" and "OUT_OF_SERVICE" Health Requests.
     * We skip the default Error Handling for 503.
     * If the responsebody isn't in the format we expect, we handle that in the main-class.
     */
    public static class CustomErrorHandler extends DefaultResponseErrorHandler {

        @Override
        public void handleError(@NonNull ClientHttpResponse response, @NonNull HttpStatusCode statusCode)
            throws IOException {

            //skip Default Error Handling for SERVICE_UNAVAILABLE
            if (statusCode.value() != HttpStatus.SERVICE_UNAVAILABLE.value()) {
                super.handleError(response, statusCode);
            }
        }
    }
}
