package com.megatech.usuariosservice.controller;

import com.megatech.usuariosservice.dto.DireccionRequest;
import com.megatech.usuariosservice.dto.DireccionResponse;
import com.megatech.usuariosservice.dto.UsuarioCreateRequest;
import com.megatech.usuariosservice.dto.UsuarioResponse;
import com.megatech.usuariosservice.dto.UsuarioUpdateRequest;
import com.megatech.usuariosservice.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioCreateRequest request) {
        UsuarioResponse response = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{azureOid}")
    public ResponseEntity<UsuarioResponse> buscarPorAzureOid(@PathVariable String azureOid) {
        return ResponseEntity.ok(usuarioService.buscarPorAzureOid(azureOid));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, request));
    }

    @PostMapping("/{id}/direcciones")
    public ResponseEntity<DireccionResponse> agregarDireccion(
            @PathVariable Long id,
            @Valid @RequestBody DireccionRequest request) {
        DireccionResponse response = usuarioService.agregarDireccion(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/direcciones")
    public ResponseEntity<List<DireccionResponse>> listarDirecciones(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.listarDirecciones(id));
    }
}
