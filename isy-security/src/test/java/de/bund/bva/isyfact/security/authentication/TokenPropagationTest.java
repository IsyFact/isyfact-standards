package de.bund.bva.isyfact.security.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TokenPropagationTest {

    private WebClient webClient;

    private String testUri;

    @LocalServerPort
    private int port;

    private String bearerToken;

    @Captor
    private ArgumentCaptor<ClientRequest> argumentCaptor;

    @Mock
    private BearerTokenAuthentication authentication;

    @Mock
    private OAuth2AccessToken oAuth2AccessToken;

    @Mock
    private ExchangeFunction exchangeFunction;

    @Mock
    private ClientResponse clientResponse;

    @BeforeEach
    public void setup() {
        bearerToken = "eYsupernicetoken";
        when(oAuth2AccessToken.getTokenValue()).thenReturn(bearerToken);
        when(authentication.getCredentials()).thenReturn(oAuth2AccessToken);

        // Initialize security context with mocked authentication that provides an OAuth2 access token
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock client response
        when(clientResponse.bodyToMono(Void.class)).thenReturn(Mono.empty());
        when(clientResponse.statusCode()).thenReturn(HttpStatus.OK);
        when(exchangeFunction.exchange(argumentCaptor.capture())).thenReturn(Mono.just(clientResponse));

        // the web client doesn't have to actually perform a successful request
        // but to avoid potential errors we perform a request against a port hosted by spring
        testUri = "http://localhost:" + port;
        // Build web client which contains ServletBearerExchangeFilterFunction (enables token propagation)
        webClient = WebClient.builder().baseUrl(testUri).exchangeFunction(exchangeFunction)
            .filter(new ServletBearerExchangeFilterFunction()).build();
    }

    @Test
    void shouldPropagateBearerTokenInHeader() {
        webClient.get()
            .retrieve()
            .bodyToMono(Void.class)
            .block();

        ClientRequest request = verifyAndGetRequest();

        assertEquals(request.headers().get(HttpHeaders.AUTHORIZATION), Collections.singletonList("Bearer " + bearerToken));
    }

    @Test
    void shouldNotPropagateBearerTokenInHeader() {
        SecurityContextHolder.getContext().setAuthentication(null);

        webClient.get()
            .retrieve()
            .bodyToMono(Void.class)
            .block();

        ClientRequest request = verifyAndGetRequest();

        assertNull(request.headers().get(HttpHeaders.AUTHORIZATION));
    }

    private ClientRequest verifyAndGetRequest() {
        ClientRequest request = argumentCaptor.getValue();
        verify(exchangeFunction).exchange(request);
        verifyNoMoreInteractions(exchangeFunction);
        return request;
    }

}
