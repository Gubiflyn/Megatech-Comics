package cl.megatech.catalogo.controller;

import cl.megatech.catalogo.entity.Comic;
import cl.megatech.catalogo.service.ComicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comics")
public class ComicController {

    private final ComicService comicService;

    public ComicController(ComicService comicService) {
        this.comicService = comicService;
    }

    @GetMapping
    public ResponseEntity<List<Comic>> listarTodos() {
        return ResponseEntity.ok(comicService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comic> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(comicService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Comic> crear(@Valid @RequestBody Comic comic) {
        Comic nuevoComic = comicService.guardar(comic);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoComic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comic> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Comic comic
    ) {
        return ResponseEntity.ok(
                comicService.actualizar(id, comic)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        comicService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Comic>> buscarPorTitulo(
            @RequestParam String titulo
    ) {
        return ResponseEntity.ok(
                comicService.buscarPorTitulo(titulo)
        );
    }

    @GetMapping("/genero")
    public ResponseEntity<List<Comic>> buscarPorGenero(
            @RequestParam String genero
    ) {
        return ResponseEntity.ok(
                comicService.buscarPorGenero(genero)
        );
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<Comic>> buscarPorTipo(
            @RequestParam String tipo
    ) {
        return ResponseEntity.ok(
                comicService.buscarPorTipo(tipo)
        );
    }
}