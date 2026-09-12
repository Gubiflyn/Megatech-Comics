package cl.megatech.catalogo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "comics")
public class Comic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 150, message = "El titulo no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    @Column(length = 1000)
    private String descripcion;

    @NotBlank(message = "El tipo es obligatorio")
    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(length = 100)
    private String edicion;

    @Min(value = 1, message = "El tomo debe ser mayor o igual a 1")
    private Integer tomo;

    @Column(length = 100)
    private String genero;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "El precio debe ser mayor a 0"
    )
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "editorial_id")
    private Long editorialId;

    public Comic() {
    }

    public Comic(
            Long id,
            String titulo,
            String descripcion,
            String tipo,
            String edicion,
            Integer tomo,
            String genero,
            BigDecimal precio,
            Long editorialId
    ) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.edicion = edicion;
        this.tomo = tomo;
        this.genero = genero;
        this.precio = precio;
        this.editorialId = editorialId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEdicion() {
        return edicion;
    }

    public void setEdicion(String edicion) {
        this.edicion = edicion;
    }

    public Integer getTomo() {
        return tomo;
    }

    public void setTomo(Integer tomo) {
        this.tomo = tomo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Long getEditorialId() {
        return editorialId;
    }

    public void setEditorialId(Long editorialId) {
        this.editorialId = editorialId;
    }
}