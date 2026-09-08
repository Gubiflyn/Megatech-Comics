package cl.megatech.pedidos.client;

import cl.megatech.pedidos.dto.InventarioResponse;
import cl.megatech.pedidos.dto.StockRequest;
import cl.megatech.pedidos.exception.RecursoNoEncontradoException;
import cl.megatech.pedidos.exception.ServicioExternoException;
import cl.megatech.pedidos.exception.StockInsuficienteException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class InventarioClient {

    private final RestClient restClient;

    public InventarioClient(
            @Value("${services.inventario.url:http://localhost:8084}")
            String inventarioUrl) {

        restClient = RestClient.builder()
                .baseUrl(inventarioUrl)
                .build();
    }

    public InventarioResponse obtenerInventario(
            Long productoId,
            String authorizationHeader) {

        try {

            RestClient.RequestHeadersSpec<?> request = restClient
                    .get()
                    .uri(
                            "/api/inventario/producto/{productoId}",
                            productoId
                    );

            agregarAuthorization(request, authorizationHeader);

            InventarioResponse respuesta = request
                    .retrieve()
                    .body(InventarioResponse.class);

            if (respuesta == null) {
                throw new ServicioExternoException(
                        "inventario-service no entregó una respuesta válida"
                );
            }

            return respuesta;

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 404) {
                throw new RecursoNoEncontradoException(
                        "No existe inventario para el producto "
                                + productoId
                );
            }

            throw new ServicioExternoException(
                    "No fue posible consultar inventario-service"
            );
        }
    }

    public void descontarStock(
            Long productoId,
            Integer cantidad,
            String authorizationHeader) {

        try {

            RestClient.RequestBodySpec request = restClient
                    .post()
                    .uri(
                            "/api/inventario/producto/{productoId}/descontar",
                            productoId
                    )
                    .contentType(MediaType.APPLICATION_JSON);

            agregarAuthorization(request, authorizationHeader);

            request
                    .body(new StockRequest(cantidad))
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 409) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto "
                                + productoId
                );
            }

            throw new ServicioExternoException(
                    "No fue posible descontar stock del producto "
                            + productoId
            );
        }
    }

    public void reponerStock(
            Long productoId,
            Integer cantidad,
            String authorizationHeader) {

        try {

            RestClient.RequestBodySpec request = restClient
                    .post()
                    .uri(
                            "/api/inventario/producto/{productoId}/reponer",
                            productoId
                    )
                    .contentType(MediaType.APPLICATION_JSON);

            agregarAuthorization(request, authorizationHeader);

            request
                    .body(new StockRequest(cantidad))
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception ex) {

            throw new ServicioExternoException(
                    "No fue posible reponer stock del producto "
                            + productoId
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