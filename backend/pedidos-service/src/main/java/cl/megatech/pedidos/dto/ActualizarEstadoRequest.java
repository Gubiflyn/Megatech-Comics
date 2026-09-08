package cl.megatech.pedidos.dto;

import jakarta.validation.constraints.NotBlank;

public class ActualizarEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    public ActualizarEstadoRequest() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}