package com.megatech.bffservice.service;

import com.megatech.bffservice.client.DownstreamClient;
import com.megatech.bffservice.dto.inventario.InventarioDTO;
import com.megatech.bffservice.dto.inventario.InventarioRequest;
import com.megatech.bffservice.dto.inventario.StockRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioService {

    private final DownstreamClient downstreamClient;
    private final String baseUrl;

    public InventarioService(DownstreamClient downstreamClient,
                              @Value("${app.services.inventario-url}") String baseUrl) {
        this.downstreamClient = downstreamClient;
        this.baseUrl = baseUrl;
    }

    public List<InventarioDTO> listarTodos() {
        return List.of(downstreamClient.get(baseUrl + "/api/inventario", null, InventarioDTO[].class));
    }

    public InventarioDTO obtenerPorProducto(Long productoId) {
        return downstreamClient.get(baseUrl + "/api/inventario/producto/" + productoId, null, InventarioDTO.class);
    }

    public InventarioDTO crear(InventarioRequest request) {
        return downstreamClient.post(baseUrl + "/api/inventario", request, null, InventarioDTO.class);
    }

    public InventarioDTO actualizar(Long productoId, InventarioRequest request) {
        return downstreamClient.put(baseUrl + "/api/inventario/producto/" + productoId, request, null, InventarioDTO.class);
    }

    public InventarioDTO descontar(Long productoId, StockRequest request) {
        return downstreamClient.post(baseUrl + "/api/inventario/producto/" + productoId + "/descontar", request, null, InventarioDTO.class);
    }

    public InventarioDTO reponer(Long productoId, StockRequest request) {
        return downstreamClient.post(baseUrl + "/api/inventario/producto/" + productoId + "/reponer", request, null, InventarioDTO.class);
    }
}
