package cl.megatech.pedidos.exception;

public class CarritoVacioException extends RuntimeException {

    public CarritoVacioException(String mensaje) {
        super(mensaje);
    }
}