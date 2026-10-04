package com.megatech.bffservice.dto.catalogo;

import java.math.BigDecimal;

public record ComicDTO(
        Long id,
        String titulo,
        String descripcion,
        String tipo,
        String edicion,
        Integer tomo,
        String genero,
        BigDecimal precio,
        Long editorialId) {
}
