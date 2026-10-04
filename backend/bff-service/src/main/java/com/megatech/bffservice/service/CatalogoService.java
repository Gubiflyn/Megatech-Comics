package com.megatech.bffservice.service;

import com.megatech.bffservice.client.DownstreamClient;
import com.megatech.bffservice.dto.catalogo.ComicDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class CatalogoService {

    private final DownstreamClient downstreamClient;
    private final String baseUrl;

    public CatalogoService(DownstreamClient downstreamClient,
                            @Value("${app.services.catalogo-url}") String baseUrl) {
        this.downstreamClient = downstreamClient;
        this.baseUrl = baseUrl;
    }

    public List<ComicDTO> listarTodos() {
        return List.of(downstreamClient.get(baseUrl + "/api/comics", null, ComicDTO[].class));
    }

    public ComicDTO buscarPorId(Long id) {
        return downstreamClient.get(baseUrl + "/api/comics/" + id, null, ComicDTO.class);
    }

    public ComicDTO crear(ComicDTO comic) {
        return downstreamClient.post(baseUrl + "/api/comics", comic, null, ComicDTO.class);
    }

    public ComicDTO actualizar(Long id, ComicDTO comic) {
        return downstreamClient.put(baseUrl + "/api/comics/" + id, comic, null, ComicDTO.class);
    }

    public void eliminar(Long id) {
        downstreamClient.delete(baseUrl + "/api/comics/" + id, null);
    }

    public List<ComicDTO> buscarPorTitulo(String titulo) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/comics/buscar")
                .queryParam("titulo", titulo).toUriString();
        return List.of(downstreamClient.get(url, null, ComicDTO[].class));
    }

    public List<ComicDTO> buscarPorGenero(String genero) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/comics/genero")
                .queryParam("genero", genero).toUriString();
        return List.of(downstreamClient.get(url, null, ComicDTO[].class));
    }

    public List<ComicDTO> buscarPorTipo(String tipo) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/comics/tipo")
                .queryParam("tipo", tipo).toUriString();
        return List.of(downstreamClient.get(url, null, ComicDTO[].class));
    }
}
