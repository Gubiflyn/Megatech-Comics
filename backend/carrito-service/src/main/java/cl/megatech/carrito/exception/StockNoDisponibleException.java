package cl.megatech.carrito.exception;

public class StockNoDisponibleException extends RuntimeException {

    public StockNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}