package cl.megatech.pagos.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    @Column(name = "codigo_transaccion", nullable = false, unique = true)
    private String codigoTransaccion;

    @Column(nullable = false)
    private String mensaje;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Pago() {
    }

    @PrePersist
    public void prepararPago() {

        if (codigoTransaccion == null) {
            codigoTransaccion = UUID.randomUUID().toString();
        }

        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
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

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public String getCodigoTransaccion() {
        return codigoTransaccion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}