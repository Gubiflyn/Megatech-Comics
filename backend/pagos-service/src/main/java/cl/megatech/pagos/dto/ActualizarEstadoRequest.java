package cl.megatech.pagos.dto;

public class ActualizarEstadoRequest {

    private String estado;

    public ActualizarEstadoRequest() {
    }

    public ActualizarEstadoRequest(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}