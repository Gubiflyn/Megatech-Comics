package com.megatech.bffservice.service;

import com.megatech.bffservice.client.DownstreamClient;
import com.megatech.bffservice.dto.pagos.PagoDTO;
import com.megatech.bffservice.dto.pagos.ProcesarPagoRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagosService {

    private final DownstreamClient downstreamClient;
    private final String baseUrl;

    public PagosService(DownstreamClient downstreamClient,
                         @Value("${app.services.pagos-url}") String baseUrl) {
        this.downstreamClient = downstreamClient;
        this.baseUrl = baseUrl;
    }

    public PagoDTO procesarPago(Long pedidoId, ProcesarPagoRequest request, String authorizationHeader) {
        return downstreamClient.post(baseUrl + "/api/pagos/pedido/" + pedidoId, request, authorizationHeader, PagoDTO.class);
    }

    public PagoDTO obtenerPago(Long pagoId, String authorizationHeader) {
        return downstreamClient.get(baseUrl + "/api/pagos/" + pagoId, authorizationHeader, PagoDTO.class);
    }

    public List<PagoDTO> obtenerPagosPedido(Long pedidoId, String authorizationHeader) {
        return List.of(downstreamClient.get(baseUrl + "/api/pagos/pedido/" + pedidoId, authorizationHeader, PagoDTO[].class));
    }
}
