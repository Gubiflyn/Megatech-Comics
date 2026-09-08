package cl.megatech.editoriales.controller;

import cl.megatech.editoriales.entity.Autor;
import cl.megatech.editoriales.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    public ResponseEntity<List<Autor>> listarTodos() {
        return ResponseEntity.ok(autorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Autor> crear(
            @Valid @RequestBody Autor autor
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(autorService.guardar(autor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Autor> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Autor autor
    ) {
        return ResponseEntity.ok(
                autorService.actualizar(id, autor)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        autorService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Autor>> buscarPorNombre(
            @RequestParam String nombre
    ) {
        return ResponseEntity.ok(
                autorService.buscarPorNombre(nombre)
        );
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<Autor>> buscarPorTipo(
            @RequestParam String tipo
    ) {
        return ResponseEntity.ok(
                autorService.buscarPorTipo(tipo)
        );
    }

    @GetMapping("/nacionalidad")
    public ResponseEntity<List<Autor>> buscarPorNacionalidad(
            @RequestParam String nacionalidad
    ) {
        return ResponseEntity.ok(
                autorService.buscarPorNacionalidad(nacionalidad)
        );
    }
}