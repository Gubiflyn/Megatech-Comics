package cl.megatech.pagos.client;

import cl.megatech.pagos.dto.PedidoResponse;
import cl.megatech.pagos.exception.RecursoNoEncontradoException;
import cl.megatech.pagos.exception.ServicioExternoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class PedidosClient {

    private final RestClient restClient;

    public PedidosClient(
            @Value("${services.pedidos.url:http://localhost:8086}")
            String pedidosUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(pedidosUrl)
                .build();
    }

    public PedidoResponse obtenerPedido(
            Long pedidoId,
            String authorizationHeader) {

        try {

            RestClient.RequestHeadersSpec<?> request =
                    restClient
                            .get()
                            .uri(
                                    "/api/pedidos/{pedidoId}",
                                    pedidoId
                            );

            agregarAuthorization(
                    request,
                    authorizationHeader
            );

            PedidoResponse pedido =
                    request
                            .retrieve()
                            .body(PedidoResponse.class);

            if (pedido == null) {

                throw new ServicioExternoException(
                        "pedidos-service no entregó "
                                + "una respuesta válida"
                );
            }

            return pedido;

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 404) {

                throw new RecursoNoEncontradoException(
                        "No existe el pedido "
                                + pedidoId
                );
            }

            throw new ServicioExternoException(
                    "No fue posible consultar pedidos-service"
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

