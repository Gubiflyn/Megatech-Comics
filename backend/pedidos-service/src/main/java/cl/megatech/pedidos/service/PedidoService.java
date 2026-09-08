package cl.megatech.pedidos.service;

import cl.megatech.pedidos.client.CarritoClient;
import cl.megatech.pedidos.client.InventarioClient;
import cl.megatech.pedidos.dto.CarritoResponse;
import cl.megatech.pedidos.dto.InventarioResponse;
import cl.megatech.pedidos.dto.ItemCarritoResponse;
import cl.megatech.pedidos.exception.CarritoVacioException;
import cl.megatech.pedidos.exception.RecursoNoEncontradoException;
import cl.megatech.pedidos.exception.StockInsuficienteException;
import cl.megatech.pedidos.model.EstadoPedido;
import cl.megatech.pedidos.model.ItemPedido;
import cl.megatech.pedidos.model.Pedido;
import cl.megatech.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoClient carritoClient;
    private final InventarioClient inventarioClient;

    public PedidoService(
            PedidoRepository pedidoRepository,
            CarritoClient carritoClient,
            InventarioClient inventarioClient) {

        this.pedidoRepository = pedidoRepository;
        this.carritoClient = carritoClient;
        this.inventarioClient = inventarioClient;
    }

    @Transactional
    public Pedido crearPedido(
            String usuarioId,
            String authorizationHeader) {

        CarritoResponse carrito = carritoClient.obtenerCarrito(
                usuarioId,
                authorizationHeader
        );

        if (carrito.getItems() == null
                || carrito.getItems().isEmpty()) {

            throw new CarritoVacioException(
                    "No se puede crear un pedido con el carrito vacío"
            );
        }

        validarStockCompleto(
                carrito.getItems(),
                authorizationHeader
        );

        List<ItemCarritoResponse> descontados =
                new ArrayList<>();

        Pedido pedidoGuardado = null;

        try {

            for (ItemCarritoResponse item : carrito.getItems()) {

                inventarioClient.descontarStock(
                        item.getProductoId(),
                        item.getCantidad(),
                        authorizationHeader
                );

                descontados.add(item);
            }

            Pedido pedido = construirPedido(
                    usuarioId,
                    carrito.getItems()
            );

            pedidoGuardado = pedidoRepository.save(pedido);

            carritoClient.vaciarCarrito(
                    usuarioId,
                    authorizationHeader
            );

            return pedidoGuardado;

        } catch (RuntimeException ex) {

            if (pedidoGuardado != null) {
                pedidoRepository.delete(pedidoGuardado);
            }

            compensarStock(
                    descontados,
                    authorizationHeader
            );

            throw ex;
        }
    }

    public Pedido obtenerPorId(Long pedidoId) {

        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el pedido " + pedidoId
                        )
                );
    }

    public List<Pedido> obtenerPorUsuario(
            String usuarioId) {

        return pedidoRepository
                .findByUsuarioIdOrderByFechaCreacionDesc(
                        usuarioId
                );
    }

    @Transactional
    public Pedido actualizarEstado(
            Long pedidoId,
            String nuevoEstado) {

        Pedido pedido = obtenerPorId(pedidoId);

        EstadoPedido estado;

        try {
            estado = EstadoPedido.valueOf(
                    nuevoEstado.trim().toUpperCase()
            );
        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "Estado inválido. Valores permitidos: "
                            + "PENDIENTE_PAGO, PAGADO, CANCELADO"
            );
        }

        pedido.setEstado(estado);

        return pedidoRepository.save(pedido);
    }

    private void validarStockCompleto(
            List<ItemCarritoResponse> items,
            String authorizationHeader) {

        for (ItemCarritoResponse item : items) {

            InventarioResponse inventario =
                    inventarioClient.obtenerInventario(
                            item.getProductoId(),
                            authorizationHeader
                    );

            if (inventario.getStock() < item.getCantidad()) {

                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto "
                                + item.getProductoId()
                                + ". Disponible: "
                                + inventario.getStock()
                                + ", solicitado: "
                                + item.getCantidad()
                );
            }
        }
    }

    private Pedido construirPedido(
            String usuarioId,
            List<ItemCarritoResponse> itemsCarrito) {

        Pedido pedido = new Pedido();

        pedido.setUsuarioId(usuarioId);
        pedido.setEstado(EstadoPedido.PENDIENTE_PAGO);

        for (ItemCarritoResponse itemCarrito : itemsCarrito) {

            ItemPedido itemPedido = new ItemPedido();

            itemPedido.setProductoId(
                    itemCarrito.getProductoId()
            );

            itemPedido.setCantidad(
                    itemCarrito.getCantidad()
            );

            pedido.agregarItem(itemPedido);
        }

        return pedido;
    }

    private void compensarStock(
            List<ItemCarritoResponse> descontados,
            String authorizationHeader) {

        for (int i = descontados.size() - 1;
             i >= 0;
             i--) {

            ItemCarritoResponse item =
                    descontados.get(i);

            try {

                inventarioClient.reponerStock(
                        item.getProductoId(),
                        item.getCantidad(),
                        authorizationHeader
                );

            } catch (RuntimeException ignored) {
                // En una arquitectura completa este caso
                // debería registrarse para reintento/compensación.
            }
        }
    }
}