package com.megatech.bffservice.controller;

import com.megatech.bffservice.dto.editoriales.AutorDTO;
import com.megatech.bffservice.dto.editoriales.EditorialDTO;
import com.megatech.bffservice.service.EditorialesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editoriales")
public class EditorialesController {

    private final EditorialesService editorialesService;

    public EditorialesController(EditorialesService editorialesService) {
        this.editorialesService = editorialesService;
    }

    // ----- Autores: /api/editoriales/autores -----

    @GetMapping("/autores")
    public ResponseEntity<List<AutorDTO>> listarAutores() {
        return ResponseEntity.ok(editorialesService.listarAutores());
    }

    @GetMapping("/autores/{id}")
    public ResponseEntity<AutorDTO> buscarAutorPorId(@PathVariable Long id) {
        return ResponseEntity.ok(editorialesService.buscarAutorPorId(id));
    }

    @PostMapping("/autores")
    public ResponseEntity<AutorDTO> crearAutor(@RequestBody AutorDTO autor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(editorialesService.crearAutor(autor));
    }

    @PutMapping("/autores/{id}")
    public ResponseEntity<AutorDTO> actualizarAutor(@PathVariable Long id, @RequestBody AutorDTO autor) {
        return ResponseEntity.ok(editorialesService.actualizarAutor(id, autor));
    }

    @DeleteMapping("/autores/{id}")
    public ResponseEntity<Void> eliminarAutor(@PathVariable Long id) {
        editorialesService.eliminarAutor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/autores/buscar")
    public ResponseEntity<List<AutorDTO>> buscarAutorPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(editorialesService.buscarAutorPorNombre(nombre));
    }

    @GetMapping("/autores/tipo")
    public ResponseEntity<List<AutorDTO>> buscarAutorPorTipo(@RequestParam String tipo) {
        return ResponseEntity.ok(editorialesService.buscarAutorPorTipo(tipo));
    }

    @GetMapping("/autores/nacionalidad")
    public ResponseEntity<List<AutorDTO>> buscarAutorPorNacionalidad(@RequestParam String nacionalidad) {
        return ResponseEntity.ok(editorialesService.buscarAutorPorNacionalidad(nacionalidad));
    }

    // ----- Editoriales: /api/editoriales/editoriales -----

    @GetMapping("/editoriales")
    public ResponseEntity<List<EditorialDTO>> listarEditoriales() {
        return ResponseEntity.ok(editorialesService.listarEditoriales());
    }

    @GetMapping("/editoriales/{id}")
    public ResponseEntity<EditorialDTO> buscarEditorialPorId(@PathVariable Long id) {
        return ResponseEntity.ok(editorialesService.buscarEditorialPorId(id));
    }

    @PostMapping("/editoriales")
    public ResponseEntity<EditorialDTO> crearEditorial(@RequestBody EditorialDTO editorial) {
        return ResponseEntity.status(HttpStatus.CREATED).body(editorialesService.crearEditorial(editorial));
    }

    @PutMapping("/editoriales/{id}")
    public ResponseEntity<EditorialDTO> actualizarEditorial(@PathVariable Long id, @RequestBody EditorialDTO editorial) {
        return ResponseEntity.ok(editorialesService.actualizarEditorial(id, editorial));
    }

    @DeleteMapping("/editoriales/{id}")
    public ResponseEntity<Void> eliminarEditorial(@PathVariable Long id) {
        editorialesService.eliminarEditorial(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/editoriales/buscar")
    public ResponseEntity<List<EditorialDTO>> buscarEditorialPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(editorialesService.buscarEditorialPorNombre(nombre));
    }

    @GetMapping("/editoriales/pais")
    public ResponseEntity<List<EditorialDTO>> buscarEditorialPorPais(@RequestParam String pais) {
        return ResponseEntity.ok(editorialesService.buscarEditorialPorPais(pais));
    }
}
