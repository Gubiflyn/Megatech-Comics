package cl.megatech.editoriales.service;

import cl.megatech.editoriales.entity.Autor;
import cl.megatech.editoriales.exception.ResourceNotFoundException;
import cl.megatech.editoriales.repository.AutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public List<Autor> listarTodos() {
        return autorRepository.findAll();
    }

    public Autor buscarPorId(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Autor", id)
                );
    }

    public Autor guardar(Autor autor) {
        return autorRepository.save(autor);
    }

    public Autor actualizar(Long id, Autor datos) {
        Autor autor = buscarPorId(id);

        autor.setNombre(datos.getNombre());
        autor.setApellido(datos.getApellido());
        autor.setTipo(datos.getTipo());
        autor.setNacionalidad(datos.getNacionalidad());

        return autorRepository.save(autor);
    }

    public void eliminar(Long id) {
        Autor autor = buscarPorId(id);
        autorRepository.delete(autor);
    }

    public List<Autor> buscarPorNombre(String nombre) {
        return autorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Autor> buscarPorTipo(String tipo) {
        return autorRepository.findByTipoIgnoreCase(tipo);
    }

    public List<Autor> buscarPorNacionalidad(String nacionalidad) {
        return autorRepository.findByNacionalidadIgnoreCase(nacionalidad);
    }
}