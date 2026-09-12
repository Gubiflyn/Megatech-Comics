package cl.megatech.carrito.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "carritos",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "usuario_id")
    }
)
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private String usuarioId;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @OneToMany(
        mappedBy = "carrito",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ItemCarrito> items = new ArrayList<>();

    public Carrito() {
    }

    @PrePersist
    public void crearFechas() {
        LocalDateTime ahora = LocalDateTime.now();
        this.fechaCreacion = ahora;
        this.fechaActualizacion = ahora;

        if (this.estado == null) {
            this.estado = "ACTIVO";
        }
    }

    @PreUpdate
    public void actualizarFecha() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void agregarItem(ItemCarrito item) {
        items.add(item);
        item.setCarrito(this);
    }

    public void eliminarItem(ItemCarrito item) {
        items.remove(item);
        item.setCarrito(null);
    }

    public Long getId() {
        return id;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }
}