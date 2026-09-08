package cl.megatech.pagos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProcesarPagoRequest {

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

    @NotNull(message = "Debe indicar el resultado simulado del pago")
    private Boolean aprobarPago;

    public ProcesarPagoRequest() {
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Boolean getAprobarPago() {
        return aprobarPago;
    }

    public void setAprobarPago(Boolean aprobarPago) {
        this.aprobarPago = aprobarPago;
    }
}