package cl.megatech.inventario.service;

import cl.megatech.inventario.dto.InventarioRequest;
import cl.megatech.inventario.exception.RecursoNoEncontradoException;
import cl.megatech.inventario.exception.StockInsuficienteException;
import cl.megatech.inventario.model.Inventario;
import cl.megatech.inventario.repository.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(
            InventarioRepository inventarioRepository) {

        this.inventarioRepository = inventarioRepository;
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

            inventario.setStock(
                    inventario.getStock()
                            - item.getValue()
            );

            inventarioRepository.save(inventario);
        }
    }
}