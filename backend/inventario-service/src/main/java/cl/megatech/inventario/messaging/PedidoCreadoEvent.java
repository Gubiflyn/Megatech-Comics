package cl.megatech.inventario.messaging;

import java.util.ArrayList;
import java.util.List;

public class PedidoCreadoEvent {

    private Long pedidoId;
    private String usuarioId;
    private List<ItemPedidoEvent> items = new ArrayList<>();

    public PedidoCreadoEvent() {
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

    public List<ItemPedidoEvent> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoEvent> items) {
        this.items = items;
    }

    public static class ItemPedidoEvent {

        private Long productoId;
        private Integer cantidad;

        public ItemPedidoEvent() {
        }

        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}