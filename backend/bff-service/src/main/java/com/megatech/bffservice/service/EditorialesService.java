package com.megatech.bffservice.service;

import com.megatech.bffservice.client.DownstreamClient;
import com.megatech.bffservice.dto.editoriales.AutorDTO;
import com.megatech.bffservice.dto.editoriales.EditorialDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class EditorialesService {

    private final DownstreamClient downstreamClient;
    private final String baseUrl;

    public EditorialesService(DownstreamClient downstreamClient,
                               @Value("${app.services.editoriales-url}") String baseUrl) {
        this.downstreamClient = downstreamClient;
        this.baseUrl = baseUrl;
    }

    // ----- Autores -----

    public List<AutorDTO> listarAutores() {
        return List.of(downstreamClient.get(baseUrl + "/api/autores", null, AutorDTO[].class));
    }

    public AutorDTO buscarAutorPorId(Long id) {
        return downstreamClient.get(baseUrl + "/api/autores/" + id, null, AutorDTO.class);
    }

    public AutorDTO crearAutor(AutorDTO autor) {
        return downstreamClient.post(baseUrl + "/api/autores", autor, null, AutorDTO.class);
    }

    public AutorDTO actualizarAutor(Long id, AutorDTO autor) {
        return downstreamClient.put(baseUrl + "/api/autores/" + id, autor, null, AutorDTO.class);
    }

    public void eliminarAutor(Long id) {
        downstreamClient.delete(baseUrl + "/api/autores/" + id, null);
    }

    public List<AutorDTO> buscarAutorPorNombre(String nombre) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/autores/buscar")
                .queryParam("nombre", nombre).toUriString();
        return List.of(downstreamClient.get(url, null, AutorDTO[].class));
    }

    public List<AutorDTO> buscarAutorPorTipo(String tipo) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/autores/tipo")
                .queryParam("tipo", tipo).toUriString();
        return List.of(downstreamClient.get(url, null, AutorDTO[].class));
    }

    public List<AutorDTO> buscarAutorPorNacionalidad(String nacionalidad) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/autores/nacionalidad")
                .queryParam("nacionalidad", nacionalidad).toUriString();
        return List.of(downstreamClient.get(url, null, AutorDTO[].class));
    }

    // ----- Editoriales -----

    public List<EditorialDTO> listarEditoriales() {
        return List.of(downstreamClient.get(baseUrl + "/api/editoriales", null, EditorialDTO[].class));
    }

    public EditorialDTO buscarEditorialPorId(Long id) {
        return downstreamClient.get(baseUrl + "/api/editoriales/" + id, null, EditorialDTO.class);
    }

    public EditorialDTO crearEditorial(EditorialDTO editorial) {
        return downstreamClient.post(baseUrl + "/api/editoriales", editorial, null, EditorialDTO.class);
    }

    public EditorialDTO actualizarEditorial(Long id, EditorialDTO editorial) {
        return downstreamClient.put(baseUrl + "/api/editoriales/" + id, editorial, null, EditorialDTO.class);
    }

    public void eliminarEditorial(Long id) {
        downstreamClient.delete(baseUrl + "/api/editoriales/" + id, null);
    }

    public List<EditorialDTO> buscarEditorialPorNombre(String nombre) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/editoriales/buscar")
                .queryParam("nombre", nombre).toUriString();
        return List.of(downstreamClient.get(url, null, EditorialDTO[].class));
    }

    public List<EditorialDTO> buscarEditorialPorPais(String pais) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/editoriales/pais")
                .queryParam("pais", pais).toUriString();
        return List.of(downstreamClient.get(url, null, EditorialDTO[].class));
    }
}
