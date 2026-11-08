package cl.megatech.kafkaadmin.controller;

import cl.megatech.kafkaadmin.dto.ActualizarConfigTopicoRequest;
import cl.megatech.kafkaadmin.dto.AumentarParticionesRequest;
import cl.megatech.kafkaadmin.dto.ClusterInfoResponse;
import cl.megatech.kafkaadmin.dto.ConsumerGroupDetalleResponse;
import cl.megatech.kafkaadmin.dto.ConsumerGroupResumen;
import cl.megatech.kafkaadmin.dto.CrearTopicoRequest;
import cl.megatech.kafkaadmin.dto.TopicoInfoResponse;
import cl.megatech.kafkaadmin.service.KafkaAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaAdminController {

    private final KafkaAdminService kafkaAdminService;

    @PostMapping("/topics")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearTopico(@Valid @RequestBody CrearTopicoRequest request) {
        kafkaAdminService.crearTopico(request);
    }

    @GetMapping("/topics")
    public List<String> listarTopicos() {
        return kafkaAdminService.listarTopicos();
    }

    @GetMapping("/topics/{nombre}")
    public TopicoInfoResponse describirTopico(@PathVariable String nombre) {
        return kafkaAdminService.describirTopico(nombre);
    }

    @PutMapping("/topics/{nombre}/config")
    public void actualizarConfig(
            @PathVariable String nombre,
            @Valid @RequestBody ActualizarConfigTopicoRequest request) {

        kafkaAdminService.actualizarConfig(nombre, request);
    }

    @PutMapping("/topics/{nombre}/partitions")
    public void aumentarParticiones(
            @PathVariable String nombre,
            @Valid @RequestBody AumentarParticionesRequest request) {

        kafkaAdminService.aumentarParticiones(nombre, request);
    }

    @DeleteMapping("/topics/{nombre}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarTopico(@PathVariable String nombre) {
        kafkaAdminService.eliminarTopico(nombre);
    }

    @GetMapping("/consumer-groups")
    public List<ConsumerGroupResumen> listarConsumerGroups() {
        return kafkaAdminService.listarConsumerGroups();
    }

    @GetMapping("/consumer-groups/{id}")
    public ConsumerGroupDetalleResponse describirConsumerGroup(@PathVariable String id) {
        return kafkaAdminService.describirConsumerGroup(id);
    }

    @DeleteMapping("/consumer-groups/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarConsumerGroup(@PathVariable String id) {
        kafkaAdminService.eliminarConsumerGroup(id);
    }

    @GetMapping("/cluster")
    public ClusterInfoResponse obtenerInfoCluster() {
        return kafkaAdminService.obtenerInfoCluster();
    }

}
