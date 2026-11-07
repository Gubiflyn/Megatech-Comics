package cl.megatech.pedidos.service;

import cl.megatech.pedidos.client.CarritoClient;
import cl.megatech.pedidos.client.InventarioClient;
import cl.megatech.pedidos.dto.CarritoResponse;
import cl.megatech.pedidos.dto.InventarioResponse;
import cl.megatech.pedidos.dto.ItemCarritoResponse;
import cl.megatech.pedidos.exception.CarritoVacioException;
import cl.megatech.pedidos.exception.RecursoNoEncontradoException;
import cl.megatech.pedidos.exception.StockInsuficienteException;
import cl.megatech.pedidos.messaging.PedidoCreadoEvent;
import cl.megatech.pedidos.messaging.PedidoCreadoProducer;
import cl.megatech.pedidos.messaging.PedidoKafkaEvent;
import cl.megatech.pedidos.messaging.PedidoKafkaProducer;
import cl.megatech.pedidos.model.EstadoPedido;
import cl.megatech.pedidos.model.ItemPedido;
import cl.megatech.pedidos.model.Pedido;
import cl.megatech.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoClient carritoClient;
    private final InventarioClient inventarioClient;
    private final PedidoCreadoProducer pedidoCreadoProducer;
    private final PedidoKafkaProducer pedidoKafkaProducer;

    public PedidoService(
            PedidoRepository pedidoRepository,
            CarritoClient carritoClient,
            InventarioClient inventarioClient,
            PedidoCreadoProducer pedidoCreadoProducer,
            PedidoKafkaProducer pedidoKafkaProducer) {

        this.pedidoRepository = pedidoRepository;
        this.carritoClient = carritoClient;
        this.inventarioClient = inventarioClient;
        this.pedidoCreadoProducer = pedidoCreadoProducer;
        this.pedidoKafkaProducer = pedidoKafkaProducer;
    }

    @Transactional
    public Pedido crearPedido(
            String usuarioId,
            String authorizationHeader) {

        CarritoResponse carrito =
                carritoClient.obtenerCarrito(
                        usuarioId,
                        authorizationHeader
                );

        if (carrito.getItems() == null
                || carrito.getItems().isEmpty()) {

            throw new CarritoVacioException(
                    "No se puede crear un pedido con el carrito vacío"
            );
        }

        // Se valida stock antes de crear el pedido.
        // El descuento real lo realizará inventario-service
        // al consumir el evento pedido.creado.
        validarStockCompleto(
                carrito.getItems(),
                authorizationHeader
        );

        Pedido pedido =
                construirPedido(
                        usuarioId,
                        carrito.getItems()
                );

        Pedido pedidoGuardado =
                pedidoRepository.save(pedido);

        PedidoCreadoEvent evento =
                construirEvento(pedidoGuardado);

        pedidoCreadoProducer.publicar(evento);

        carritoClient.vaciarCarrito(
                usuarioId,
                authorizationHeader
        );

        publicarKafkaDespuesDelCommit(pedidoGuardado, "PEDIDO_CREADO");

        return pedidoGuardado;
    }

    public Pedido obtenerPorId(Long pedidoId) {

        return pedidoRepository
                .findById(pedidoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el pedido "
                                        + pedidoId
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

        Pedido pedido =
                pedidoRepository.buscarPorIdParaActualizar(pedidoId)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "No existe el pedido " + pedidoId
                        ));

        EstadoPedido estado;

        try {

            estado = EstadoPedido.valueOf(
                    nuevoEstado
                            .trim()
                            .toUpperCase()
            );

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "Estado inválido. Valores permitidos: "
                            + "PENDIENTE_PAGO, PAGADO, CANCELADO"
            );
        }

        boolean pasoAPagado = estado == EstadoPedido.PAGADO
                && pedido.getEstado() != EstadoPedido.PAGADO;

        pedido.setEstado(estado);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        if (pasoAPagado) {
            publicarKafkaDespuesDelCommit(pedidoGuardado, "PEDIDO_PAGADO");
        }

        return pedidoGuardado;
    }

    private void publicarKafkaDespuesDelCommit(Pedido pedido, String tipo) {

        List<PedidoKafkaEvent.ItemPedido> items = pedido.getItems().stream()
                .map(item -> new PedidoKafkaEvent.ItemPedido(
                        item.getProductoId(), item.getCantidad()))
                .toList();

        PedidoKafkaEvent evento = new PedidoKafkaEvent(
                UUID.randomUUID().toString(), tipo, pedido.getId(),
                pedido.getUsuarioId(), items, Instant.now().toString()
        );

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        pedidoKafkaProducer.publicar(evento);
                    }
                }
        );
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

            if (inventario.getStock()
                    < item.getCantidad()) {

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

        pedido.setEstado(
                EstadoPedido.PENDIENTE_PAGO
        );

        for (ItemCarritoResponse itemCarrito
                : itemsCarrito) {

            ItemPedido itemPedido =
                    new ItemPedido();

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

    private PedidoCreadoEvent construirEvento(
            Pedido pedido) {

        List<PedidoCreadoEvent.ItemPedidoEvent> items =
                new ArrayList<>();

        for (ItemPedido item : pedido.getItems()) {

            items.add(
                    new PedidoCreadoEvent.ItemPedidoEvent(
                            item.getProductoId(),
                            item.getCantidad()
                    )
            );
        }

        return new PedidoCreadoEvent(
                pedido.getId(),
                pedido.getUsuarioId(),
                items
        );
    }
}