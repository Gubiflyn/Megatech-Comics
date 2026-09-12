package cl.megatech.catalogo.repository;

import cl.megatech.catalogo.entity.Comic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComicRepository extends JpaRepository<Comic, Long> {

    List<Comic> findByTituloContainingIgnoreCase(String titulo);

    List<Comic> findByGeneroIgnoreCase(String genero);

    List<Comic> findByTipoIgnoreCase(String tipo);
}