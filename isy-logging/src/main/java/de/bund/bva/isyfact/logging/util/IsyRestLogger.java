package de.bund.bva.isyfact.logging.util;

import reactor.core.publisher.Mono;

import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.config.IsyLoggingBoundaryLoggerProperties;
import de.bund.bva.isyfact.logging.impl.Ereignisschluessel;

/**
 * Bean class that provides a logging function for outgoing REST calls. It provides logging calls for
 * <li>started REST calls</li>
 * <li>successfull REST calls with or without time measurement</li>
 * <li>unsuccessfull REST calls with or without time measurement</li>
 * <li>detailed information about used parameters and response bodies</li>
 */
public class IsyRestLogger {

    /** Properties that define which actions should be logged. */
    private final IsyLoggingBoundaryLoggerProperties properties;

    public IsyRestLogger(IsyLoggingBoundaryLoggerProperties properties) {
        this.properties = properties;
    }

    /**
     * Filter function that has to be called by WebClientConfigBeans through the WebClient.builder()-API chain.
     * @param logger An IysLogger instance
     * @param system name of the called system (Nachbarsystem)
     * @return ExchangeFilterFunction to call build() on
     */
    public ExchangeFilterFunction loggingFilter(IsyLogger logger, String system) {
        boolean loggeAufruf = properties.isLoggeAufruf();
        boolean loggeErgebnis = properties.isLoggeErgebnis();
        boolean loggeDauer = properties.isLoggeDauer();
        boolean loggeDaten = properties.isLoggeDaten();
        boolean loggeDatenBeiException = properties.isLoggeDatenBeiException();


        return (request, next) -> {
            long start = System.currentTimeMillis();
            HttpMethod method = request.method();
            String url = request.url().getPath();

            if (loggeAufruf) {
                logger.info(
                        LogKategorie.JOURNAL,
                        Ereignisschluessel.EISYLO01011.name(),
                        Ereignisschluessel.EISYLO01011.getNachricht(),
                        method,
                        system,
                        url
                );
            }

            return next.exchange(request)
                    .flatMap(response -> {
                        long duration = System.currentTimeMillis() - start;
                        int status = response.statusCode().value();

                        if (status >= 400) {
                            if (!loggeDauer) {
                                logger.info(LogKategorie.PROFILING,
                                        Ereignisschluessel.EISYLO01013.name(),
                                        Ereignisschluessel.EISYLO01013.getNachricht(),
                                        method,
                                        system,
                                        url);
                            }
                            if (loggeDauer) {
                                logger.info(LogKategorie.PROFILING,
                                        Ereignisschluessel.EISYLO01015.name(),
                                        Ereignisschluessel.EISYLO01015.getNachricht(),
                                        method,
                                        system,
                                        url,
                                        duration);
                            }
                            if (loggeDatenBeiException && logger.isDebugEnabled()) {
                                return logResponseBody(logger, request, response);
                            }
                        } else {
                            if (loggeErgebnis && !loggeDauer) {
                                logger.info(LogKategorie.METRIK,
                                        Ereignisschluessel.EISYLO01012.name(),
                                        Ereignisschluessel.EISYLO01012.getNachricht(),
                                        method,
                                        system,
                                        url);
                            }
                            if (loggeErgebnis && loggeDauer) {
                                logger.info(LogKategorie.METRIK,
                                        Ereignisschluessel.EISYLO01014.name(),
                                        Ereignisschluessel.EISYLO01014.getNachricht(),
                                        method,
                                        system,
                                        url,
                                        duration);
                            }
                            if (loggeDaten && logger.isDebugEnabled()) {
                                return logResponseBody(logger, request, response);
                            }
                        }
                        return Mono.just(response);
                    })
                    .doOnError(ex -> {
                        long duration = System.currentTimeMillis() - start;
                        if (!loggeDauer) {
                            logger.info(LogKategorie.PROFILING,
                                    Ereignisschluessel.EISYLO01013.name(),
                                    Ereignisschluessel.EISYLO01013.getNachricht(),
                                    method,
                                    system,
                                    url);
                        }
                        if (loggeDauer) {
                            logger.info(LogKategorie.PROFILING,
                                    Ereignisschluessel.EISYLO01015.name(),
                                    Ereignisschluessel.EISYLO01015.getNachricht(),
                                    method,
                                    system,
                                    url,
                                    duration);
                        }
                    });
        };
    }

    /**
     * Logs details about the request and its response. Should not be used in production setting.
     * @param logger IsyLogger instance
     * @param request Request object
     * @param response Response object
     * @return newly built body
     */
    private Mono<ClientResponse> logResponseBody(IsyLogger logger, ClientRequest request, ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> {
                    logger.debugFachdaten(Ereignisschluessel.DEBUG_LOGGE_DATEN.getNachricht(),
                            request.url().getPath(), request.url().getQuery(), response);
                    return Mono.just(
                            response.mutate()
                                    .body(body)
                                    .build()
                    );
                });
    }
}
