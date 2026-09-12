package com.megatech.bffservice.service;

import com.megatech.bffservice.client.DownstreamClient;
import com.megatech.bffservice.dto.pedidos.ActualizarEstadoRequest;
import com.megatech.bffservice.dto.pedidos.PedidoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidosService {

    private final DownstreamClient downstreamClient;
    private final String baseUrl;

    public PedidosService(DownstreamClient downstreamClient,
                           @Value("${app.services.pedidos-url}") String baseUrl) {
        this.downstreamClient = downstreamClient;
        this.baseUrl = baseUrl;
    }

    public PedidoDTO crearPedido(String usuarioId, String authorizationHeader) {
        return downstreamClient.post(baseUrl + "/api/pedidos/" + usuarioId, authorizationHeader, PedidoDTO.class);
    }

    public PedidoDTO obtenerPedido(Long pedidoId, String authorizationHeader) {
        return downstreamClient.get(baseUrl + "/api/pedidos/" + pedidoId, authorizationHeader, PedidoDTO.class);
    }

    public List<PedidoDTO> obtenerPedidosUsuario(String usuarioId, String authorizationHeader) {
        return List.of(downstreamClient.get(baseUrl + "/api/pedidos/usuario/" + usuarioId, authorizationHeader, PedidoDTO[].class));
    }

    public PedidoDTO actualizarEstado(Long pedidoId, ActualizarEstadoRequest request, String authorizationHeader) {
        return downstreamClient.put(baseUrl + "/api/pedidos/" + pedidoId + "/estado", request, authorizationHeader, PedidoDTO.class);
    }
}
