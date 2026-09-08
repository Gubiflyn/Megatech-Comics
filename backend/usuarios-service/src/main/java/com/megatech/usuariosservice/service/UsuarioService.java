package com.megatech.usuariosservice.service;

import com.megatech.usuariosservice.dto.DireccionRequest;
import com.megatech.usuariosservice.dto.DireccionResponse;
import com.megatech.usuariosservice.dto.UsuarioCreateRequest;
import com.megatech.usuariosservice.dto.UsuarioResponse;
import com.megatech.usuariosservice.dto.UsuarioUpdateRequest;
import com.megatech.usuariosservice.exception.RecursoDuplicadoException;
import com.megatech.usuariosservice.exception.UsuarioNoEncontradoException;
import com.megatech.usuariosservice.model.Direccion;
import com.megatech.usuariosservice.model.Usuario;
import com.megatech.usuariosservice.repository.DireccionRepository;
import com.megatech.usuariosservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;

    public UsuarioResponse crearUsuario(UsuarioCreateRequest request) {
        if (usuarioRepository.existsByAzureOid(request.getAzureOid())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un usuario con azureOid: " + request.getAzureOid());
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un usuario con email: " + request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .azureOid(request.getAzureOid())
                .email(request.getEmail())
                .nombreCompleto(request.getNombreCompleto())
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorAzureOid(String azureOid) {
        Usuario usuario = obtenerPorAzureOidOrThrow(azureOid);
        return UsuarioResponse.fromEntity(usuario);
    }

    public UsuarioResponse actualizarUsuario(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = obtenerPorIdOrThrow(id);

        if (!usuario.getEmail().equals(request.getEmail())
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un usuario con email: " + request.getEmail());
        }

        usuario.setEmail(request.getEmail());
        usuario.setNombreCompleto(request.getNombreCompleto());

        Usuario actualizado = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(actualizado);
    }

    public DireccionResponse agregarDireccion(Long usuarioId, DireccionRequest request) {
        Usuario usuario = obtenerPorIdOrThrow(usuarioId);

        Direccion direccion = Direccion.builder()
                .calle(request.getCalle())
                .comuna(request.getComuna())
                .ciudad(request.getCiudad())
                .esPrincipal(request.isEsPrincipal())
                .usuario(usuario)
                .build();

        usuario.getDirecciones().add(direccion);
        usuarioRepository.save(usuario);

        return DireccionResponse.fromEntity(direccion);
    }

    public void eliminarDireccion(Long usuarioId, Long direccionId) {
        Usuario usuario = obtenerPorIdOrThrow(usuarioId);

        Direccion direccion = usuario.getDirecciones().stream()
                .filter(d -> d.getId().equals(direccionId))
                .findFirst()
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No se encontro la direccion con id " + direccionId + " para el usuario " + usuarioId));

        usuario.getDirecciones().remove(direccion);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<DireccionResponse> listarDirecciones(Long usuarioId) {
        obtenerPorIdOrThrow(usuarioId);
        return direccionRepository.findByUsuarioId(usuarioId).stream()
                .map(DireccionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private Usuario obtenerPorIdOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No se encontro el usuario con id: " + id));
    }

    private Usuario obtenerPorAzureOidOrThrow(String azureOid) {
        return usuarioRepository.findByAzureOid(azureOid)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No se encontro el usuario con azureOid: " + azureOid));
    }
}
