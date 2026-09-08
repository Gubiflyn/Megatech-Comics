package com.megatech.bffservice.client;

import com.megatech.bffservice.exception.DownstreamServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Thin wrapper around the shared WebClient used by every domain service proxy.
 * Centralizes error translation: a non-2xx response becomes a passthrough
 * DownstreamServiceException (same status relayed to the client), while any
 * other failure (timeout, connection refused, DNS) becomes a 502.
 */
@Component
public class DownstreamClient {

    private final WebClient webClient;

    public DownstreamClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> T get(String url, String authorizationHeader, Class<T> responseType) {
        return execute(webClient.get().uri(url), authorizationHeader, responseType);
    }

    public <T> T post(String url, Object body, String authorizationHeader, Class<T> responseType) {
        return execute(webClient.post().uri(url).bodyValue(body), authorizationHeader, responseType);
    }

    public <T> T post(String url, String authorizationHeader, Class<T> responseType) {
        return execute(webClient.post().uri(url), authorizationHeader, responseType);
    }

    public <T> T put(String url, Object body, String authorizationHeader, Class<T> responseType) {
        return execute(webClient.put().uri(url).bodyValue(body), authorizationHeader, responseType);
    }

    public void delete(String url, String authorizationHeader) {
        execute(webClient.delete().uri(url), authorizationHeader, Void.class);
    }

    public <T> T delete(String url, String authorizationHeader, Class<T> responseType) {
        return execute(webClient.delete().uri(url), authorizationHeader, responseType);
    }

    private <T> T execute(WebClient.RequestHeadersSpec<?> spec, String authorizationHeader, Class<T> responseType) {
        if (authorizationHeader != null) {
            spec = spec.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }

        try {
            return spec.retrieve()
                    .onStatus(HttpStatusCode::isError, this::mapBusinessError)
                    .bodyToMono(responseType)
                    .block();
        } catch (DownstreamServiceException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new DownstreamServiceException(null, e.getMessage(), false);
        }
    }

    private Mono<? extends Throwable> mapBusinessError(org.springframework.web.reactive.function.client.ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .map(body -> new DownstreamServiceException(response.statusCode(), body, true));
    }
}
