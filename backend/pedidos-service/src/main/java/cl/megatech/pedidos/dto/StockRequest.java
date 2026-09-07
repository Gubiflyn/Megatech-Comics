package cl.megatech.pedidos.dto;

public class StockRequest {

    private Integer cantidad;

    public StockRequest() {
    }

    public StockRequest(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}