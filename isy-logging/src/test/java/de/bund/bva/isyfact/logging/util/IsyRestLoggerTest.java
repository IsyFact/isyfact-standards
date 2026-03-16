package de.bund.bva.isyfact.logging.util;

import reactor.core.publisher.Mono;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.logging.IsyDatentypMarker;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.config.IsyLoggingBoundaryLoggerProperties;

public class IsyRestLoggerTest {

    /**
     * Fake Logger for collecting entries
     */
    static class TestLogger implements IsyLogger {

        List<String> infos = new ArrayList<>();
        List<String> debugs = new ArrayList<>();

        @Override
        public void trace(String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void debug(String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void info(LogKategorie kategorie, String key, String msg, Object... params) {
            infos.add(key);
        }

        @Override
        public void info(LogKategorie kategorie, String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void info(LogKategorie kategorie, String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void info(LogKategorie kategorie, String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warn(String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warn(String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warn(String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warn(String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void error(String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void error(String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void error(String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void error(String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatal(String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatal(String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatal(String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatal(String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void traceFachdaten(String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void debugFachdaten(String msg, Object... params) {
            debugs.add(msg);
        }

        @Override
        public void infoFachdaten(LogKategorie kategorie, String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void infoFachdaten(LogKategorie kategorie, String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void infoFachdaten(LogKategorie kategorie, String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void infoFachdaten(LogKategorie kategorie, String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warnFachdaten(String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warnFachdaten(String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warnFachdaten(String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warnFachdaten(String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void errorFachdaten(String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void errorFachdaten(String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void errorFachdaten(String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void errorFachdaten(String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatalFachdaten(String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatalFachdaten(String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatalFachdaten(String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatalFachdaten(String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public boolean isDebugEnabled() {
            return true;
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public boolean isFatalEnabled() {
            return false;
        }

        @Override
        public void trace(IsyDatentypMarker typ, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void debug(IsyDatentypMarker typ, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void info(LogKategorie kategorie, IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void info(LogKategorie kategorie, IsyDatentypMarker typ, String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void info(LogKategorie kategorie, IsyDatentypMarker typ, String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void info(LogKategorie kategorie, IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warn(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warn(IsyDatentypMarker typ, String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warn(IsyDatentypMarker typ, String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void warn(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void error(IsyDatentypMarker typ, String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void error(IsyDatentypMarker typ, String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void error(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void error(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatal(IsyDatentypMarker typ, String nachricht, BaseException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatal(IsyDatentypMarker typ, String nachricht, TechnicalRuntimeException exception, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatal(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t, Object... werte) {
            // no-op
            // default implementation for test adapter
        }

        @Override
        public void fatal(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte) {
            // no-op
            // default implementation for test adapter
        }
    }

    /**
     * Mock properties
     */
    static class TestProperties extends IsyLoggingBoundaryLoggerProperties {

        boolean logAufruf = true;
        boolean logErgebnis = true;
        boolean logDauer = true;
        boolean logDaten = false;
        boolean logDatenException = false;

        @Override public boolean isLoggeAufruf() { return logAufruf; }
        @Override public boolean isLoggeErgebnis() { return logErgebnis; }
        @Override public boolean isLoggeDauer() { return logDauer; }
        @Override public boolean isLoggeDaten() { return logDaten; }
        @Override public boolean isLoggeDatenBeiException() { return logDatenException; }
    }

    private ClientRequest createRequest() {
        return ClientRequest
                .create(HttpMethod.GET, URI.create("http://localhost/test"))
                .body(BodyInserters.empty())
                .build();
    }

    @Test
    public void testSuccessfulResponse() {

        TestLogger logger = new TestLogger();
        TestProperties properties = new TestProperties();

        IsyRestLogger restLogger = new IsyRestLogger(properties);

        ExchangeFunction exchange = request ->
                Mono.just(ClientResponse.create(HttpStatus.OK).build());

        ExchangeFilterFunction filter = restLogger.loggingFilter(logger, "TEST");

        ClientResponse response =
                filter.filter(createRequest(), exchange).block();

        assertNotNull(response);
        assertTrue(response.statusCode().is2xxSuccessful());
        assertFalse(logger.infos.isEmpty());
    }

    @Test
    public void testErrorResponse() {

        TestLogger logger = new TestLogger();
        TestProperties properties = new TestProperties();

        IsyRestLogger restLogger = new IsyRestLogger(properties);

        ExchangeFunction exchange = request ->
                Mono.just(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build());

        ExchangeFilterFunction filter = restLogger.loggingFilter(logger, "TEST");

        ClientResponse response =
                filter.filter(createRequest(), exchange).block();

        assertNotNull(response);
        assertEquals(500, response.statusCode().value());
        assertFalse(logger.infos.isEmpty());
    }

    @Test
    public void testExceptionHandling() {

        TestLogger logger = new TestLogger();
        TestProperties properties = new TestProperties();

        IsyRestLogger restLogger = new IsyRestLogger(properties);

        ExchangeFunction exchange = request ->
                Mono.error(new RuntimeException("Some error"));

        ExchangeFilterFunction filter = restLogger.loggingFilter(logger, "TEST");

        ClientRequest request = createRequest();
        Mono<ClientResponse> result = filter.filter(request, exchange);

        assertThrows(RuntimeException.class, result::block);

        assertFalse(logger.infos.isEmpty());
    }

    @Test
    public void testDebugBodyLogging() {

        TestLogger logger = new TestLogger();
        TestProperties props = new TestProperties();
        props.logDaten = true;

        IsyRestLogger restLogger = new IsyRestLogger(props);

        ExchangeFunction exchange = request ->
                Mono.just(ClientResponse.create(HttpStatus.OK)
                        .body("test-body")
                        .build());

        ExchangeFilterFunction filter = restLogger.loggingFilter(logger, "TEST");

        filter.filter(createRequest(), exchange).block();

        assertFalse(logger.debugs.isEmpty());
    }

    @Test
    public void testSuccessfulResponseWithoutDurationLogging() {

        TestLogger logger = new TestLogger();
        TestProperties props = new TestProperties();
        props.logDauer = false;

        IsyRestLogger restLogger = new IsyRestLogger(props);

        ExchangeFunction exchange = request ->
                Mono.just(ClientResponse.create(HttpStatus.OK).build());

        ExchangeFilterFunction filter = restLogger.loggingFilter(logger, "TEST");

        ClientResponse response =
                filter.filter(createRequest(), exchange).block();

        assertNotNull(response);
        assertTrue(response.statusCode().is2xxSuccessful());

        // logging should still happen
        assertFalse(logger.infos.isEmpty());
    }

    @Test
    public void testErrorResponseWithoutDurationLogging() {

        TestLogger logger = new TestLogger();
        TestProperties props = new TestProperties();
        props.logDauer = false;

        IsyRestLogger restLogger = new IsyRestLogger(props);

        ExchangeFunction exchange = request ->
                Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST).build());

        ExchangeFilterFunction filter = restLogger.loggingFilter(logger, "TEST");

        ClientResponse response =
                filter.filter(createRequest(), exchange).block();

        assertNotNull(response);
        assertEquals(400, response.statusCode().value());

        // error logging path executed
        assertFalse(logger.infos.isEmpty());
    }
}