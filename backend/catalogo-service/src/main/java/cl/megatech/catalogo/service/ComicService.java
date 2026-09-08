package cl.megatech.catalogo.service;

import cl.megatech.catalogo.entity.Comic;
import cl.megatech.catalogo.exception.ComicNotFoundException;
import cl.megatech.catalogo.repository.ComicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComicService {

    private final ComicRepository comicRepository;

    public ComicService(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    public List<Comic> listarTodos() {
        return comicRepository.findAll();
    }

    public Comic buscarPorId(Long id) {
        return comicRepository.findById(id)
                .orElseThrow(() -> new ComicNotFoundException(id));
    }

    public Comic guardar(Comic comic) {
        return comicRepository.save(comic);
    }

    public Comic actualizar(Long id, Comic comicActualizado) {
        Comic comic = buscarPorId(id);

        comic.setTitulo(comicActualizado.getTitulo());
        comic.setDescripcion(comicActualizado.getDescripcion());
        comic.setTipo(comicActualizado.getTipo());
        comic.setEdicion(comicActualizado.getEdicion());
        comic.setTomo(comicActualizado.getTomo());
        comic.setGenero(comicActualizado.getGenero());
        comic.setPrecio(comicActualizado.getPrecio());
        comic.setEditorialId(comicActualizado.getEditorialId());

        return comicRepository.save(comic);
    }

    public void eliminar(Long id) {
        Comic comic = buscarPorId(id);
        comicRepository.delete(comic);
    }

    public List<Comic> buscarPorTitulo(String titulo) {
        return comicRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Comic> buscarPorGenero(String genero) {
        return comicRepository.findByGeneroIgnoreCase(genero);
    }

    public List<Comic> buscarPorTipo(String tipo) {
        return comicRepository.findByTipoIgnoreCase(tipo);
    }
}