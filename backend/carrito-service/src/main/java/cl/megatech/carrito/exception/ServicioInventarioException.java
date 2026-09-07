package cl.megatech.carrito.exception;

public class ServicioInventarioException extends RuntimeException {

    public ServicioInventarioException(String mensaje) {
        super(mensaje);
    }
}