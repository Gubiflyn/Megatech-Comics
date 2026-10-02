package cl.megatech.rabbitadmin.service;

import cl.megatech.rabbitadmin.dto.CrearBindingRequest;
import cl.megatech.rabbitadmin.dto.CrearColaRequest;
import cl.megatech.rabbitadmin.dto.CrearExchangeRequest;
import cl.megatech.rabbitadmin.exception.RecursoNoEncontradoException;
import cl.megatech.rabbitadmin.exception.TipoExchangeInvalidoException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;

    public void crearCola(CrearColaRequest request) {
        Queue queue = new Queue(
                request.getNombre(),
                request.isDurable(),
                request.isExclusive(),
                request.isAutoDelete()
        );
        amqpAdmin.declareQueue(queue);
    }

    public void eliminarCola(String nombre) {
        boolean eliminada = amqpAdmin.deleteQueue(nombre);
        if (!eliminada) {
            throw new RecursoNoEncontradoException("La cola '" + nombre + "' no existe");
        }
    }

    public void crearExchange(CrearExchangeRequest request) {
        Exchange exchange = construirExchange(request);
        amqpAdmin.declareExchange(exchange);
    }

    public void eliminarExchange(String nombre) {
        boolean eliminado = amqpAdmin.deleteExchange(nombre);
        if (!eliminado) {
            throw new RecursoNoEncontradoException("El exchange '" + nombre + "' no existe");
        }
    }

    public void crearBinding(CrearBindingRequest request) {
        Binding binding = BindingBuilder
                .bind(new Queue(request.getCola()))
                .to(new DirectExchange(request.getExchange()))
                .with(request.getRoutingKey());
        amqpAdmin.declareBinding(binding);
    }

    public void eliminarBinding(String exchange, String cola, String routingKey) {
        Binding binding = BindingBuilder
                .bind(new Queue(cola))
                .to(new DirectExchange(exchange))
                .with(routingKey);
        amqpAdmin.removeBinding(binding);
    }

    private Exchange construirExchange(CrearExchangeRequest request) {
        String nombre = request.getNombre();
        boolean durable = request.isDurable();
        boolean autoDelete = request.isAutoDelete();

        return switch (request.getTipo().toLowerCase()) {
            case "direct" -> new DirectExchange(nombre, durable, autoDelete);
            case "fanout" -> new FanoutExchange(nombre, durable, autoDelete);
            case "topic" -> new TopicExchange(nombre, durable, autoDelete);
            case "headers" -> new HeadersExchange(nombre, durable, autoDelete);
            default -> throw new TipoExchangeInvalidoException(
                    "Tipo de exchange inválido: '" + request.getTipo()
                            + "'. Debe ser uno de: direct, fanout, topic, headers");
        };
    }

}
