package cl.megatech.rabbitadmin.controller;

import cl.megatech.rabbitadmin.dto.CrearBindingRequest;
import cl.megatech.rabbitadmin.dto.CrearColaRequest;
import cl.megatech.rabbitadmin.dto.CrearExchangeRequest;
import cl.megatech.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rabbitmq")
@RequiredArgsConstructor
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    @PostMapping("/queues")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearCola(@Valid @RequestBody CrearColaRequest request) {
        rabbitAdminService.crearCola(request);
    }

    @DeleteMapping("/queues/{nombre}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCola(@PathVariable String nombre) {
        rabbitAdminService.eliminarCola(nombre);
    }

    @PostMapping("/exchanges")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearExchange(@Valid @RequestBody CrearExchangeRequest request) {
        rabbitAdminService.crearExchange(request);
    }

    @DeleteMapping("/exchanges/{nombre}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarExchange(@PathVariable String nombre) {
        rabbitAdminService.eliminarExchange(nombre);
    }

    @PostMapping("/bindings")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearBinding(@Valid @RequestBody CrearBindingRequest request) {
        rabbitAdminService.crearBinding(request);
    }

    @DeleteMapping("/bindings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarBinding(
            @RequestParam String exchange,
            @RequestParam String cola,
            @RequestParam String routingKey) {
        rabbitAdminService.eliminarBinding(exchange, cola, routingKey);
    }

}
