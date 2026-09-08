package cl.megatech.pedidos.client;

import cl.megatech.pedidos.dto.CarritoResponse;
import cl.megatech.pedidos.exception.RecursoNoEncontradoException;
import cl.megatech.pedidos.exception.ServicioExternoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class CarritoClient {

    private final RestClient restClient;

    public CarritoClient(
            @Value("${services.carrito.url:http://localhost:8085}")
            String carritoUrl) {

        restClient = RestClient.builder()
                .baseUrl(carritoUrl)
                .build();
    }

    public CarritoResponse obtenerCarrito(
            String usuarioId,
            String authorizationHeader) {

        try {

            RestClient.RequestHeadersSpec<?> request = restClient
                    .get()
                    .uri("/api/carritos/{usuarioId}", usuarioId);

            agregarAuthorization(request, authorizationHeader);

            CarritoResponse carrito = request
                    .retrieve()
                    .body(CarritoResponse.class);

            if (carrito == null) {
                throw new ServicioExternoException(
                        "carrito-service no entregó una respuesta válida"
                );
            }

            return carrito;

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 404) {
                throw new RecursoNoEncontradoException(
                        "El usuario no tiene un carrito"
                );
            }

            throw new ServicioExternoException(
                    "No fue posible consultar carrito-service"
            );
        }
    }

    public void vaciarCarrito(
            String usuarioId,
            String authorizationHeader) {

        try {

            RestClient.RequestHeadersSpec<?> request = restClient
                    .delete()
                    .uri("/api/carritos/{usuarioId}", usuarioId);

            agregarAuthorization(request, authorizationHeader);

            request.retrieve().toBodilessEntity();

        } catch (RestClientResponseException ex) {

            throw new ServicioExternoException(
                    "No fue posible vaciar el carrito"
            );
        }
    }

    private void agregarAuthorization(
            RestClient.RequestHeadersSpec<?> request,
            String authorizationHeader) {

        if (authorizationHeader != null
                && !authorizationHeader.isBlank()) {

            request.header(
                    HttpHeaders.AUTHORIZATION,
                    authorizationHeader
            );
        }
    }
}