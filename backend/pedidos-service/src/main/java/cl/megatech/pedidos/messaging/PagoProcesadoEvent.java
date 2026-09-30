package cl.megatech.pedidos.messaging;

public class PagoProcesadoEvent {

    private Long pagoId;
    private Long pedidoId;
    private String usuarioId;
    private String estado;
    private String metodoPago;

    public PagoProcesadoEvent() {
    }

    public PagoProcesadoEvent(
            Long pagoId,
            Long pedidoId,
            String usuarioId,
            String estado,
            String metodoPago) {

        this.pagoId = pagoId;
        this.pedidoId = pedidoId;
        this.usuarioId = usuarioId;
        this.estado = estado;
        this.metodoPago = metodoPago;
    }

    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long pagoId) {
        this.pagoId = pagoId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}