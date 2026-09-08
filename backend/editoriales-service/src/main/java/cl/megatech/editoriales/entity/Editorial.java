package cl.megatech.editoriales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "editoriales")
public class Editorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la editorial es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Size(max = 100, message = "El pais no puede superar los 100 caracteres")
    @Column(length = 100)
    private String pais;

    @Size(max = 255, message = "El sitio web no puede superar los 255 caracteres")
    @Column(name = "sitio_web")
    private String sitioWeb;

    public Editorial() {
    }

    public Editorial(Long id, String nombre, String pais, String sitioWeb) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.sitioWeb = sitioWeb;
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

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }
}