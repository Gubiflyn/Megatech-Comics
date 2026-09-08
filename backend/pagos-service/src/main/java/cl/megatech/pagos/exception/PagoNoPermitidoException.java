package cl.megatech.pagos.exception;

public class PagoNoPermitidoException extends RuntimeException {

    public PagoNoPermitidoException(String mensaje) {
        super(mensaje);
    }
}