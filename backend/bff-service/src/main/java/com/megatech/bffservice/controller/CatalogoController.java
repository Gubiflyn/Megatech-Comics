package com.megatech.bffservice.controller;

import com.megatech.bffservice.dto.catalogo.ComicDTO;
import com.megatech.bffservice.service.CatalogoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping
    public ResponseEntity<List<ComicDTO>> listarTodos() {
        return ResponseEntity.ok(catalogoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComicDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ComicDTO> crear(@RequestBody ComicDTO comic) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crear(comic));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComicDTO> actualizar(@PathVariable Long id, @RequestBody ComicDTO comic) {
        return ResponseEntity.ok(catalogoService.actualizar(id, comic));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        catalogoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ComicDTO>> buscarPorTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(catalogoService.buscarPorTitulo(titulo));
    }

    @GetMapping("/genero")
    public ResponseEntity<List<ComicDTO>> buscarPorGenero(@RequestParam String genero) {
        return ResponseEntity.ok(catalogoService.buscarPorGenero(genero));
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<ComicDTO>> buscarPorTipo(@RequestParam String tipo) {
        return ResponseEntity.ok(catalogoService.buscarPorTipo(tipo));
    }
}
