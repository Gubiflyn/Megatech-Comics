package cl.megatech.editoriales.repository;

import cl.megatech.editoriales.entity.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    List<Autor> findByNombreContainingIgnoreCase(String nombre);

    List<Autor> findByTipoIgnoreCase(String tipo);

    List<Autor> findByNacionalidadIgnoreCase(String nacionalidad);
}