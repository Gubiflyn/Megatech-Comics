package cl.megatech.editoriales.repository;

import cl.megatech.editoriales.entity.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EditorialRepository extends JpaRepository<Editorial, Long> {

    List<Editorial> findByNombreContainingIgnoreCase(String nombre);

    List<Editorial> findByPaisIgnoreCase(String pais);
}