package cl.megatech.editoriales.controller;

import cl.megatech.editoriales.entity.Editorial;
import cl.megatech.editoriales.service.EditorialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editoriales")
public class EditorialController {

    private final EditorialService editorialService;

    public EditorialController(EditorialService editorialService) {
        this.editorialService = editorialService;
    }

    @GetMapping
    public ResponseEntity<List<Editorial>> listarTodas() {
        return ResponseEntity.ok(editorialService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Editorial> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(editorialService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Editorial> crear(
            @Valid @RequestBody Editorial editorial
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(editorialService.guardar(editorial));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Editorial> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Editorial editorial
    ) {
        return ResponseEntity.ok(
                editorialService.actualizar(id, editorial)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        editorialService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Editorial>> buscarPorNombre(
            @RequestParam String nombre
    ) {
        return ResponseEntity.ok(
                editorialService.buscarPorNombre(nombre)
        );
    }

    @GetMapping("/pais")
    public ResponseEntity<List<Editorial>> buscarPorPais(
            @RequestParam String pais
    ) {
        return ResponseEntity.ok(
                editorialService.buscarPorPais(pais)
        );
    }
}