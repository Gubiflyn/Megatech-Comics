package com.megatech.bffservice.dto.inventario;

public record InventarioRequest(Long productoId, Integer stock, Integer stockMinimo) {
}
