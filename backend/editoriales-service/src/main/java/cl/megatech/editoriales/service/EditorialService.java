package cl.megatech.editoriales.service;

import cl.megatech.editoriales.entity.Editorial;
import cl.megatech.editoriales.exception.ResourceNotFoundException;
import cl.megatech.editoriales.repository.EditorialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditorialService {

    private final EditorialRepository editorialRepository;

    public EditorialService(EditorialRepository editorialRepository) {
        this.editorialRepository = editorialRepository;
    }

    public List<Editorial> listarTodas() {
        return editorialRepository.findAll();
    }

    public Editorial buscarPorId(Long id) {
        return editorialRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Editorial", id)
                );
    }

    public Editorial guardar(Editorial editorial) {
        return editorialRepository.save(editorial);
    }

    public Editorial actualizar(Long id, Editorial datos) {
        Editorial editorial = buscarPorId(id);

        editorial.setNombre(datos.getNombre());
        editorial.setPais(datos.getPais());
        editorial.setSitioWeb(datos.getSitioWeb());

        return editorialRepository.save(editorial);
    }

    public void eliminar(Long id) {
        Editorial editorial = buscarPorId(id);
        editorialRepository.delete(editorial);
    }

    public List<Editorial> buscarPorNombre(String nombre) {
        return editorialRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Editorial> buscarPorPais(String pais) {
        return editorialRepository.findByPaisIgnoreCase(pais);
    }
}