package com.megatech.bffservice.service;

import com.megatech.bffservice.client.DownstreamClient;
import com.megatech.bffservice.dto.usuarios.DireccionRequest;
import com.megatech.bffservice.dto.usuarios.DireccionResponse;
import com.megatech.bffservice.dto.usuarios.UsuarioCreateRequest;
import com.megatech.bffservice.dto.usuarios.UsuarioResponse;
import com.megatech.bffservice.dto.usuarios.UsuarioUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuariosService {

    private final DownstreamClient downstreamClient;
    private final String baseUrl;

    public UsuariosService(DownstreamClient downstreamClient,
                            @Value("${app.services.usuarios-url}") String baseUrl) {
        this.downstreamClient = downstreamClient;
        this.baseUrl = baseUrl;
    }

    public UsuarioResponse crearUsuario(UsuarioCreateRequest request) {
        return downstreamClient.post(baseUrl + "/api/usuarios", request, null, UsuarioResponse.class);
    }

    public UsuarioResponse buscarPorAzureOid(String azureOid) {
        return downstreamClient.get(baseUrl + "/api/usuarios/" + azureOid, null, UsuarioResponse.class);
    }

    public UsuarioResponse actualizarUsuario(Long id, UsuarioUpdateRequest request) {
        return downstreamClient.put(baseUrl + "/api/usuarios/" + id, request, null, UsuarioResponse.class);
    }

    public DireccionResponse agregarDireccion(Long id, DireccionRequest request) {
        return downstreamClient.post(baseUrl + "/api/usuarios/" + id + "/direcciones", request, null, DireccionResponse.class);
    }

    public List<DireccionResponse> listarDirecciones(Long id) {
        return List.of(downstreamClient.get(baseUrl + "/api/usuarios/" + id + "/direcciones", null, DireccionResponse[].class));
    }
}
