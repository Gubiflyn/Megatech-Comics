package cl.megatech.catalogo.messaging;

public class StockActualizadoEvent {

    private Long productoId;
    private Integer stockAnterior;
    private Integer stockActual;
    private String motivo;

    public StockActualizadoEvent() {
    }

    public StockActualizadoEvent(Long productoId, Integer stockAnterior,
                                 Integer stockActual, String motivo) {
        this.productoId = productoId;
        this.stockAnterior = stockAnterior;
        this.stockActual = stockActual;
        this.motivo = motivo;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(Integer stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
