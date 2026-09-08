package cl.megatech.carrito.client;

import cl.megatech.carrito.dto.InventarioResponse;
import cl.megatech.carrito.exception.RecursoNoEncontradoException;
import cl.megatech.carrito.exception.ServicioInventarioException;
import cl.megatech.carrito.exception.StockNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class InventarioClient {

    private final RestClient restClient;

    public InventarioClient(
            @Value("${services.inventario.url:http://localhost:8083}")
            String inventarioUrl) {

        this.restClient = RestClient.builder()
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

            if (authorizationHeader != null
                    && !authorizationHeader.isBlank()) {

                request.header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeader
                );
            }

            InventarioResponse inventario = request
                    .retrieve()
                    .body(InventarioResponse.class);

            if (inventario == null) {
                throw new ServicioInventarioException(
                        "Inventario no entregó una respuesta válida"
                );
            }

            return inventario;

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 404) {
                throw new RecursoNoEncontradoException(
                        "No existe inventario para el producto "
                                + productoId
                );
            }

            if (ex.getStatusCode().value() == 401) {
                throw new ServicioInventarioException(
                        "Inventario rechazó la petición por falta de autenticación"
                );
            }

            throw new ServicioInventarioException(
                    "No fue posible consultar inventario"
            );

        } catch (RecursoNoEncontradoException
                 | ServicioInventarioException
                 | StockNoDisponibleException ex) {

            throw ex;

        } catch (Exception ex) {

            throw new ServicioInventarioException(
                    "No fue posible conectar con inventario-service"
            );
        }
    }

    public void validarStock(
            Long productoId,
            Integer cantidadSolicitada,
            String authorizationHeader) {

        InventarioResponse inventario =
                obtenerInventario(
                        productoId,
                        authorizationHeader
                );

        if (inventario.getStock() < cantidadSolicitada) {

            throw new StockNoDisponibleException(
                    "Stock insuficiente para el producto "
                            + productoId
                            + ". Disponible: "
                            + inventario.getStock()
                            + ", solicitado: "
                            + cantidadSolicitada
            );
        }
    }
}