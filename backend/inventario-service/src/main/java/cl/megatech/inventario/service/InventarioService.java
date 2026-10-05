package cl.megatech.inventario.service;

import cl.megatech.inventario.dto.InventarioRequest;
import cl.megatech.inventario.exception.RecursoNoEncontradoException;
import cl.megatech.inventario.exception.StockInsuficienteException;
import cl.megatech.inventario.messaging.StockActualizadoEvent;
import cl.megatech.inventario.messaging.StockActualizadoProducer;
import cl.megatech.inventario.model.Inventario;
import cl.megatech.inventario.repository.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final StockActualizadoProducer stockActualizadoProducer;

    public InventarioService(
            InventarioRepository inventarioRepository,
            StockActualizadoProducer stockActualizadoProducer) {

        this.inventarioRepository = inventarioRepository;
        this.stockActualizadoProducer = stockActualizadoProducer;
    }

    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    public Inventario obtenerPorProductoId(Long productoId) {

        return inventarioRepository
                .findByProductoId(productoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe inventario para el producto "
                                        + productoId
                        )
                );
    }

    @Transactional
    public Inventario crear(
            InventarioRequest request) {

        if (inventarioRepository.existsByProductoId(
                request.getProductoId())) {

            throw new IllegalArgumentException(
                    "Ya existe un registro de inventario para el producto "
                            + request.getProductoId()
            );
        }

        Inventario inventario = new Inventario();

        inventario.setProductoId(
                request.getProductoId()
        );

        inventario.setStock(
                request.getStock()
        );

        inventario.setStockMinimo(
                request.getStockMinimo()
        );

        return inventarioRepository.save(inventario);
    }

    @Transactional
    public Inventario actualizar(
            Long productoId,
            InventarioRequest request) {

        Inventario inventario =
                obtenerPorProductoId(productoId);

        inventario.setStock(request.getStock());

        inventario.setStockMinimo(
                request.getStockMinimo()
        );

        return inventarioRepository.save(inventario);
    }

    @Transactional
    public Inventario descontarStock(
            Long productoId,
            Integer cantidad) {

        Inventario inventario =
                obtenerPorProductoId(productoId);

        if (inventario.getStock() < cantidad) {

            throw new StockInsuficienteException(
                    "Stock insuficiente para el producto "
                            + productoId
                            + ". Disponible: "
                            + inventario.getStock()
                            + ", solicitado: "
                            + cantidad
            );
        }

        inventario.setStock(
                inventario.getStock() - cantidad
        );

        return inventarioRepository.save(inventario);
    }

    @Transactional
    public Inventario reponerStock(
            Long productoId,
            Integer cantidad) {

        Inventario inventario =
                obtenerPorProductoId(productoId);

        inventario.setStock(
                inventario.getStock() + cantidad
        );

        return inventarioRepository.save(inventario);
    }

    /*
     * Descuenta todos los productos correspondientes
     * a un pedido dentro de una sola transacción.
     *
     * Primero valida todo el stock.
     * Solo si todos los productos tienen disponibilidad
     * se realiza el descuento.
     */
    @Transactional
    public void descontarStockPedido(
            Map<Long, Integer> productos) {

        // Primero validar todo el pedido
        for (Map.Entry<Long, Integer> item
                : productos.entrySet()) {

            Long productoId = item.getKey();
            Integer cantidad = item.getValue();

            Inventario inventario =
                    obtenerPorProductoId(productoId);

            if (inventario.getStock() < cantidad) {

                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto "
                                + productoId
                                + ". Disponible: "
                                + inventario.getStock()
                                + ", solicitado: "
                                + cantidad
                );
            }
        }

        // Si todos tienen stock, realizar descuentos
        for (Map.Entry<Long, Integer> item
                : productos.entrySet()) {

            Inventario inventario =
                    obtenerPorProductoId(
                            item.getKey()
                    );

            Integer stockAnterior = inventario.getStock();

            inventario.setStock(
                    stockAnterior
                            - item.getValue()
            );

            inventarioRepository.save(inventario);

            StockActualizadoEvent evento = new StockActualizadoEvent(
                    item.getKey(),
                    stockAnterior,
                    inventario.getStock(),
                    "PEDIDO"
            );

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            stockActualizadoProducer.publicar(evento);
                        }
                    }
            );
        }
    }
}