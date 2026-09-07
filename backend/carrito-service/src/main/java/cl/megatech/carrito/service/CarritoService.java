package cl.megatech.carrito.service;

import cl.megatech.carrito.dto.AgregarItemRequest;
import cl.megatech.carrito.exception.RecursoNoEncontradoException;
import cl.megatech.carrito.model.Carrito;
import cl.megatech.carrito.model.ItemCarrito;
import cl.megatech.carrito.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;

    public CarritoService(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    public Carrito obtenerPorUsuario(String usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "El usuario no tiene un carrito creado"
                        )
                );
    }

    @Transactional
    public Carrito agregarItem(
            String usuarioId,
            AgregarItemRequest request) {

        Carrito carrito = carritoRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(() -> crearCarrito(usuarioId));

        ItemCarrito existente = carrito.getItems()
                .stream()
                .filter(item ->
                        item.getProductoId()
                                .equals(request.getProductoId())
                )
                .findFirst()
                .orElse(null);

        if (existente != null) {
            existente.setCantidad(
                    existente.getCantidad()
                            + request.getCantidad()
            );
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();

            nuevoItem.setProductoId(
                    request.getProductoId()
            );

            nuevoItem.setCantidad(
                    request.getCantidad()
            );

            carrito.agregarItem(nuevoItem);
        }

        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito actualizarCantidad(
            String usuarioId,
            Long productoId,
            Integer cantidad) {

        Carrito carrito = obtenerPorUsuario(usuarioId);

        ItemCarrito item = buscarItem(
                carrito,
                productoId
        );

        item.setCantidad(cantidad);

        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito eliminarItem(
            String usuarioId,
            Long productoId) {

        Carrito carrito = obtenerPorUsuario(usuarioId);

        ItemCarrito item = buscarItem(
                carrito,
                productoId
        );

        carrito.eliminarItem(item);

        return carritoRepository.save(carrito);
    }

    @Transactional
    public void vaciarCarrito(String usuarioId) {

        Carrito carrito = obtenerPorUsuario(usuarioId);

        carrito.getItems().clear();

        carritoRepository.save(carrito);
    }

    private Carrito crearCarrito(String usuarioId) {

        Carrito carrito = new Carrito();

        carrito.setUsuarioId(usuarioId);
        carrito.setEstado("ACTIVO");

        return carritoRepository.save(carrito);
    }

    private ItemCarrito buscarItem(
            Carrito carrito,
            Long productoId) {

        return carrito.getItems()
                .stream()
                .filter(item ->
                        item.getProductoId()
                                .equals(productoId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "El producto "
                                        + productoId
                                        + " no está en el carrito"
                        )
                );
    }
}