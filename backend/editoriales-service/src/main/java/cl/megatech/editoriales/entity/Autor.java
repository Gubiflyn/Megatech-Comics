package cl.megatech.editoriales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    @Column(length = 100)
    private String apellido;

    @NotBlank(message = "El tipo de creador es obligatorio")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String tipo;

    @Size(max = 100)
    @Column(length = 100)
    private String nacionalidad;

    public Autor() {
    }

    public Autor(
            Long id,
            String nombre,
            String apellido,
            String tipo,
            String nacionalidad
    ) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipo = tipo;
        this.nacionalidad = nacionalidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
}