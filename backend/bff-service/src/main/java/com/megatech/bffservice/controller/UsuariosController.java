package com.megatech.bffservice.controller;

import com.megatech.bffservice.dto.usuarios.DireccionRequest;
import com.megatech.bffservice.dto.usuarios.DireccionResponse;
import com.megatech.bffservice.dto.usuarios.UsuarioCreateRequest;
import com.megatech.bffservice.dto.usuarios.UsuarioResponse;
import com.megatech.bffservice.dto.usuarios.UsuarioUpdateRequest;
import com.megatech.bffservice.service.UsuariosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    private final UsuariosService usuariosService;

    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody UsuarioCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuariosService.crearUsuario(request));
    }

    @GetMapping("/{azureOid}")
    public ResponseEntity<UsuarioResponse> buscarPorAzureOid(@PathVariable String azureOid) {
        return ResponseEntity.ok(usuariosService.buscarPorAzureOid(azureOid));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuariosService.actualizarUsuario(id, request));
    }

    @PostMapping("/{id}/direcciones")
    public ResponseEntity<DireccionResponse> agregarDireccion(@PathVariable Long id, @RequestBody DireccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuariosService.agregarDireccion(id, request));
    }

    @GetMapping("/{id}/direcciones")
    public ResponseEntity<List<DireccionResponse>> listarDirecciones(@PathVariable Long id) {
        return ResponseEntity.ok(usuariosService.listarDirecciones(id));
    }
}
