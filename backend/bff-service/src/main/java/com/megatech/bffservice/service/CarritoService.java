package com.megatech.bffservice.service;

import com.megatech.bffservice.client.DownstreamClient;
import com.megatech.bffservice.dto.carrito.ActualizarCantidadRequest;
import com.megatech.bffservice.dto.carrito.AgregarItemRequest;
import com.megatech.bffservice.dto.carrito.CarritoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CarritoService {

    private final DownstreamClient downstreamClient;
    private final String baseUrl;

    public CarritoService(DownstreamClient downstreamClient,
                           @Value("${app.services.carrito-url}") String baseUrl) {
        this.downstreamClient = downstreamClient;
        this.baseUrl = baseUrl;
    }

    public CarritoDTO obtenerCarrito(String usuarioId, String authorizationHeader) {
        return downstreamClient.get(baseUrl + "/api/carritos/" + usuarioId, authorizationHeader, CarritoDTO.class);
    }

    public CarritoDTO agregarItem(String usuarioId, AgregarItemRequest request, String authorizationHeader) {
        return downstreamClient.post(baseUrl + "/api/carritos/" + usuarioId + "/items", request, authorizationHeader, CarritoDTO.class);
    }

    public CarritoDTO actualizarCantidad(String usuarioId, Long productoId, ActualizarCantidadRequest request, String authorizationHeader) {
        return downstreamClient.put(baseUrl + "/api/carritos/" + usuarioId + "/items/" + productoId, request, authorizationHeader, CarritoDTO.class);
    }

    public CarritoDTO eliminarItem(String usuarioId, Long productoId, String authorizationHeader) {
        return downstreamClient.delete(baseUrl + "/api/carritos/" + usuarioId + "/items/" + productoId, authorizationHeader, CarritoDTO.class);
    }

    public void vaciarCarrito(String usuarioId, String authorizationHeader) {
        downstreamClient.delete(baseUrl + "/api/carritos/" + usuarioId, authorizationHeader);
    }
}
